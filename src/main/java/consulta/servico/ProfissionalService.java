package consulta.servico;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.repositorio.RepositorioEmMemoria;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ProfissionalService {
    private final RepositorioEmMemoria<Profissional, UUID> profissionais;

    public ProfissionalService(AgendaDados dados) {
        this.profissionais = dados.profissionais;
    }

    public void cadastrarProfissional(Profissional profissional) {
        if (profissional == null) {
            throw new DominioException("Profissional obrigatório.");
        }
        profissionais.salvar(profissional);
    }

    public List<Profissional> listarTodosProfissionais() {
        return profissionais.listarTodos();
    }

    public List<Profissional> listarProfissionais() {
        return profissionais.listarTodos().stream().filter(Profissional::isAtivo)
                .sorted(Comparator.comparing(Profissional::getNome)).toList();
    }

    public void ativarProfissional(UUID id) {
        Profissional profissional = profissionais.buscarPorId(id)
                .orElseThrow(() -> new DominioException("Profissional não encontrado."));
        profissional.ativar();
    }

    public void desativarProfissional(UUID id) {
        Profissional profissional = profissionais.buscarPorId(id)
                .orElseThrow(() -> new DominioException("Profissional não encontrado."));
        profissional.desativar();
    }

    public void restaurarProfissional(Profissional p) {
        if (p == null || profissionais.buscarPorId(p.getId()).isPresent()) {
            throw new DominioException("Profissional duplicado ou inválido no arquivo JSON.");
        }
        profissionais.salvar(p);
    }
}
