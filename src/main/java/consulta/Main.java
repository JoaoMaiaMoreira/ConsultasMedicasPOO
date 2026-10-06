package consulta;

import consulta.dominio.*;
import consulta.estrategia.PrecoDescontoRetornoStrategy;
import consulta.estrategia.PrecoFixoStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        Paciente paciente = new Paciente(UUID.randomUUID(), "Bianca", "123.456.789-00", "(35) 99999-8888");
        Profissional medico = new Profissional(UUID.randomUUID(), "Dr. Japones", EspecialidadeEnum.CARDIOLOGIA);

        LocalDateTime amanha = LocalDateTime.now().plusDays(1);

        PeriodoRecord periodo1 = new PeriodoRecord(amanha.withHour(14), amanha.withHour(15));
        Consulta consulta1 = new Consulta(UUID.randomUUID(), paciente, medico, periodo1);

        consulta1.aplicarCalculoPreco(new PrecoFixoStrategy(BigDecimal.valueOf(250.00)));
        consulta1.concluir();

        System.out.println("Consulta 1: " + consulta1.getStatus().getDescricao() +
                " | Valor: R$ " + consulta1.getValorConsulta().valorComDesconto());

        LocalDateTime daqui15Dias = amanha.plusDays(15);
        PeriodoRecord periodo2 = new PeriodoRecord(daqui15Dias.withHour(10), daqui15Dias.withHour(11));
        Consulta consulta2 = new Consulta(UUID.randomUUID(), paciente, medico, periodo2);

        consulta1.vincularProxima(consulta2);
        consulta2.aplicarCalculoPreco(new PrecoDescontoRetornoStrategy(BigDecimal.valueOf(250.00), BigDecimal.valueOf(20.00)));

        System.out.println("Consulta 2 (Retorno): " + consulta2.getStatus().getDescricao() +
                " | Valor com Desconto: R$ " + consulta2.getValorConsulta().valorComDesconto() +
                " (" + consulta2.getValorConsulta().promocao() + ")");
    }
}