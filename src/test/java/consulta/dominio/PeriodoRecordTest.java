package consulta.dominio;

import consulta.excecao.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PeriodoRecordTest {

    @Test
    @DisplayName("Deve criar um período válido quando a data de início for anterior à de fim")
    void deveCriarPeriodoValido() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(1);

        PeriodoRecord periodo = new PeriodoRecord(inicio, fim);

        assertEquals(inicio, periodo.inicio());
        assertEquals(fim, periodo.fim());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data de início for igual ou posterior à de fim")
    void deveLancarExcecaoQuandoInicioMaiorOuIgualFim() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fimInvalido = inicio.minusHours(1);

        assertThrows(DominioException.class, () -> new PeriodoRecord(inicio, fimInvalido));
        assertThrows(DominioException.class, () -> new PeriodoRecord(inicio, inicio));
    }

    @Test
    @DisplayName("Deve detectar sobreposição entre dois períodos")
    void deveDetectarSobreposicaoDeHorarios() {
        LocalDateTime base = LocalDateTime.now().plusDays(1);
        PeriodoRecord p1 = new PeriodoRecord(base.withHour(14), base.withHour(15));
        PeriodoRecord p2 = new PeriodoRecord(base.withHour(14), base.withHour(15));

        assertTrue(p1.sobrepoe(p2));
    }
}