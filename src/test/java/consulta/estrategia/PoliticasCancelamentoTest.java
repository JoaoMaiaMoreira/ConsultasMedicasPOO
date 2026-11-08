package consulta.estrategia;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PoliticasCancelamentoTest {
    private Consulta consulta(LocalDateTime inicio) {
        Paciente paciente = new Paciente(UUID.randomUUID(), "Ana", "123", "999");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. João", EspecialidadeEnum.CARDIOLOGIA, new BigDecimal("100"));
        return new Consulta(UUID.randomUUID(), paciente, medico, new PeriodoRecord(inicio, inicio.plusHours(1)));
    }

    @Test void cancelamentoLivreAceitaCancelamentoAntesDoFim() {
        Consulta c = consulta(LocalDateTime.now().plusDays(3));
        assertDoesNotThrow(() -> new CancelamentoLivreStrategy().validarCancelamento(c, LocalDateTime.now()));
    }

    @Test void cancelamentoComAntecedenciaRejeitaMenosDe24Horas() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(10);
        Consulta c = consulta(inicio);
        assertThrows(DominioException.class, () -> new CancelamentoComAntecedenciaStrategy().validarCancelamento(c, LocalDateTime.now()));
    }

    @Test void cancelamentoComAntecedenciaAceitaMaisDe24Horas() {
        Consulta c = consulta(LocalDateTime.now().plusDays(3));
        assertDoesNotThrow(() -> new CancelamentoComAntecedenciaStrategy().validarCancelamento(c, LocalDateTime.now()));
    }
}
