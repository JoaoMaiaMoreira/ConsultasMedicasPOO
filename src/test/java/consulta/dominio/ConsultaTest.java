package consulta.dominio;

import consulta.excecao.DominioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaTest {

    private Paciente paciente;
    private Profissional medico;
    private PeriodoRecord periodo;

    @BeforeEach
    void setUp() {
        paciente = new Paciente(UUID.randomUUID(), "Bianca", "123.456.789-00", "(35) 99999-8888");
        medico = new Profissional(UUID.randomUUID(), "Dr. Japones", EspecialidadeEnum.CARDIOLOGIA);
        LocalDateTime inicio = LocalDateTime.now().plusDays(1).withHour(14);
        periodo = new PeriodoRecord(inicio, inicio.plusHours(1));
    }

    @Test
    @DisplayName("Deve agendar uma consulta com status inicial AGENDADA")
    void deveCriarConsultaComStatusAgendada() {
        Consulta consulta = new Consulta(UUID.randomUUID(), paciente, medico, periodo);
        assertEquals(StatusConsultaEnum.AGENDADA, consulta.getStatus());
    }

    @Test
    @DisplayName("Não deve permitir agendar consulta para profissional inativo")
    void naoDeveAgendarParaProfissionalInativo() {
        medico.desativar();
        assertThrows(DominioException.class, () -> new Consulta(UUID.randomUUID(), paciente, medico, periodo));
    }

    @Test
    @DisplayName("Deve vincular consulta de retorno válida dentro do prazo de 30 dias")
    void deveVincularConsultaDeRetornoComSucesso() {
        Consulta c1 = new Consulta(UUID.randomUUID(), paciente, medico, periodo);

        LocalDateTime inicioRetorno = periodo.inicio().plusDays(10);
        PeriodoRecord periodoRetorno = new PeriodoRecord(inicioRetorno, inicioRetorno.plusHours(1));
        Consulta c2 = new Consulta(UUID.randomUUID(), paciente, medico, periodoRetorno);

        c1.vincularProxima(c2);

        assertEquals(c2, c1.getProximaConsulta());
        assertEquals(c1, c2.getConsultaAnterior());
    }

    @Test
    @DisplayName("Deve rejeitar consulta de retorno quando exceder 30 dias")
    void deveRejeitarRetornoApos30Dias() {
        Consulta c1 = new Consulta(UUID.randomUUID(), paciente, medico, periodo);

        LocalDateTime inicioInvalido = periodo.inicio().plusDays(31);
        PeriodoRecord periodoRetorno = new PeriodoRecord(inicioInvalido, inicioInvalido.plusHours(1));
        Consulta c2 = new Consulta(UUID.randomUUID(), paciente, medico, periodoRetorno);

        assertThrows(DominioException.class, () -> c1.vincularProxima(c2));
    }
}