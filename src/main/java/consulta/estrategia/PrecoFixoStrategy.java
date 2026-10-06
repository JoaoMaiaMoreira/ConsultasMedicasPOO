package consulta.estrategia;

import consulta.dominio.Consulta;
import consulta.dominio.ValorConsultaRecord;
import consulta.excecao.DominioException;

import java.math.BigDecimal;


public class PrecoFixoStrategy implements CalculadoraPreco {

    private final BigDecimal valorFixo;

    public PrecoFixoStrategy(BigDecimal valorFixo) {
        if (valorFixo == null) {
            throw new DominioException("O valor fixo da consulta não pode ser nulo.");
        }
        if (valorFixo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DominioException("O valor fixo da consulta não pode ser negativo.");
        }
        this.valorFixo = valorFixo;
    }

    @Override
    public ValorConsultaRecord calcular(Consulta consulta) {
        if (consulta == null) {
            throw new DominioException("A consulta para cálculo de preço não pode ser nula.");
        }
        return new ValorConsultaRecord(valorFixo, valorFixo, "Preço Fixo Tabela");
    }
}