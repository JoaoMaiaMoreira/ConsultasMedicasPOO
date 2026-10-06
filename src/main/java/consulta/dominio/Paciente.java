package consulta.dominio;

import consulta.excecao.DominioException;

import java.util.Objects;
import java.util.UUID;

public class Paciente {

    private final UUID id;
    private final String nome;
    private final String cpf;
    private final String telefone;

    public Paciente(UUID id, String nome, String cpf, String telefone) {
        validarCamposObrigatorios(id, nome, cpf);

        this.id = id;
        this.nome = nome.trim();
        this.cpf = cpf.trim();
        this.telefone = telefone != null ? telefone.trim() : "";
    }

    private void validarCamposObrigatorios(UUID id, String nome, String cpf) {
        if (id == null) {
            throw new DominioException("O identificador do paciente não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new DominioException("O nome do paciente é obrigatório e não pode ser vazio.");
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new DominioException("O CPF do paciente é obrigatório e não pode ser vazio.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(id, paciente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}