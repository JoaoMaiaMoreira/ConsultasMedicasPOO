# Decisões do projeto

Registre decisões que afetem o modelo, a API, as dependências ou a evolução.

## Modelo de registro

### 1. Uso de Java Records para Objetos de Valor Imutáveis

- **Data:** 04/10/2026
- **Problema observado:** Precisávamos de estruturas para guardar intervalos de datas (`PeriodoRecord`) e detalhes financeiros (`ValorConsultaRecord`) sem permitir alterações acidentais dos dados e sem escrever muito código repetitivo.
- **Alternativas consideradas:** Criar classes Java tradicionais com atributos `private final` e métodos manuais, ou usar a funcionalidade de `record`.
- **Decisão:** Usar `record` do Java para todos os Objetos de Valor do sistema.
- **Consequências:** Garantimos imutabilidade por padrão, código limpo e sem necessidade de gerar getters, `equals()` ou `hashCode()` manualmente.
- **Teste ou evidência que verifica a decisão:** Testes unitários na classe `PeriodoRecordTest`.

---

### 2. Padrão Strategy para Cálculo de Preço das Consultas

- **Data:** 04/10/2026
- **Problema observado:** O valor de uma consulta pode mudar dependendo de regras como preço fixo ou desconto para consultas de retorno. Colocar esses `if/else` dentro da classe `Consulta` deixaria o código confuso.
- **Alternativas consideradas:** Criar vários métodos de cálculo dentro da entidade `Consulta` ou isolar os cálculos usando o padrão de projeto Strategy.
- **Decisão:** Criar a interface `CalculadoraPreco` com implementações separadas (`PrecoFixoStrategy` e `PrecoDescontoRetornoStrategy`).
- **Consequências:** A classe `Consulta` fica livre de regras complexas de cobrança e fica fácil adicionar novas formas de desconto no futuro sem mexer no código existente.
- **Teste ou evidência que verifica a decisão:** Testes unitários em `PrecoFixoStrategyTest` e `PrecoDescontoRetornoStrategyTest`.

---

### 3. Validação de Invariantes e Regra de Retorno no Próprio Domínio

- **Data:** 06/10/2026
- **Problema observado:** Precisávamos garantir regras de negócio rígidas, como proibir agendamento para médicos inativos e limitar o retorno ao mesmo médico, mesmo paciente e prazo máximo de 30 dias.
- **Alternativas consideradas:** Validar essas regras na classe `Main` ou colocar a validação dentro das próprias entidades.
- **Decisão:** Colocar todas as validações dentro dos construtores e métodos das entidades (`Consulta`, `Paciente`, `Profissional`), lançando a exceção `DominioException` em caso de erro.
- **Consequências:** Impossibilita a criação de consultas ou cadastros inválidos no sistema, garantindo a integridade dos dados em qualquer lugar da aplicação.
- **Teste ou evidência que verifica a decisão:** Testes em `ConsultaTest` tratando cenários de erro com `assertThrows`.

---

### 4. Uso do BigDecimal para Precisão Financeira

- **Data:** 06/10/2026
- **Problema observado:** Tipos primitivos como `double` ou `float` causam arredondamentos incorretos em valores decimais/monetários devido à representação em ponto flutuante.
- **Alternativas consideradas:** Usar `double` ou usar `BigDecimal` inicializado com `String` ou `BigDecimal.valueOf()`.
- **Decisão:** Utilizar estritamente `BigDecimal` para guardar e calcular qualquer valor em dinheiro no sistema.
- **Consequências:** Operações de desconto e cálculo de preço ficam com precisão exata de centavos, evitando erros financeiros.
- **Teste ou evidência que verifica a decisão:** Verificação das asserções financeiras exatas nos testes de cálculo de preço.
---

### 5. Serviço de agenda e disponibilidade em blocos de uma hora
- **Data:** 01/11/2026
- **Problema observado:** a entidade `Consulta` representa um agendamento, mas não havia um componente responsável por administrar os horários oferecidos e impedir conflitos entre consultas.
- **Alternativas consideradas:** concentrar as regras na `Main` ou criar um serviço de aplicação dedicado.
- **Decisão:** criar `AgendaService`, responsável por registrar disponibilidade, listar horários livres, agendar e cancelar consultas. Cada disponibilidade é dividida em blocos de uma hora.
- **Consequências:** a `Main` fica responsável pela interação, enquanto as regras de agendamento permanecem fora da interface. O cancelamento torna o horário disponível novamente.
- **Teste ou evidência:** `AgendaServiceTest` verifica preço individual, conflito e liberação após cancelamento.

### 6. Repositório genérico em memória
- **Data:** 01/11/2026
- **Problema observado:** o serviço precisava armazenar diferentes tipos de entidades sem duplicar a lógica básica de busca e armazenamento.
- **Decisão:** criar `Repositorio<T, ID>` e `RepositorioEmMemoria<T, ID>`, recebendo uma função para extrair o identificador da entidade.
- **Consequências:** a mesma abstração pode armazenar pacientes, profissionais e consultas. A implementação é temporária e não substitui a persistência em arquivo prevista para etapa posterior.


### 7. Persistência em JSON com Adapter simples

- **Data:** 08/11/2026
- **Problema observado:** o projeto precisava preservar cadastros, horários e consultas entre execuções, sem utilizar banco de dados ou ORM.
- **Alternativas consideradas:** serialização Java nativa, banco relacional/ORM ou arquivo JSON.
- **Decisão:** usar JSON legível por meio de `ArquivoAgendaJson`, com Jackson apenas para leitura e escrita. O domínio continua sem depender da biblioteca.
- **Consequências:** os dados ficam inspecionáveis e podem ser restaurados com as validações das entidades. O arquivo não deve conter dados pessoais reais.
- **Verificação:** `ArquivoAgendaJsonTest` cobre gravação e restauração. A equipe deve executar `./mvnw clean test` com JDK 25.

### 8. Strategy para políticas de cancelamento

- **Data:** 08/11/2026
- **Problema observado:** o sistema precisa aceitar regras de cancelamento diferentes sem espalhar condicionais pelo serviço.
- **Alternativas consideradas:** implementar regras diretamente em `AgendaService` ou definir um contrato com implementações substituíveis.
- **Decisão:** criar `PoliticaCancelamento`, `CancelamentoLivre` e `CancelamentoComAntecedencia`.
- **Consequências:** a política é escolhida no momento da operação e o serviço permanece independente da regra concreta.
- **Verificação:** `PoliticasCancelamentoTest` cobre aceitação e rejeição das duas políticas.

### 9. Regra de retorno como regra do domínio

- **Data:** 08/11/2026
- **Problema observado:** o retorno precisa manter paciente e profissional e respeitar prazo de até 30 dias.
- **Decisão:** manter a validação do vínculo em `Consulta.vincularProxima` e coordenar o fluxo em `AgendaService.agendarRetorno`.
- **Consequências:** a regra não depende da interface de terminal; o retorno recebe desconto de 10% após a validação.
- **Verificação:** a equipe deve testar retorno válido, paciente/profissional diferentes, prazo superior a 30 dias e tentativa de criar mais de um retorno.
