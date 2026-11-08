package consulta.persistencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.servico.AgendaService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Salva e restaura os dados em JSON legível, sem banco de dados ou ORM. */
public class ArquivoAgendaJson {
    private final Path caminho;
    private final ObjectMapper mapper = new ObjectMapper();

    public ArquivoAgendaJson(Path caminho) {
        if (caminho == null) {
            throw new IllegalArgumentException("O caminho do arquivo é obrigatório.");
        }
        this.caminho = caminho;
    }

    public void salvar(AgendaService agenda) {
        try {
            ObjectNode raiz = mapper.createObjectNode();
            ArrayNode pacientes = raiz.putArray("pacientes");
            for (Paciente p : agenda.listarPacientes()) {
                ObjectNode n = pacientes.addObject();
                n.put("id", p.getId().toString()); n.put("nome", p.getNome());
                n.put("cpf", p.getCpf()); n.put("telefone", p.getTelefone());
            }
            ArrayNode profissionais = raiz.putArray("profissionais");
            for (Profissional p : agenda.listarTodosProfissionais()) {
                ObjectNode n = profissionais.addObject();
                n.put("id", p.getId().toString()); n.put("nome", p.getNome());
                n.put("especialidade", p.getEspecialidade().name()); n.put("ativo", p.isAtivo());
                n.put("valorConsulta", p.getValorConsulta().toPlainString());
            }
            ArrayNode horarios = raiz.putArray("horarios");
            for (AgendaService.HorarioSalvo h : agenda.exportarHorarios()) {
                ObjectNode n = horarios.addObject(); n.put("profissionalId", h.profissionalId().toString());
                n.put("inicio", h.periodo().inicio().toString()); n.put("fim", h.periodo().fim().toString());
            }
            ArrayNode consultas = raiz.putArray("consultas");
            for (Consulta c : agenda.listarConsultas()) {
                ObjectNode n = consultas.addObject();
                n.put("id", c.getId().toString()); n.put("pacienteId", c.getPaciente().getId().toString());
                n.put("profissionalId", c.getProfissional().getId().toString());
                n.put("inicio", c.getPeriodo().inicio().toString()); n.put("fim", c.getPeriodo().fim().toString());
                n.put("status", c.getStatus().name());
                if (c.getValorConsulta() != null) {
                    n.put("precoBase", c.getValorConsulta().precoBase().toPlainString());
                    n.put("valorFinal", c.getValorConsulta().valorComDesconto().toPlainString());
                    n.put("promocao", c.getValorConsulta().promocao());
                }
                if (c.getConsultaAnterior() != null) {
                    n.put("consultaAnteriorId", c.getConsultaAnterior().getId().toString());
                }
            }
            Path pasta = caminho.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(caminho.toFile(), raiz);
        } catch (IOException e) {
            throw new DominioException("Não foi possível salvar os dados em JSON.", e);
        }
    }

    public AgendaService carregar() {
        AgendaService agenda = new AgendaService();
        if (!Files.exists(caminho)) {
            return agenda;
        }
        try {
            JsonNode raiz = mapper.readTree(caminho.toFile());
            Map<UUID, Paciente> pacientes = new HashMap<>();
            Map<UUID, Profissional> profissionais = new HashMap<>();
            Map<UUID, Boolean> profissionaisAtivos = new HashMap<>();
            Map<UUID, Consulta> consultas = new HashMap<>();
            for (JsonNode n : raiz.path("pacientes")) {
                Paciente p = new Paciente(UUID.fromString(n.get("id").asText()), n.get("nome").asText(),
                        n.get("cpf").asText(), n.path("telefone").asText(""));
                agenda.restaurarPaciente(p); pacientes.put(p.getId(), p);
            }
            for (JsonNode n : raiz.path("profissionais")) {
                Profissional p = new Profissional(UUID.fromString(n.get("id").asText()), n.get("nome").asText(),
                        EspecialidadeEnum.valueOf(n.get("especialidade").asText()), new BigDecimal(n.get("valorConsulta").asText()));
                profissionaisAtivos.put(p.getId(), n.path("ativo").asBoolean(true));
                agenda.restaurarProfissional(p); profissionais.put(p.getId(), p);
            }
            for (JsonNode n : raiz.path("horarios")) {
                Profissional p = profissionais.get(UUID.fromString(n.get("profissionalId").asText()));
                if (p == null) {
                    throw new DominioException("Horário referencia médico inexistente.");
                }
                agenda.restaurarHorario(p, new PeriodoRecord(
                        LocalDateTime.parse(n.get("inicio").asText()), LocalDateTime.parse(n.get("fim").asText())));
            }
            for (JsonNode n : raiz.path("consultas")) {
                Paciente p = pacientes.get(UUID.fromString(n.get("pacienteId").asText()));
                Profissional profissional = profissionais.get(UUID.fromString(n.get("profissionalId").asText()));
                if (p == null || profissional == null) {
                    throw new DominioException("Consulta salva referencia cadastro inexistente.");
                }
                Consulta c = new Consulta(UUID.fromString(n.get("id").asText()), p, profissional,
                        new PeriodoRecord(LocalDateTime.parse(n.get("inicio").asText()), LocalDateTime.parse(n.get("fim").asText())));
                if (n.has("precoBase")) c.restaurarValor(new ValorConsultaRecord(new BigDecimal(n.get("precoBase").asText()),
                        new BigDecimal(n.get("valorFinal").asText()), n.path("promocao").asText("Sem promoção")));
                StatusConsultaEnum status = StatusConsultaEnum.valueOf(n.get("status").asText());
                if (status == StatusConsultaEnum.CANCELADA) {
                    c.cancelar();
                }
                if (status == StatusConsultaEnum.CONCLUIDA) {
                    c.concluir();
                }
                agenda.restaurarConsulta(c); consultas.put(c.getId(), c);
            }
            // Profissionais são desativados somente depois de restaurar as consultas antigas.
            profissionais.forEach((id, profissional) -> {
                if (!profissionaisAtivos.getOrDefault(id, true)) {
                    profissional.desativar();
                }
            });
            for (JsonNode n : raiz.path("consultas")) {
                if (n.has("consultaAnteriorId")) {
                    Consulta atual = consultas.get(UUID.fromString(n.get("id").asText()));
                    Consulta anterior = consultas.get(UUID.fromString(n.get("consultaAnteriorId").asText()));
                    if (atual == null || anterior == null || anterior.getStatus() != StatusConsultaEnum.CONCLUIDA)
                        throw new DominioException("Vínculo de retorno inválido no arquivo JSON.");
                    anterior.vincularProxima(atual);
                }
            }
            return agenda;
        } catch (IOException | RuntimeException e) {
            throw new DominioException("O arquivo JSON está inválido ou contém dados inconsistentes.", e);
        }
    }
}
