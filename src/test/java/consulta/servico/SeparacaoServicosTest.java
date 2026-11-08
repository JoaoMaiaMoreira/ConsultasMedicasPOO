package consulta.servico;

import consulta.dominio.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SeparacaoServicosTest {

    @Test
    void servicosDevemCompartilharDadosDaMesmaAgenda() {
        AgendaService agenda = new AgendaService();
        Paciente paciente = new Paciente(UUID.randomUUID(), "Ana", "111", "999");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dra. Maria",
                EspecialidadeEnum.CARDIOLOGIA, new BigDecimal("100"));
        LocalDateTime inicio = LocalDateTime.now().plusDays(3)
                .withMinute(0).withSecond(0).withNano(0);
        PeriodoRecord periodo = new PeriodoRecord(inicio, inicio.plusHours(1));

        agenda.cadastrarPaciente(paciente);
        agenda.cadastrarProfissional(medico);
        agenda.cadastrarDisponibilidade(medico, inicio, inicio.plusHours(1));
        Consulta consulta = agenda.agendar(paciente, medico, periodo);

        assertEquals(1, agenda.listarPacientes().size());
        assertEquals(1, agenda.listarConsultas().size());
        assertEquals(0, agenda.listarHorariosDisponiveis(medico).size());

        agenda.cancelarConsulta(consulta.getId(), paciente);

        assertEquals(1, agenda.listarHorariosDisponiveis(medico).size());
        assertTrue(agenda.gerarRelatorio().contains("Consultas canceladas: 1"));
    }
}
