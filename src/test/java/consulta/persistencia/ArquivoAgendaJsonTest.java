package consulta.persistencia;

import consulta.dominio.*;
import consulta.servico.AgendaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ArquivoAgendaJsonTest {
    @TempDir Path pasta;

    @Test void salvaERecuperaCadastrosEConsultas() {
        AgendaService original = new AgendaService();
        Paciente paciente = new Paciente(UUID.randomUUID(), "Maria", "11122233344", "31999990000");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. Carlos", EspecialidadeEnum.CARDIOLOGIA, new BigDecimal("180.00"));
        LocalDateTime inicio = LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0);
        PeriodoRecord periodo = new PeriodoRecord(inicio, inicio.plusHours(1));
        original.cadastrarPaciente(paciente);
        original.cadastrarProfissional(medico);
        original.restaurarHorario(medico, periodo);
        original.agendar(paciente, medico, periodo);

        ArquivoAgendaJson arquivo = new ArquivoAgendaJson(pasta.resolve("agenda.json"));
        arquivo.salvar(original);
        AgendaService recuperada = arquivo.carregar();

        assertEquals(1, recuperada.listarPacientes().size());
        assertEquals(1, recuperada.listarTodosProfissionais().size());
        assertEquals(1, recuperada.listarConsultas().size());
        assertEquals("Maria", recuperada.listarPacientes().getFirst().getNome());
        assertTrue(Files.exists(pasta.resolve("agenda.json")));
    }
}
