package consulta.dominio;

import consulta.excecao.DominioException;

import java.math.BigDecimal;

public record ValorConsultaRecord(
        BigDecimal precoBase,
        BigDecimal valorComDesconto,
        String promocao
) {

    public ValorConsultaRecord {
        validarValoresMonetarios(precoBase, valorComDesconto);

        if (promocao == null || promocao.trim().isEmpty()) {
            promocao = "Sem promoção aplicável";
        } else {
            promocao = promocao.trim();
        }
    }

    private void validarValoresMonetarios(BigDecimal precoBase, BigDecimal valorComDesconto) {
        if (precoBase == null || valorComDesconto == null) {
            throw new DominioException("Os valores monetários da consulta não podem ser nulos.");
        }
        if (precoBase.compareTo(BigDecimal.ZERO) < 0 || valorComDesconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new DominioException("Os valores monetários da consulta não podem ser negativos.");
        }
    }
}