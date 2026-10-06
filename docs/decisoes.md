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