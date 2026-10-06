package consulta.estrategia;

import consulta.dominio.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PrecoDescontoRetornoStrategyTest {

    @Test
    @DisplayName("Deve aplicar valor cheio para consulta inicial (sem retorno vinculado)")
    void deveCalcularPrecoCheioParaConsultaInicial() {
        Paciente p = new Paciente(UUID.randomUUID(), "Ana", "123.456.789-00", "");
        Profissional m = new Profissional(UUID.randomUUID(), "Dr. Silva", EspecialidadeEnum.CLINICO_GERAL);
        PeriodoRecord periodo = new PeriodoRecord(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        Consulta consulta = new Consulta(UUID.randomUUID(), p, m, periodo);

        PrecoDescontoRetornoStrategy estrategia = new PrecoDescontoRetornoStrategy(
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(15.00)
        );

        ValorConsultaRecord valor = estrategia.calcular(consulta);

        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(valor.valorComDesconto()));
    }

    @Test
    @DisplayName("Deve aplicar desconto percentual quando for consulta de retorno")
    void deveAplicarDescontoQuandoForRetorno() {
        Paciente p = new Paciente(UUID.randomUUID(), "Ana", "123.456.789-00", "");
        Profissional m = new Profissional(UUID.randomUUID(), "Dr. Silva", EspecialidadeEnum.CLINICO_GERAL);

        LocalDateTime inicio1 = LocalDateTime.now().plusDays(1);
        Consulta c1 = new Consulta(UUID.randomUUID(), p, m, new PeriodoRecord(inicio1, inicio1.plusHours(1)));

        LocalDateTime inicio2 = inicio1.plusDays(10);
        Consulta c2 = new Consulta(UUID.randomUUID(), p, m, new PeriodoRecord(inicio2, inicio2.plusHours(1)));

        c1.vincularProxima(c2);

        PrecoDescontoRetornoStrategy estrategia = new PrecoDescontoRetornoStrategy(
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(20.00)
        );

        ValorConsultaRecord valor = estrategia.calcular(c2);

        assertEquals(0, BigDecimal.valueOf(160.00).compareTo(valor.valorComDesconto()));
    }
}