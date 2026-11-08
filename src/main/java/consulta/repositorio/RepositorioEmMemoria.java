package consulta.repositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Implementação simples em memória; os dados existem enquanto o programa estiver aberto. */
public class RepositorioEmMemoria<T, ID> implements Repositorio<T, ID> {
    private final Map<ID, T> dados = new LinkedHashMap<>();
    private final Function<T, ID> extratorId;

    public RepositorioEmMemoria(Function<T, ID> extratorId) {
        if (extratorId == null) {
            throw new IllegalArgumentException("O extrator de ID é obrigatório.");
        }
        this.extratorId = extratorId;
    }

    @Override public void salvar(T entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("A entidade não pode ser nula.");
        }
        ID id = extratorId.apply(entidade);
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        dados.put(id, entidade);
    }
    @Override public Optional<T> buscarPorId(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        return Optional.ofNullable(dados.get(id));
    }
    @Override public List<T> listarTodos() {
        return new ArrayList<>(dados.values());
    }
    @Override public void remover(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        dados.remove(id);
    }
}
