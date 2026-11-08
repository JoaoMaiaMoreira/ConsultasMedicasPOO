package consulta.servico;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AgendaServiceTest {
    @Test
    void deveAgendarConsultaComPrecoDoMedico() {
        AgendaService agenda = new AgendaService();
        Paciente paciente = new Paciente(UUID.randomUUID(), "Ana", "123", "999");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dra. Maria", EspecialidadeEnum.CARDIOLOGIA, new BigDecimal("180.00"));
        agenda.cadastrarPaciente(paciente);
        agenda.cadastrarProfissional(medico);
        LocalDateTime inicio = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(9, 0));
        agenda.cadastrarDisponibilidade(medico, inicio, inicio.plusHours(2));

        Consulta consulta = agenda.agendar(paciente, medico, new PeriodoRecord(inicio, inicio.plusHours(1)));

        assertEquals(new BigDecimal("180.00"), consulta.getValorConsulta().valorComDesconto());
        assertEquals(1, agenda.listarConsultasDoPaciente(paciente).size());
        assertEquals(1, agenda.listarHorariosDisponiveis(medico).size());
    }

    @Test
    void deveRecusarConflitoDeMedicoEPermitirHorarioAposCancelamento() {
        AgendaService agenda = new AgendaService();
        Paciente p1 = new Paciente(UUID.randomUUID(), "Ana", "123", "");
        Paciente p2 = new Paciente(UUID.randomUUID(), "Bia", "456", "");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. Paulo", EspecialidadeEnum.CLINICO_GERAL, new BigDecimal("100"));
        agenda.cadastrarPaciente(p1);
        agenda.cadastrarPaciente(p2);
        agenda.cadastrarProfissional(medico);
        LocalDateTime inicio = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.of(10, 0));
        agenda.cadastrarDisponibilidade(medico, inicio, inicio.plusHours(1));
        PeriodoRecord periodo = new PeriodoRecord(inicio, inicio.plusHours(1));

        Consulta consulta = agenda.agendar(p1, medico, periodo);
        assertThrows(DominioException.class, () -> agenda.agendar(p2, medico, periodo));
        agenda.cancelarConsulta(consulta.getId(), p1);
        assertDoesNotThrow(() -> agenda.agendar(p2, medico, periodo));
    }

    @Test
    void deveAgendarRetornoComDescontoEManterVinculo() {
        AgendaService agenda = new AgendaService();
        Paciente paciente = new Paciente(UUID.randomUUID(), "Carla", "789", "");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dra. Lúcia", EspecialidadeEnum.CARDIOLOGIA, new BigDecimal("200.00"));
        agenda.cadastrarPaciente(paciente);
        agenda.cadastrarProfissional(medico);
        LocalDateTime primeiraData = LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0);
        PeriodoRecord primeiroHorario = new PeriodoRecord(primeiraData, primeiraData.plusHours(1));
        PeriodoRecord retornoHorario = new PeriodoRecord(primeiraData.plusDays(10), primeiraData.plusDays(10).plusHours(1));
        agenda.restaurarHorario(medico, primeiroHorario);
        agenda.restaurarHorario(medico, retornoHorario);

        Consulta primeira = agenda.agendar(paciente, medico, primeiroHorario);
        agenda.concluirConsulta(primeira.getId());
        Consulta retorno = agenda.agendarRetorno(primeira.getId(), retornoHorario);

        assertEquals(primeira, retorno.getConsultaAnterior());
        assertEquals(retorno, primeira.getProximaConsulta());
        assertEquals(new BigDecimal("180.00"), retorno.getValorConsulta().valorComDesconto());
        assertThrows(DominioException.class, () -> agenda.agendarRetorno(primeira.getId(), retornoHorario));
    }

    @Test
    void consultaComStreamDeveFiltrarEOrdenar() {
        AgendaService agenda = new AgendaService();
        Paciente paciente = new Paciente(UUID.randomUUID(), "Davi", "321", "");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. André", EspecialidadeEnum.ODONTOLOGIA, new BigDecimal("90"));
        agenda.cadastrarPaciente(paciente);
        agenda.cadastrarProfissional(medico);
        LocalDateTime dia = LocalDateTime.now().plusDays(2).withMinute(0).withSecond(0).withNano(0);
        PeriodoRecord p1 = new PeriodoRecord(dia.plusHours(1), dia.plusHours(2));
        PeriodoRecord p2 = new PeriodoRecord(dia, dia.plusHours(1));
        agenda.restaurarHorario(medico, p1);
        agenda.restaurarHorario(medico, p2);
        agenda.agendar(paciente, medico, p1);
        // A consulta anterior ocupa outro horário; o filtro retorna somente consultas agendadas.
        agenda.agendar(paciente, medico, p2);
        var resultado = agenda.consultar(c -> c.getStatus() == StatusConsultaEnum.AGENDADA);
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getPeriodo().inicio().isBefore(resultado.get(1).getPeriodo().inicio()));
    }


    @Test
    void devePermitirAtivarEDesativarProfissional() {
        AgendaService agenda = new AgendaService();
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. Bruno", EspecialidadeEnum.CLINICO_GERAL);
        agenda.cadastrarProfissional(medico);
        agenda.desativarProfissional(medico.getId());
        assertFalse(medico.isAtivo());
        assertTrue(agenda.listarProfissionais().isEmpty());
        agenda.ativarProfissional(medico.getId());
        assertTrue(medico.isAtivo());
        assertEquals(1, agenda.listarProfissionais().size());
    }

}
