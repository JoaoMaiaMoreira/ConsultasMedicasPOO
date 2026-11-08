package consulta.servico;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.repositorio.RepositorioEmMemoria;
import consulta.estrategia.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ConsultaService {
    private final RepositorioEmMemoria<Paciente, UUID> pacientes;
    private final RepositorioEmMemoria<Profissional, UUID> profissionais;
    private final RepositorioEmMemoria<Consulta, UUID> consultas;
    private final List<AgendaDados.HorarioDisponivel> horarios;

    public ConsultaService(AgendaDados dados) {
        this.pacientes = dados.pacientes;
        this.profissionais = dados.profissionais;
        this.consultas = dados.consultas;
        this.horarios = dados.horarios;
    }

    public List<Consulta> listarConsultas() {
        return consultas.listarTodos();
    }

    public Consulta agendar(Paciente paciente, Profissional profissional, PeriodoRecord periodo) {
        if (paciente == null || pacientes.buscarPorId(paciente.getId()).isEmpty()) {
            throw new DominioException("Paciente não cadastrado.");
        }
        exigirProfissionalCadastrado(profissional);
        if (periodo == null) {
            throw new DominioException("O horário escolhido é obrigatório.");
        }
        boolean horarioExiste = horarios.stream().anyMatch(h -> h.profissional().equals(profissional) && h.periodo().equals(periodo));
        if (!horarioExiste) {
            throw new DominioException("Esse horário não foi disponibilizado pelo médico.");
        }
        boolean conflito = consultas.listarTodos().stream().filter(c -> c.getStatus() == StatusConsultaEnum.AGENDADA)
                .anyMatch(c -> (c.getProfissional().equals(profissional) || c.getPaciente().equals(paciente)) && c.getPeriodo().sobrepoe(periodo));
        if (conflito) {
            throw new DominioException("Horário indisponível: existe outra consulta conflitante.");
        }
        Consulta consulta = new Consulta(UUID.randomUUID(), paciente, profissional, periodo);
        consulta.aplicarCalculoPreco(new PrecoFixoStrategy(profissional.getValorConsulta()));
        consultas.salvar(consulta);
        return consulta;
    }

    public Consulta agendarRetorno(UUID consultaAnteriorId, PeriodoRecord periodo) {
        Consulta anterior = consultas.buscarPorId(consultaAnteriorId)
                .orElseThrow(() -> new DominioException("Consulta anterior não encontrada."));
        if (anterior.getStatus() != StatusConsultaEnum.CONCLUIDA) {
            throw new DominioException("O retorno só pode ser agendado após a conclusão da consulta anterior.");
        }
        if (anterior.getProximaConsulta() != null) {
            throw new DominioException("Essa consulta já possui um retorno agendado.");
        }
        Consulta retorno = agendar(anterior.getPaciente(), anterior.getProfissional(), periodo);
        try {
            anterior.vincularProxima(retorno);
            retorno.aplicarCalculoPreco(new PrecoDescontoRetornoStrategy(anterior.getProfissional().getValorConsulta(), new java.math.BigDecimal("10")));
            return retorno;
        } catch (RuntimeException e) {
            consultas.remover(retorno.getId());
            throw e;
        }
    }

    public void concluirConsulta(UUID id) {
        Consulta consulta = consultas.buscarPorId(id).orElseThrow(() -> new DominioException("Consulta não encontrada."));
        consulta.concluir();
    }

    public void cancelarConsulta(UUID id, Paciente paciente) {
        cancelarConsulta(id, paciente, new CancelamentoLivreStrategy(), LocalDateTime.now());
    }

    public void cancelarConsulta(UUID id, Paciente paciente, PoliticaCancelamento politica, LocalDateTime momento) {
        Consulta consulta = consultas.buscarPorId(id).orElseThrow(() -> new DominioException("Consulta não encontrada."));
        if (paciente == null || !consulta.getPaciente().equals(paciente)) {
            throw new DominioException("Somente o paciente responsável pode cancelar esta consulta.");
        }
        if (politica == null) {
            throw new DominioException("A política de cancelamento é obrigatória.");
        }
        politica.validarCancelamento(consulta, momento);
        consulta.cancelar();
    }

    public List<Consulta> listarConsultasDoPaciente(Paciente paciente) {
        return consultas.listarTodos().stream().filter(c -> c.getPaciente().equals(paciente))
                .sorted(Comparator.comparing(c -> c.getPeriodo().inicio())).toList();
    }

    public List<Consulta> listarConsultasDoProfissional(Profissional profissional) {
        return consultas.listarTodos().stream().filter(c -> c.getProfissional().equals(profissional))
                .sorted(Comparator.comparing(c -> c.getPeriodo().inicio())).toList();
    }

    public List<Consulta> consultar(java.util.function.Predicate<Consulta> filtro) {
        if (filtro == null) {
            throw new DominioException("O filtro é obrigatório.");
        }
        return consultas.listarTodos().stream().filter(filtro)
                .sorted(Comparator.comparing(c -> c.getPeriodo().inicio())).toList();
    }

    public void restaurarConsulta(Consulta c) {
        if (c == null || consultas.buscarPorId(c.getId()).isPresent()) {
            throw new DominioException("Consulta duplicada ou inválida no arquivo JSON.");
        }
        if (pacientes.buscarPorId(c.getPaciente().getId()).isEmpty()
                || profissionais.buscarPorId(c.getProfissional().getId()).isEmpty()) {
            throw new DominioException("Consulta referencia paciente ou profissional inexistente.");
        }
        boolean horarioExiste = horarios.stream().anyMatch(h -> h.profissional().equals(c.getProfissional())
                && h.periodo().equals(c.getPeriodo()));
        if (!horarioExiste) {
            throw new DominioException("Consulta referencia horário não cadastrado.");
        }
        if (c.getStatus() == StatusConsultaEnum.AGENDADA) {
            boolean conflito = consultas.listarTodos().stream().filter(outra -> outra.getStatus() == StatusConsultaEnum.AGENDADA)
                    .anyMatch(outra -> (outra.getProfissional().equals(c.getProfissional())
                            || outra.getPaciente().equals(c.getPaciente())) && outra.getPeriodo().sobrepoe(c.getPeriodo()));
            if (conflito) {
                throw new DominioException("O arquivo JSON contém consultas conflitantes.");
            }
        }
        consultas.salvar(c);
    }

    private void exigirProfissionalCadastrado(Profissional profissional) {
        if (profissional == null || profissionais.buscarPorId(profissional.getId()).isEmpty()) {
            throw new DominioException("Médico não cadastrado.");
        }
    }
}
