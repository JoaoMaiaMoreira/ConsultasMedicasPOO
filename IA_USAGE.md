# Registro de uso de inteligência artificial

Registre usos materiais de IA que tenham influenciado código, testes,
documentação ou decisões.

## Modelo de registro

### 1. Definição da Estrutura de Pastas e Arquitetura

- **Data:** 06/10/2026
- **Ferramenta:** IA / Assistant
- **Objetivo:** Definir a melhor organização de pacotes para o projeto em Orientação a Objetos pura.
- **Parte do projeto afetada:** Arquitetura do projeto e estrutura de diretórios.
- **Resultado aproveitado:** Como eu estava acostumado com o padrão em camadas tradicional do Maven/Spring (`model`, `service`, `controller`), a IA explicou que essa divisão não era adequada para um projeto de POO pura voltado ao domínio. A IA auxiliou na reestruturação para uma arquitetura orientada ao domínio (`dominio`, `estrategia`, `excecao`).
- **Verificações executadas:** Validação da estrutura das pastas e compilação do projeto sem dependências de frameworks externos.
- **Correções ou decisões da equipe:** Decidi seguir a sugestão da IA para manter a lógica de negócios e as invariantes encapsuladas diretamente nas entidades do domínio.

---

### 2. Escolha e Aplicação do Padrão Strategy (Polimorfismo)

- **Data:** 06/10/2026
- **Ferramenta:** IA / Assistant
- **Objetivo:** Escolher a melhor abordagem de polimorfismo para o cálculo de preços e regras de desconto das consultas.
- **Parte do projeto afetada:** Pacote `consulta.estrategia` (`CalculadoraPreco`, `PrecoFixoStrategy`, `PrecoDescontoRetornoStrategy`) e classe `Consulta`.
- **Resultado aproveitado:** A IA auxiliou na tomada de decisão pela escolha do padrão **Strategy**, permitindo isolar os diferentes cálculos de preço e manter o código extensível para novos descontos sem alterar as entidades.
- **Verificações executadas:** Execução dos testes unitários com JUnit para validar os cálculos de preço fixo e desconto de retorno.
- **Correções ou decisões da equipe:** Criação da interface `CalculadoraPreco` integrada diretamente com o método de aplicar preço na classe `Consulta`.

---

### 3. Formatação de Textos, Exemplos de Código e Documentação

- **Data:** 06/10/2026
- **Ferramenta:** IA / Assistant
- **Objetivo:** Melhorar a escrita das explicações e organizar os arquivos Markdown.
- **Parte do projeto afetada: Documentação do projeto (`.md`) e mensagens de exceção do sistema.
- **Resultado aproveitado:** A IA ajudou a reescrever e refatorar os textos para ficarem mais claros e diretos, formatou os exemplos práticos de uso do código na classe `Main` e organizou a documentação de decisões do projeto.
- **Verificações executadas:** Leitura final dos textos gerados, verificação da legibilidade do código e execução com sucesso da classe `Main`.
- **Correções ou decisões da equipe:** Ajustada a criação dos objetos `BigDecimal` para garantir precisão exata nos valores monetários informados nos exemplos.