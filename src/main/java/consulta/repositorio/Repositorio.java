package consulta.repositorio;

import java.util.List;
import java.util.Optional;

/** Contrato genérico para guardar entidades identificadas por uma chave. */
public interface Repositorio<T, ID> {
    void salvar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void remover(ID id);
}
