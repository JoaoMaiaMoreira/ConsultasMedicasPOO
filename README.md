# Plataforma de Reservas — Agendamento de Consultas Médicas

# Github

https://github.com/JoaoMaiaMoreira/ConsultasMedicasPOO

## Variante
**Serviços e atendimentos.** O recurso disputado é a agenda do profissional de saúde.

## Regra específica do domínio
Uma consulta de retorno deve pertencer ao mesmo paciente e profissional da consulta anterior e ocorrer em até 30 dias.

## Funcionalidades implementadas
- Cadastro de pacientes com identificador UUID e validação de dados obrigatórios.
- Cadastro de médicos com especialidade e valor individual da consulta.
- Cadastro de disponibilidade em blocos consecutivos de uma hora.
- Consulta de médicos por especialidade e visualização de horários disponíveis.
- Agendamento apenas em horários cadastrados pelo médico.
- Bloqueio de conflitos para o mesmo médico e para o mesmo paciente.
- Cancelamento de consultas, liberando novamente o horário.
- Consulta dos agendamentos do paciente e do médico.
- Cálculo do preço com `BigDecimal` e estratégia de preço já existente no projeto.
- Repositório genérico `Repositorio<T, ID>` com implementação em memória.
- Interface de terminal usando `Scanner`.

## Execução
Requisito do enunciado: Java 25 LTS.

```bash
./mvnw test
./mvnw package
```

Para executar a aplicação pela IDE, execute a classe `consulta.Main`. Alternativamente, no terminal:

```bash
./mvnw exec:java -Dexec.mainClass=consulta.Main
```

No Windows, substitua `./mvnw` por `mvnw.cmd`.

Os dados são carregados de `dados/agenda.json` ao iniciar e salvos ao encerrar o programa. O arquivo é JSON legível, sem PostgreSQL, JPA ou Hibernate. Na primeira execução, se não existir arquivo, o sistema começa com uma agenda vazia.

## Estrutura principal
- `consulta.dominio`: entidades e objetos de valor do domínio.
- `consulta.estrategia`: contrato e estratégias de cálculo de preço.
- `consulta.repositorio`: abstração genérica e repositório em memória.
- `consulta.servico`: regras de agenda e coordenação dos agendamentos.
- `consulta.Main`: menus e interação com o usuário.

## Verificação
A suíte de testes automatizados cobre as entidades, as estratégias de preço e os cenários de agendamento, conflito e cancelamento.

## Checkpoints e entrega final
- Checkpoint 1: entidades, objetos de valor, validações e testes.
- Checkpoint 2: composição, polimorfismo, repositório genérico, filtros e consultas.
- Checkpoint 3: estratégias de cancelamento, JSON, restauração e testes.
- Entrega final: menu, relatórios, documentação e diagrama em `docs/classes.mmd`.

Consulte `docs/checkpoints.md` para as evidências detalhadas.

**Atenção:** o projeto exige JDK 25 e acesso ao Maven Central para baixar dependências na primeira execução.

