package consulta.estrategia;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PrecoFixoStrategyTest {

    @Test
    @DisplayName("Deve calcular o valor fixo corretamente para uma consulta")
    void deveCalcularPrecoFixo() {
        BigDecimal valorEsperado = BigDecimal.valueOf(250.00);
        PrecoFixoStrategy estrategia = new PrecoFixoStrategy(valorEsperado);

        Paciente p = new Paciente(UUID.randomUUID(), "Bianca", "123.456.789-00", "");
        Profissional m = new Profissional(UUID.randomUUID(), "Dr. Japones", EspecialidadeEnum.CARDIOLOGIA);
        PeriodoRecord periodo = new PeriodoRecord(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        Consulta consulta = new Consulta(UUID.randomUUID(), p, m, periodo);

        ValorConsultaRecord valor = estrategia.calcular(consulta);

        assertEquals(0, valorEsperado.compareTo(valor.precoBase()));
        assertEquals(0, valorEsperado.compareTo(valor.valorComDesconto()));
        assertEquals("Preço Fixo Tabela", valor.promocao());
    }

    @Test
    @DisplayName("Deve lançar exceção para valor fixo nulo ou negativo")
    void deveLancarExcecaoParaValorInvalido() {
        assertThrows(DominioException.class, () -> new PrecoFixoStrategy(null));
        assertThrows(DominioException.class, () -> new PrecoFixoStrategy(BigDecimal.valueOf(-10.00)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar calcular preço para consulta nula")
    void deveLancarExcecaoParaConsultaNula() {
        PrecoFixoStrategy estrategia = new PrecoFixoStrategy(BigDecimal.valueOf(150.00));
        assertThrows(DominioException.class, () -> estrategia.calcular(null));
    }
}