package consulta.dominio;

import consulta.excecao.DominioException;

import java.util.Objects;
import java.math.BigDecimal;
import java.util.UUID;

public class Profissional {

    private final UUID id;
    private final String nome;
    private final EspecialidadeEnum especialidade;
    private boolean ativo;
    private final BigDecimal valorConsulta;

    public Profissional(UUID id, String nome, EspecialidadeEnum especialidade) {
        this(id, nome, especialidade, BigDecimal.ZERO);
    }

    public Profissional(UUID id, String nome, EspecialidadeEnum especialidade, BigDecimal valorConsulta) {
        validarCamposObrigatorios(id, nome, especialidade);
        if (valorConsulta == null || valorConsulta.compareTo(BigDecimal.ZERO) < 0) {
            throw new DominioException("O valor da consulta não pode ser nulo ou negativo.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.especialidade = especialidade;
        this.valorConsulta = valorConsulta.setScale(2, java.math.RoundingMode.HALF_UP);
        this.ativo = true;
    }

    private void validarCamposObrigatorios(UUID id, String nome, EspecialidadeEnum especialidade) {
        if (id == null) {
            throw new DominioException("O identificador do profissional não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new DominioException("O nome do profissional é obrigatório e não pode ser vazio.");
        }
        if (especialidade == null) {
            throw new DominioException("A especialidade do profissional não pode ser nula.");
        }
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public EspecialidadeEnum getEspecialidade() {
        return especialidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public BigDecimal getValorConsulta() {
        return valorConsulta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profissional header = (Profissional) o;
        return Objects.equals(id, header.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
