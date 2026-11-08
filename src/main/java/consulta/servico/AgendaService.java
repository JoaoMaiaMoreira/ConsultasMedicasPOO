package consulta.servico;

import consulta.dominio.*;
import consulta.estrategia.PoliticaCancelamento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Ponto de entrada da agenda. Cada responsabilidade fica em um serviço menor.
 * A fachada mantém os métodos usados no menu, nos testes e na persistência JSON.
 */
public class AgendaService {
    private final AgendaDados dados = new AgendaDados();
    private final PacienteService pacienteService = new PacienteService(dados);
    private final ProfissionalService profissionalService = new ProfissionalService(dados);
    private final HorarioService horarioService = new HorarioService(dados);
    private final ConsultaService consultaService = new ConsultaService(dados);
    private final RelatorioService relatorioService = new RelatorioService(dados);

    public record HorarioSalvo(UUID profissionalId, PeriodoRecord periodo) {
    }

    public void cadastrarPaciente(Paciente paciente) {
        pacienteService.cadastrarPaciente(paciente);
    }

    public List<Paciente> listarPacientes() {
        return pacienteService.listarPacientes();
    }

    public void restaurarPaciente(Paciente paciente) {
        pacienteService.restaurarPaciente(paciente);
    }

    public void cadastrarProfissional(Profissional profissional) {
        profissionalService.cadastrarProfissional(profissional);
    }

    public List<Profissional> listarTodosProfissionais() {
        return profissionalService.listarTodosProfissionais();
    }

    public List<Profissional> listarProfissionais() {
        return profissionalService.listarProfissionais();
    }

    public void ativarProfissional(UUID id) {
        profissionalService.ativarProfissional(id);
    }

    public void desativarProfissional(UUID id) {
        profissionalService.desativarProfissional(id);
    }

    public void restaurarProfissional(Profissional profissional) {
        profissionalService.restaurarProfissional(profissional);
    }

    public void cadastrarDisponibilidade(Profissional profissional, LocalDateTime inicio, LocalDateTime fim) {
        horarioService.cadastrarDisponibilidade(profissional, inicio, fim);
    }

    public List<PeriodoRecord> listarHorariosDisponiveis(Profissional profissional) {
        return horarioService.listarHorariosDisponiveis(profissional);
    }

    public void restaurarHorario(Profissional profissional, PeriodoRecord periodo) {
        horarioService.restaurarHorario(profissional, periodo);
    }

    public List<HorarioSalvo> exportarHorarios() {
        return horarioService.exportarHorarios();
    }

    public Consulta agendar(Paciente paciente, Profissional profissional, PeriodoRecord periodo) {
        return consultaService.agendar(paciente, profissional, periodo);
    }

    public Consulta agendarRetorno(UUID consultaAnteriorId, PeriodoRecord periodo) {
        return consultaService.agendarRetorno(consultaAnteriorId, periodo);
    }

    public void concluirConsulta(UUID id) {
        consultaService.concluirConsulta(id);
    }

    public void cancelarConsulta(UUID id, Paciente paciente) {
        consultaService.cancelarConsulta(id, paciente);
    }

    public void cancelarConsulta(UUID id, Paciente paciente, PoliticaCancelamento politica, LocalDateTime momento) {
        consultaService.cancelarConsulta(id, paciente, politica, momento);
    }

    public List<Consulta> listarConsultas() {
        return consultaService.listarConsultas();
    }

    public List<Consulta> listarConsultasDoPaciente(Paciente paciente) {
        return consultaService.listarConsultasDoPaciente(paciente);
    }

    public List<Consulta> listarConsultasDoProfissional(Profissional profissional) {
        return consultaService.listarConsultasDoProfissional(profissional);
    }

    public List<Consulta> consultar(Predicate<Consulta> filtro) {
        return consultaService.consultar(filtro);
    }

    public void restaurarConsulta(Consulta consulta) {
        consultaService.restaurarConsulta(consulta);
    }

    public String gerarRelatorio() {
        return relatorioService.gerarRelatorio();
    }
}
