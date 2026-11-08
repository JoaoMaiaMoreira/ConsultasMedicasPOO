package consulta.servico;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.repositorio.RepositorioEmMemoria;
import java.util.List;
import java.util.UUID;

public class PacienteService {
    private final RepositorioEmMemoria<Paciente, UUID> pacientes;

    public PacienteService(AgendaDados dados) {
        this.pacientes = dados.pacientes;
    }

    public void cadastrarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new DominioException("Paciente obrigatório.");
        }
        boolean cpfDuplicado = pacientes.listarTodos().stream().anyMatch(p -> p.getCpf().equals(paciente.getCpf()));
        if (cpfDuplicado) {
            throw new DominioException("Já existe paciente cadastrado com esse CPF.");
        }
        pacientes.salvar(paciente);
    }

    public List<Paciente> listarPacientes() {
        return pacientes.listarTodos();
    }

    public void restaurarPaciente(Paciente p) {
        if (p == null || pacientes.buscarPorId(p.getId()).isPresent()
                || pacientes.listarTodos().stream().anyMatch(cadastrado -> cadastrado.getCpf().equals(p.getCpf()))) {
            throw new DominioException("Paciente duplicado ou inválido no arquivo JSON.");
        }
        pacientes.salvar(p);
    }
}
