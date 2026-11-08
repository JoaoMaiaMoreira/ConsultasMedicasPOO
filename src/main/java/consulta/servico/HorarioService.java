package consulta.servico;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.repositorio.RepositorioEmMemoria;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class HorarioService {
    private final RepositorioEmMemoria<Profissional, UUID> profissionais;
    private final RepositorioEmMemoria<Consulta, UUID> consultas;
    private final List<AgendaDados.HorarioDisponivel> horarios;

    public HorarioService(AgendaDados dados) {
        this.profissionais = dados.profissionais;
        this.consultas = dados.consultas;
        this.horarios = dados.horarios;
    }

    public void cadastrarDisponibilidade(Profissional profissional, LocalDateTime inicio, LocalDateTime fim) {
        exigirProfissionalCadastrado(profissional);
        if (!profissional.isAtivo()) {
            throw new DominioException("Médico inativo não pode cadastrar horários.");
        }
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new DominioException("Período inválido.");
        }
        if (!inicio.isAfter(LocalDateTime.now())) {
            throw new DominioException("A disponibilidade deve começar em uma data e horário futuros.");
        }
        if (Duration.between(inicio, fim).toMinutes() % 60 != 0 || inicio.getMinute() != 0 || inicio.getSecond() != 0 || inicio.getNano() != 0) {
            throw new DominioException("A disponibilidade deve conter blocos completos de uma hora, começando em hora cheia.");
        }

        for (LocalDateTime atual = inicio; atual.plusHours(1).compareTo(fim) <= 0; atual = atual.plusHours(1)) {
            PeriodoRecord novo = new PeriodoRecord(atual, atual.plusHours(1));
            boolean existe = horarios.stream().anyMatch(h -> h.profissional().equals(profissional) && h.periodo().sobrepoe(novo));
            if (existe) {
                throw new DominioException("Já existe disponibilidade cadastrada nesse horário.");
            }
        }

        for (LocalDateTime atual = inicio; atual.plusHours(1).compareTo(fim) <= 0; atual = atual.plusHours(1)) {
            horarios.add(new AgendaDados.HorarioDisponivel(profissional, new PeriodoRecord(atual, atual.plusHours(1))));
        }
    }

    public List<PeriodoRecord> listarHorariosDisponiveis(Profissional profissional) {
        exigirProfissionalCadastrado(profissional);
        return horarios.stream().filter(h -> h.profissional().equals(profissional))
                .map(AgendaDados.HorarioDisponivel::periodo)
                .filter(periodo -> estaLivre(profissional, periodo))
                .sorted(Comparator.comparing(PeriodoRecord::inicio)).toList();
    }

    private boolean estaLivre(Profissional profissional, PeriodoRecord periodo) {
        return consultas.listarTodos().stream().noneMatch(c -> c.getStatus() == StatusConsultaEnum.AGENDADA
                && c.getProfissional().equals(profissional) && c.getPeriodo().sobrepoe(periodo));
    }

    public void restaurarHorario(Profissional p, PeriodoRecord periodo) {
        exigirProfissionalCadastrado(p);
        if (periodo == null || Duration.between(periodo.inicio(), periodo.fim()).toMinutes() != 60
                || periodo.inicio().getMinute() != 0 || periodo.inicio().getSecond() != 0 || periodo.inicio().getNano() != 0) {
            throw new DominioException("Horário inválido no arquivo JSON: cada bloco deve ter uma hora cheia.");
        }
        boolean conflito = horarios.stream().anyMatch(h -> h.profissional().equals(p) && h.periodo().sobrepoe(periodo));
        if (conflito) {
            throw new DominioException("Há horários sobrepostos no arquivo JSON.");
        }
        horarios.add(new AgendaDados.HorarioDisponivel(p, periodo));
    }

    public java.util.List<AgendaService.HorarioSalvo> exportarHorarios() {
        return horarios.stream().map(h -> new AgendaService.HorarioSalvo(h.profissional().getId(), h.periodo())).toList();
    }

    private void exigirProfissionalCadastrado(Profissional profissional) {
        if (profissional == null || profissionais.buscarPorId(profissional.getId()).isEmpty()) {
            throw new DominioException("Médico não cadastrado.");
        }
    }
}
