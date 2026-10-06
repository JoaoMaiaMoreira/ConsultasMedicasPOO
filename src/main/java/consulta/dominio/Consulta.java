package consulta.dominio;

import consulta.estrategia.CalculadoraPreco;
import consulta.excecao.DominioException;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Consulta {

    private final UUID id;
    private final Paciente paciente;
    private final Profissional profissional;
    private final PeriodoRecord periodo;
    private StatusConsultaEnum status;
    private ValorConsultaRecord valorConsulta;
    private Consulta consultaAnterior;
    private Consulta proximaConsulta;

    public Consulta(UUID id, Paciente paciente, Profissional profissional, PeriodoRecord periodo) {
        validarCamposObrigatorios(id, paciente, profissional, periodo);
        validarProfissionalAtivo(profissional);

        this.id = id;
        this.paciente = paciente;
        this.profissional = profissional;
        this.periodo = periodo;
        this.status = StatusConsultaEnum.AGENDADA;
    }

    private void validarCamposObrigatorios(UUID id, Paciente paciente, Profissional profissional, PeriodoRecord periodo) {
        if (id == null) {
            throw new DominioException("O identificador da consulta não pode ser nulo.");
        }
        if (paciente == null) {
            throw new DominioException("O paciente da consulta não pode ser nulo.");
        }
        if (profissional == null) {
            throw new DominioException("O profissional da consulta não pode ser nulo.");
        }
        if (periodo == null) {
            throw new DominioException("O período da consulta não pode ser nulo.");
        }
    }

    private void validarProfissionalAtivo(Profissional profissional) {
        if (!profissional.isAtivo()) {
            throw new DominioException("Não é possível agendar consulta para um profissional inativo.");
        }
    }

    public void concluir() {
        if (this.status != StatusConsultaEnum.AGENDADA) {
            throw new DominioException("Apenas consultas no estado AGENDADA podem ser concluídas.");
        }
        this.status = StatusConsultaEnum.CONCLUIDA;
    }

    public void cancelar() {
        if (this.status != StatusConsultaEnum.AGENDADA) {
            throw new DominioException("Apenas consultas no estado AGENDADA podem ser canceladas.");
        }
        this.status = StatusConsultaEnum.CANCELADA;
    }

    public void aplicarCalculoPreco(CalculadoraPreco calculadora) {
        if (calculadora == null) {
            throw new DominioException("A calculadora de preço não pode ser nula.");
        }
        this.valorConsulta = calculadora.calcular(this);
    }

    public void vincularProxima(Consulta proxima) {
        if (proxima == null) {
            throw new DominioException("A próxima consulta não pode ser nula.");
        }
        if (!this.paciente.equals(proxima.getPaciente())) {
            throw new DominioException("A consulta de retorno deve pertencer ao mesmo paciente.");
        }
        if (!this.profissional.equals(proxima.getProfissional())) {
            throw new DominioException("A consulta de retorno deve ser realizada com o mesmo profissional.");
        }

        long diasDiferenca = ChronoUnit.DAYS.between(this.periodo.inicio(), proxima.getPeriodo().inicio());
        if (diasDiferenca < 0 || diasDiferenca > 30) {
            throw new DominioException("A consulta de retorno deve ocorrer no prazo máximo de 30 dias após a consulta anterior.");
        }

        this.proximaConsulta = proxima;
        proxima.consultaAnterior = this;
    }

    public UUID getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public PeriodoRecord getPeriodo() {
        return periodo;
    }

    public StatusConsultaEnum getStatus() {
        return status;
    }

    public ValorConsultaRecord getValorConsulta() {
        return valorConsulta;
    }

    public Consulta getConsultaAnterior() {
        return consultaAnterior;
    }

    public Consulta getProximaConsulta() {
        return proximaConsulta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return Objects.equals(id, consulta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}