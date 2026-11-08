package consulta.estrategia;

import consulta.dominio.Consulta;
import consulta.excecao.DominioException;
import java.time.LocalDateTime;

public class CancelamentoLivreStrategy implements PoliticaCancelamento {
    @Override
    public void validarCancelamento(Consulta consulta, LocalDateTime momento) {
        if (consulta == null || momento == null) {
            throw new DominioException("Consulta e momento são obrigatórios.");
        }
        if (!momento.isBefore(consulta.getPeriodo().fim())) {
            throw new DominioException("Não é possível cancelar uma consulta após seu término.");
        }
    }
}
