package consulta.estrategia;

import consulta.dominio.Consulta;
import java.time.LocalDateTime;

public interface PoliticaCancelamento {
    void validarCancelamento(Consulta consulta, LocalDateTime momento);
}
