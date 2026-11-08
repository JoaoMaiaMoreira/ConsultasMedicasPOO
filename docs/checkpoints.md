# Matriz de atendimento aos checkpoints

Este documento relaciona os itens do enunciado aos arquivos e testes do projeto. A avaliação final depende da execução dos testes em JDK 25 e da demonstração pelos integrantes.

## Checkpoint 1 — Modelagem, responsabilidades e invariantes

| Exigência | Evidência no projeto |
|---|---|
| Identidade e encapsulamento | `Paciente`, `Profissional` e `Consulta` usam UUID e atributos privados. |
| Objeto de valor imutável | `PeriodoRecord` e `ValorConsultaRecord`. |
| Período válido e sobreposição | `PeriodoRecord` e `PeriodoRecordTest`. |
| Dados obrigatórios | Construtores de `Paciente`, `Profissional` e `Consulta`. |
| Inatividade | `Profissional.ativar/desativar` e validações de agenda. |
| Testes iniciais | Testes em `src/test/java/consulta/dominio`. |

## Checkpoint 2 — Composição, polimorfismo, genéricos e consultas

| Exigência | Evidência no projeto |
|---|---|
| Composição | `Consulta` contém paciente, profissional, período e valor. |
| Polimorfismo real | `CalculadoraPreco` possui duas estratégias; `PoliticaCancelamento` possui duas políticas. |
| Abstração genérica | `Repositorio<T, ID>` e `RepositorioEmMemoria<T, ID>`. |
| Conflitos e disponibilidade | `AgendaService` e `AgendaServiceTest`. |
| Streams e lambdas | `AgendaService.consultar`, filtros por paciente/profissional e ordenação. |
| Exceções tratáveis | `DominioException`. |

## Checkpoint 3 — Persistência, padrão, refatoração e testes

| Exigência | Evidência no projeto |
|---|---|
| Persistência aberta | `ArquivoAgendaJson`, arquivo `dados/agenda.json`. |
| Validação ao restaurar | Métodos `restaurar*` de `AgendaService` verificam IDs, CPF, horários e conflitos. |
| Padrão justificado | Strategy no cálculo de preço e nas políticas de cancelamento. |
| Testes de persistência | `ArquivoAgendaJsonTest`. |
| Testes de políticas | `PoliticasCancelamentoTest`. |
| Testes de integração do retorno | `AgendaServiceTest` e `ConsultaTest`. |

## Entrega final e demonstração

- `README.md`: variante, tecnologias, requisitos e comandos.
- `docs/decisoes.md`: decisões e alternativas descartadas.
- `docs/classes.mmd`: diagrama Mermaid.
- `IA_USAGE.md`: registro de uso de IA e verificações pendentes.
- `Main`: menus de paciente e profissional, agendamento, retorno, cancelamento, conclusão e relatório.
