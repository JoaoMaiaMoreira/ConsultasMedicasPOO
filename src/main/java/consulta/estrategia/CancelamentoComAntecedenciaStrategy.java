package consulta.estrategia;

import consulta.dominio.Consulta;
import consulta.excecao.DominioException;
import java.time.LocalDateTime;

public class CancelamentoComAntecedenciaStrategy implements PoliticaCancelamento {
    @Override
    public void validarCancelamento(Consulta consulta, LocalDateTime momento) {
        if (consulta == null || momento == null) {
            throw new DominioException("Consulta e momento são obrigatórios.");
        }
        if (!momento.plusHours(24).isBefore(consulta.getPeriodo().inicio())
                && !momento.plusHours(24).isEqual(consulta.getPeriodo().inicio())) {
            throw new DominioException("O cancelamento exige antecedência mínima de 24 horas.");
        }
    }
}
