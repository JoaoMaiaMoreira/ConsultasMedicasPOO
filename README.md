# Plataforma de Reservas - Agendamento de Consultas Médicas

## Equipe

- Integrante 1: João Vitor Maia Moreira.
  
## Variante

- **Variante**: Serviços e Atendimentos (Agendamento de Consultas Médicas).
- **Regra específica**: Uma consulta de retorno (`proximaConsulta`) deve ser agendada no prazo máximo de 30 dias após a `consultaAnterior` e pertencer obrigatoriamente ao mesmo paciente e profissional.

## Requisitos atendidos

### Checkpoint 1

- **RF1 (Participantes)**: Cadastro e consulta de pacientes com identificador único (`UUID`) e validação de dados obrigatórios.
- **RF2 (Recursos)**: Cadastro e controlo de estado (`ativo`/`inativo`) dos profissionais de saúde.
- **RF3 (Períodos)**: Representação de intervalo temporal através da classe imutável `PeriodoRecord` (`record`), com validação e deteção de sobreposição de horários.
- **RF4 (Reserva)**: Associação entre paciente, profissional e período, garantindo a recusa de agendamentos para profissionais inativos ou com conflito de agenda.
- **RF5 (Ciclo de vida)**: Controlo de estados da consulta (`AGENDADA`, `CONCLUIDA`, `CANCELADA`) através de `StatusConsultaEnum`, impedindo transições inválidas.
- **RF7 (Preço ou custo)**: Encapsulamento dos valores monetários em `ValorConsulta` utilizando estritamente `BigDecimal` (sem uso de `double`), preparado para estratégias de cálculo polimórficas via `CalculadoraPreco`.

## Invariantes Preservadas pelo Código

1. **Validade do Período**: O horário de início do período deve ser estritamente anterior ao horário de fim (`inicio < fim`).
2. **Dados Obrigatórios**: Identificadores (`UUID`), nomes, CPFs e campos fundamentais de cadastro não podem ser nulos nem vazios.
3. **Disponibilidade do Recurso**: Profissionais inativos não podem receber novos agendamentos de consulta.
4. **Ausência de Sobreposição**: Não são permitidos dois agendamentos ativos no mesmo intervalo de tempo para o mesmo profissional.
5. **Limites Monetários**: Todos os valores monetários calculados e salvos devem ser maiores ou iguais a zero (`>= 0`).
6. **Consistência do Ciclo de Vida**: Transições de estado são definitivas (uma consulta concluída ou cancelada não pode regressar ao estado `AGENDADA`).
7. **Invariante Própria da Variante**: O agendamento de uma consulta de retorno exige que o intervalo entre as consultas não ultrapasse 30 dias e que ambas partilhem o mesmo paciente e o mesmo profissional.

## Como executar

Para executar a suíte de testes unitários no Linux/macOS:

```bash
./mvnw test
