package consulta.estrategia;

import consulta.dominio.Consulta;
import consulta.dominio.ValorConsultaRecord;

public interface CalculadoraPreco {

    ValorConsultaRecord calcular(Consulta consulta);
}