package consulta.servico;

import consulta.dominio.*;
import consulta.repositorio.RepositorioEmMemoria;
import java.util.UUID;

public class RelatorioService {
    private final RepositorioEmMemoria<Paciente, UUID> pacientes;
    private final RepositorioEmMemoria<Profissional, UUID> profissionais;
    private final RepositorioEmMemoria<Consulta, UUID> consultas;

    public RelatorioService(AgendaDados dados) {
        this.pacientes = dados.pacientes;
        this.profissionais = dados.profissionais;
        this.consultas = dados.consultas;
    }

    public String gerarRelatorio() {
        long agendadas = consultas.listarTodos().stream().filter(c -> c.getStatus() == StatusConsultaEnum.AGENDADA).count();
        long concluidas = consultas.listarTodos().stream().filter(c -> c.getStatus() == StatusConsultaEnum.CONCLUIDA).count();
        long canceladas = consultas.listarTodos().stream().filter(c -> c.getStatus() == StatusConsultaEnum.CANCELADA).count();
        java.math.BigDecimal total = consultas.listarTodos().stream()
                .filter(c -> c.getStatus() != StatusConsultaEnum.CANCELADA && c.getValorConsulta() != null)
                .map(c -> c.getValorConsulta().valorComDesconto()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return "RELATÓRIO DA AGENDA\nPacientes: " + pacientes.listarTodos().size()
                + "\nProfissionais: " + profissionais.listarTodos().size()
                + "\nConsultas agendadas: " + agendadas + "\nConsultas concluídas: " + concluidas
                + "\nConsultas canceladas: " + canceladas + "\nTotal previsto/realizado: R$ " + total.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
