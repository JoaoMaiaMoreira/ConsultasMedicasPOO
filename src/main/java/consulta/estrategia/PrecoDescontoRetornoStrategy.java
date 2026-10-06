package consulta.estrategia;

import consulta.dominio.Consulta;
import consulta.dominio.ValorConsultaRecord;
import consulta.excecao.DominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PrecoDescontoRetornoStrategy implements CalculadoraPreco {

    private final BigDecimal precoBase;
    private final BigDecimal percentualDesconto;

    public PrecoDescontoRetornoStrategy(BigDecimal precoBase, BigDecimal percentualDesconto) {
        if (precoBase == null || percentualDesconto == null) {
            throw new DominioException("O preço base e o percentual de desconto não podem ser nulos.");
        }
        if (precoBase.compareTo(BigDecimal.ZERO) < 0) {
            throw new DominioException("O preço base não pode ser negativo.");
        }
        if (percentualDesconto.compareTo(BigDecimal.ZERO) < 0 || percentualDesconto.compareTo(new BigDecimal("100")) > 0) {
            throw new DominioException("O percentual de desconto deve estar entre 0% e 100%.");
        }
        this.precoBase = precoBase;
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public ValorConsultaRecord calcular(Consulta consulta) {
        if (consulta == null) {
            throw new DominioException("A consulta para cálculo de preço não pode ser nula.");
        }

        if (consulta.getConsultaAnterior() != null) {
            BigDecimal fatorDesconto = percentualDesconto.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal valorDesconto = precoBase.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorComDesconto = precoBase.subtract(valorDesconto);

            return new ValorConsultaRecord(
                    precoBase,
                    valorComDesconto,
                    "Desconto de Retorno (" + percentualDesconto.stripTrailingZeros().toPlainString() + "%)"
            );
        }

        return new ValorConsultaRecord(precoBase, precoBase, "Preço Base - Sem Desconto");
    }
}