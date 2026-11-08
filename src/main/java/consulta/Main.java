package consulta;

import consulta.dominio.*;
import consulta.excecao.DominioException;
import consulta.servico.AgendaService;
import consulta.persistencia.ArquivoAgendaJson;
import consulta.estrategia.PoliticaCancelamento;
import consulta.estrategia.CancelamentoLivreStrategy;
import consulta.estrategia.CancelamentoComAntecedenciaStrategy;
import java.nio.file.Path;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static AgendaService agenda;
    private static final ArquivoAgendaJson arquivo = new ArquivoAgendaJson(Path.of("dados", "agenda.json"));

    public static void main(String[] args) {
        // Os dados são carregados do JSON antes de exibir o menu.
        agenda = arquivo.carregar();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> arquivo.salvar(agenda)));
        boolean executando = true;
        while (executando) {
            System.out.println("\n====================================");
            System.out.println("       SISTEMA DE CONSULTAS");
            System.out.println("====================================");
            System.out.println("1 - Entrar como paciente");
            System.out.println("2 - Entrar como médico");
            System.out.println("0 - Sair");
            int opcao = lerInteiro("Escolha: ");
            switch (opcao) {
                case 1 -> menuPaciente();
                case 2 -> menuMedico();
                case 0 -> executando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        arquivo.salvar(agenda);
        System.out.println("Dados salvos em dados/agenda.json. Sistema encerrado. Até logo!");
    }

    private static void menuPaciente() {
        Paciente paciente = null;
        if (agenda.listarPacientes().isEmpty() || confirmar("Você já possui cadastro? (s/n): ") == false) {
            paciente = cadastrarPaciente();
        } else {
            paciente = selecionarPaciente();
            if (paciente == null) {
                paciente = cadastrarPaciente();
            }
        }
        if (paciente == null) {
            return;
        }

        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n========== MENU PACIENTE ==========");
            System.out.println("Paciente: " + paciente.getNome());
            System.out.println("1 - Consultar médicos");
            System.out.println("2 - Buscar médicos por especialidade");
            System.out.println("3 - Ver horários disponíveis");
            System.out.println("4 - Agendar consulta");
            System.out.println("5 - Minhas consultas");
            System.out.println("6 - Cancelar consulta");
            System.out.println("7 - Agendar retorno");
            System.out.println("0 - Voltar");
            switch (lerInteiro("Escolha: ")) {
                case 1 -> listarMedicos(agenda.listarProfissionais());
                case 2 -> buscarMedicosPorEspecialidade();
                case 3 -> mostrarHorarios();
                case 4 -> agendarConsulta(paciente);
                case 5 -> listarConsultasPaciente(paciente);
                case 6 -> cancelarConsulta(paciente);
                case 7 -> agendarRetorno(paciente);
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void menuMedico() {
        Profissional profissional = null;
        if (!agenda.listarTodosProfissionais().isEmpty() && confirmar("Já está cadastrado? (s/n): ")) {
            profissional = selecionarMedicoCadastrado();
        }
        if (profissional == null) {
            profissional = cadastrarMedico();
        }
        if (profissional == null) {
            return;
        }
        if (!profissional.isAtivo()) {
            System.out.println("Este cadastro está desativado.");
            if (confirmar("Deseja reativá-lo? (s/n): ")) {
                agenda.ativarProfissional(profissional.getId());
            }
            else return;
        }

        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n============= MENU MÉDICO =============");
            System.out.println("Médico: " + profissional.getNome() + " | " + profissional.getEspecialidade().getDescricao());
            System.out.println("Valor da consulta: R$ " + profissional.getValorConsulta());
            System.out.println("1 - Cadastrar horários disponíveis");
            System.out.println("2 - Consultar meus horários disponíveis");
            System.out.println("3 - Visualizar consultas agendadas");
            System.out.println("4 - Relatório geral da agenda");
            System.out.println("5 - Concluir consulta");
            System.out.println("6 - Desativar meu cadastro");
            System.out.println("0 - Voltar");
            switch (lerInteiro("Escolha: ")) {
                case 1 -> cadastrarHorarios(profissional);
                case 2 -> listarHorarios(profissional);
                case 3 -> listarConsultasMedico(profissional);
                case 4 -> System.out.println("\n" + agenda.gerarRelatorio());
                case 5 -> concluirConsulta(profissional);
                case 6 -> {
                    agenda.desativarProfissional(profissional.getId());
                    System.out.println("Cadastro desativado.");
                    voltar = true;
                }
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static Paciente cadastrarPaciente() {
        try {
            System.out.println("\n--- Cadastro de paciente ---");
            String nome = lerTexto("Nome: ");
            String cpf = lerTexto("CPF: ");
            String telefone = lerTexto("Telefone: ");
            Paciente paciente = new Paciente(UUID.randomUUID(), nome, cpf, telefone);
            boolean cpfDuplicado = agenda.listarPacientes().stream().anyMatch(p -> p.getCpf().equals(paciente.getCpf()));
            if (cpfDuplicado) {
                throw new DominioException("Já existe paciente cadastrado com esse CPF.");
            }
            agenda.cadastrarPaciente(paciente);
            System.out.println("Cadastro realizado! Bem-vindo(a), " + paciente.getNome() + ".");
            return paciente;
        } catch (DominioException e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
            return null;
        }
    }

    private static Profissional cadastrarMedico() {
        try {
            System.out.println("\n--- Cadastro de médico ---");
            String nome = lerTexto("Nome completo: ");
            EspecialidadeEnum especialidade = escolherEspecialidade();
            BigDecimal valor = lerDinheiro("Valor da consulta (R$): ");
            Profissional medico = new Profissional(UUID.randomUUID(), nome, especialidade, valor);
            agenda.cadastrarProfissional(medico);
            System.out.println("Médico cadastrado com sucesso!");
            return medico;
        } catch (DominioException e) {
            System.out.println("Não foi possível cadastrar: " + e.getMessage());
            return null;
        }
    }

    private static EspecialidadeEnum escolherEspecialidade() {
        EspecialidadeEnum[] valores = EspecialidadeEnum.values();
        System.out.println("Especialidades:");
        for (int i = 0; i < valores.length; i++) System.out.println((i + 1) + " - " + valores[i].getDescricao());
        int escolha = lerInteiro("Escolha a especialidade: ");
        if (escolha < 1 || escolha > valores.length) {
            throw new DominioException("Especialidade inválida.");
        }
        return valores[escolha - 1];
    }

    private static Paciente selecionarPaciente() {
        List<Paciente> lista = agenda.listarPacientes();
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + " - " + lista.get(i).getNome() + " (CPF: " + lista.get(i).getCpf() + ")");
        int escolha = lerInteiro("Selecione seu cadastro (0 para novo): ");
        return escolha == 0 || escolha > lista.size() || escolha < 0 ? null : lista.get(escolha - 1);
    }

    private static Profissional selecionarMedicoCadastrado() {
        List<Profissional> lista = agenda.listarTodosProfissionais();
        for (int i = 0; i < lista.size(); i++) {
            Profissional p = lista.get(i);
            System.out.println((i + 1) + " - " + p.getNome() + " | " + p.getEspecialidade().getDescricao()
                    + " | " + (p.isAtivo() ? "Ativo" : "Desativado"));
        }
        int escolha = lerInteiro("Selecione seu cadastro (0 para novo): ");
        return escolha == 0 || escolha > lista.size() || escolha < 0 ? null : lista.get(escolha - 1);
    }

    private static Profissional selecionarMedico() {
        List<Profissional> lista = agenda.listarProfissionais();
        listarMedicos(lista);
        int escolha = lerInteiro("Selecione seu cadastro (0 para novo): ");
        return escolha == 0 || escolha > lista.size() || escolha < 0 ? null : lista.get(escolha - 1);
    }

    private static void listarMedicos(List<Profissional> medicos) {
        if (medicos.isEmpty()) {
            System.out.println("Nenhum médico cadastrado.");
            return;
        }
        for (int i = 0; i < medicos.size(); i++) {
            Profissional m = medicos.get(i);
            System.out.printf("%d - %s | %s | R$ %s%n", i + 1, m.getNome(), m.getEspecialidade().getDescricao(), m.getValorConsulta());
        }
    }

    private static void buscarMedicosPorEspecialidade() {
        EspecialidadeEnum especialidade = escolherEspecialidade();
        listarMedicos(agenda.listarProfissionais().stream().filter(m -> m.getEspecialidade() == especialidade).toList());
    }

    private static Profissional escolherMedicoDaLista() {
        List<Profissional> lista = agenda.listarProfissionais();
        if (lista.isEmpty()) {
            System.out.println("Nenhum médico cadastrado.");
            return null;
        }
        listarMedicos(lista);
        int escolha = lerInteiro("Escolha o médico: ");
        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("Médico inválido.");
            return null;
        }
        return lista.get(escolha - 1);
    }

    private static void mostrarHorarios() {
        Profissional medico = escolherMedicoDaLista();
        if (medico != null) {
            listarHorarios(medico);
        }
    }

    private static void listarHorarios(Profissional medico) {
        List<PeriodoRecord> periodos = agenda.listarHorariosDisponiveis(medico);
        if (periodos.isEmpty()) {
            System.out.println("Não há horários disponíveis cadastrados.");
            return;
        }
        System.out.println("Horários disponíveis de " + medico.getNome() + ":");
        for (int i = 0; i < periodos.size(); i++) System.out.println((i + 1) + " - " + formatar(periodos.get(i)));
    }

    private static void cadastrarHorarios(Profissional medico) {
        try {
            LocalDate data = lerData("Data (AAAA-MM-DD): ");
            LocalTime inicio = lerHora("Horário inicial (HH:mm): ");
            LocalTime fim = lerHora("Horário final (HH:mm): ");
            agenda.cadastrarDisponibilidade(medico, LocalDateTime.of(data, inicio), LocalDateTime.of(data, fim));
            System.out.println("Horários de uma hora cadastrados com sucesso!");
        } catch (DateTimeParseException | DominioException e) {
            System.out.println("Não foi possível cadastrar os horários: " + e.getMessage());
        }
    }

    private static void agendarConsulta(Paciente paciente) {
        Profissional medico = escolherMedicoDaLista();
        if (medico == null) {
            return;
        }
        List<PeriodoRecord> periodos = agenda.listarHorariosDisponiveis(medico);
        if (periodos.isEmpty()) {
            System.out.println("Esse médico não possui horários disponíveis.");
            return;
        }
        for (int i = 0; i < periodos.size(); i++) System.out.println((i + 1) + " - " + formatar(periodos.get(i)));
        int escolha = lerInteiro("Escolha o horário: ");
        if (escolha < 1 || escolha > periodos.size()) {
            System.out.println("Horário inválido.");
            return;
        }
        try {
            Consulta consulta = agenda.agendar(paciente, medico, periodos.get(escolha - 1));
            System.out.println("Consulta agendada! Código: " + consulta.getId());
            System.out.println("Valor: R$ " + consulta.getValorConsulta().valorComDesconto());
        } catch (DominioException e) {
            System.out.println("Não foi possível agendar: " + e.getMessage());
        }
    }

    private static void listarConsultasPaciente(Paciente paciente) {
        List<Consulta> consultas = agenda.listarConsultasDoPaciente(paciente);
        if (consultas.isEmpty()) {
            System.out.println("Você ainda não possui consultas.");
            return;
        }
        consultas.forEach(Main::imprimirConsulta);
    }
    private static void listarConsultasMedico(Profissional medico) {
        List<Consulta> consultas = agenda.listarConsultasDoProfissional(medico);
        if (consultas.isEmpty()) {
            System.out.println("Você ainda não possui consultas agendadas.");
            return;
        }
        consultas.forEach(Main::imprimirConsulta);
    }
    private static void agendarRetorno(Paciente paciente) {
        List<Consulta> anteriores = agenda.listarConsultasDoPaciente(paciente).stream()
                .filter(c -> c.getStatus() == StatusConsultaEnum.CONCLUIDA && c.getProximaConsulta() == null).toList();
        if (anteriores.isEmpty()) {
            System.out.println("Não há consultas concluídas aptas a gerar retorno.");
            return;
        }
        for (int i = 0; i < anteriores.size(); i++) {
            Consulta c = anteriores.get(i);
            System.out.println((i + 1) + " - " + c.getProfissional().getNome() + " | " + formatar(c.getPeriodo()));
        }
        int escolha = lerInteiro("Escolha a consulta anterior: ");
        if (escolha < 1 || escolha > anteriores.size()) {
            System.out.println("Opção inválida.");
            return;
        }
        Consulta anterior = anteriores.get(escolha - 1);
        List<PeriodoRecord> horarios = agenda.listarHorariosDisponiveis(anterior.getProfissional()).stream()
                .filter(p -> p.inicio().isAfter(anterior.getPeriodo().fim())
                        && !p.inicio().isAfter(anterior.getPeriodo().inicio().plusDays(30))).toList();
        if (horarios.isEmpty()) {
            System.out.println("Não há horários disponíveis para retorno dentro de 30 dias.");
            return;
        }
        for (int i = 0; i < horarios.size(); i++) System.out.println((i + 1) + " - " + formatar(horarios.get(i)));
        int horario = lerInteiro("Escolha o horário do retorno: ");
        if (horario < 1 || horario > horarios.size()) {
            System.out.println("Horário inválido.");
            return;
        }
        try {
            Consulta retorno = agenda.agendarRetorno(anterior.getId(), horarios.get(horario - 1));
            System.out.println("Retorno agendado! Valor com desconto: R$ " + retorno.getValorConsulta().valorComDesconto());
        } catch (DominioException e) {
            System.out.println("Não foi possível agendar o retorno: " + e.getMessage());
        }
    }

    private static void cancelarConsulta(Paciente paciente) {
        List<Consulta> consultas = agenda.listarConsultasDoPaciente(paciente).stream().filter(c -> c.getStatus() == StatusConsultaEnum.AGENDADA).toList();
        if (consultas.isEmpty()) {
            System.out.println("Não há consultas agendadas para cancelar.");
            return;
        }
        for (int i = 0; i < consultas.size(); i++) System.out.println((i + 1) + " - " + formatar(consultas.get(i).getPeriodo()) + " | " + consultas.get(i).getProfissional().getNome());
        int escolha = lerInteiro("Escolha a consulta (0 para voltar): ");
        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > consultas.size()) {
            System.out.println("Opção inválida.");
            return;
        }
        try {
            System.out.println("Política de cancelamento: 1 - Livre | 2 - Exige 24 horas de antecedência");
            int opcaoPolitica = lerInteiro("Escolha: ");
            PoliticaCancelamento politica = switch (opcaoPolitica) {
                case 1 -> new CancelamentoLivreStrategy();
                case 2 -> new CancelamentoComAntecedenciaStrategy();
                default -> throw new DominioException("Política inválida.");
            };
            agenda.cancelarConsulta(consultas.get(escolha - 1).getId(), paciente, politica, LocalDateTime.now());
            System.out.println("Consulta cancelada. O horário foi liberado.");
        } catch (DominioException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void concluirConsulta(Profissional profissional) {
        List<Consulta> consultas = agenda.listarConsultasDoProfissional(profissional).stream()
                .filter(c -> c.getStatus() == StatusConsultaEnum.AGENDADA).toList();
        if (consultas.isEmpty()) {
            System.out.println("Não há consultas agendadas para concluir.");
            return;
        }
        for (int i = 0; i < consultas.size(); i++) {
            System.out.print((i + 1) + " - ");
            imprimirConsulta(consultas.get(i));
        }
        int escolha = lerInteiro("Informe o número da consulta na lista (1 a " + consultas.size() + "): ");
        if (escolha < 1 || escolha > consultas.size()) {
            System.out.println("Opção inválida.");
            return;
        }
        try { agenda.concluirConsulta(consultas.get(escolha - 1).getId()); System.out.println("Consulta concluída."); }
        catch (DominioException e) { System.out.println(e.getMessage()); }
    }

    private static void imprimirConsulta(Consulta c) {
        System.out.println("Consulta: " + c.getId() + " | " + formatar(c.getPeriodo()) + " | Paciente: " + c.getPaciente().getNome()
                + " | Médico: " + c.getProfissional().getNome() + " | Status: " + c.getStatus().getDescricao()
                + " | Valor: R$ " + (c.getValorConsulta() == null ? "não calculado" : c.getValorConsulta().valorComDesconto()));
    }
    private static String formatar(PeriodoRecord periodo) {
        return periodo.inicio().toLocalDate() + " "
                + periodo.inicio().toLocalTime() + " às "
                + periodo.fim().toLocalTime();
    }

    private static boolean confirmar(String pergunta) {
        return lerTexto(pergunta).equalsIgnoreCase("s");
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static BigDecimal lerDinheiro(String mensagem) {
        while (true) {
            try {
                BigDecimal valor = new BigDecimal(lerTexto(mensagem).replace(',', '.'));

                if (valor.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException();
                }

                return valor.setScale(2, java.math.RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor válido e não negativo. Ex.: 150,00");
            }
        }
    }

    private static LocalDate lerData(String mensagem) {
        while (true) {
            try {
                return LocalDate.parse(lerTexto(mensagem));
            } catch (DateTimeParseException e) {
                System.out.println("Use o formato AAAA-MM-DD.");
            }
        }
    }

    private static LocalTime lerHora(String mensagem) {
        while (true) {
            try {
                return LocalTime.parse(lerTexto(mensagem));
            } catch (DateTimeParseException e) {
                System.out.println("Use o formato HH:mm.");
            }
        }
    }
}
