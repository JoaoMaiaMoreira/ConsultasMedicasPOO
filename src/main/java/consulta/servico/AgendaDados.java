package consulta.servico;

import consulta.dominio.*;
import consulta.repositorio.RepositorioEmMemoria;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Dados compartilhados pelos serviços desta agenda.
// Cada agenda possui seus próprios cadastros, sem variáveis static.
class AgendaDados {
    final RepositorioEmMemoria<Paciente, UUID> pacientes = new RepositorioEmMemoria<>(Paciente::getId);
    final RepositorioEmMemoria<Profissional, UUID> profissionais = new RepositorioEmMemoria<>(Profissional::getId);
    final RepositorioEmMemoria<Consulta, UUID> consultas = new RepositorioEmMemoria<>(Consulta::getId);
    final List<HorarioDisponivel> horarios = new ArrayList<>();

    record HorarioDisponivel(Profissional profissional, PeriodoRecord periodo) {
    }
}
