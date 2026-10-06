package consulta.dominio;


import consulta.excecao.DominioException;

import java.time.LocalDateTime;

public record PeriodoRecord(LocalDateTime inicio, LocalDateTime fim) {

    public PeriodoRecord {
        if (inicio == null || fim == null) {
            throw new DominioException("As datas de início e fim do período não podem ser nulas.");
        }
        if (!inicio.isBefore(fim)) {
            throw new DominioException("A data/hora de início deve ser estritamente anterior à data/hora de fim.");
        }
    }

    public boolean sobrepoe(PeriodoRecord outro) {
        if (outro == null) {
            return false;
        }
        return this.inicio.isBefore(outro.fim()) && outro.inicio().isBefore(this.fim);
    }
}