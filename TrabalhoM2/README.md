# Trabalho M2 – Interpretador de binários (GALs)

Linguagem para números binários inteiros sem sinal com atribuição, exibição (`Show`) e as operações soma, subtração, multiplicação, divisão, exponenciação e log.

## Arquivos

| Arquivo | Conteúdo |
| --- | --- |
| `gramatica.gals` | Especificação para o GALs: tokens, gramática LL(1) e ações semânticas (`#1`..`#11`) |
| `Lexico.java`, `Sintatico.java`, `Constants.java`, `ScannerConstants.java`, `ParserConstants.java`, `Token.java`, `*Error.java` | Analisadores no formato gerado pelo GALs (opções: Java, LL, Input = String) |
| `Semantico.java` | Interpretador, com a implementação das ações semânticas |
| `Main.java` | Programa principal que liga léxico, sintático e semântico |
| `exemplo.txt`, `teste_operacoes.txt` | Programas de exemplo |

Ao abrir `gramatica.gals` no GALs e gerar o código de novo, os arquivos gerados substituem os desta pasta. O `Semantico.java` e o `Main.java` continuam compatíveis.

## Compilar e executar

```powershell
javac -encoding UTF-8 *.java
java Main                      # modo interativo: digita no terminal
java Main exemplo.txt          # interpreta um arquivo
```

### Modo interativo

Ao iniciar, o programa mostra as instruções de uso (comandos, operações e um exemplo). Elas podem ser exibidas de novo com `:ajuda`. As mensagens no terminal não têm acentos, para aparecerem certas em qualquer console.

Cada linha digitada passa pelo léxico, pelo sintático e pelo semântico, e as variáveis continuam valendo entre as linhas. Nesse modo:

- o `;` no final é opcional;
- toda atribuição mostra o valor que a variável recebeu;
- uma expressão digitada sozinha é exibida, como se fosse `Show ( expr );`;
- em caso de erro, um `^` aponta onde ele ocorreu.

```text
>> A = 10;
A = 10
>> B = 11
B = 11
>> B = 111 + A * B;
B = 1101
>> Show ( B );
1101
>> log(1000) ^ 10
1001
>> A - 1000
   Show ( A - 1000 );
              ^
Erro semantico: Subtracao com resultado negativo (10 - 1000)
>> :vars
A = 10   (decimal 2)
B = 1101   (decimal 13)
>> FIM
```

| Comando | Efeito |
| --- | --- |
| `:vars` | lista as variáveis em binário e decimal |
| `:limpar` | apaga todas as variáveis |
| `:ajuda` | mostra a ajuda |
| `FIM` ou `:sair` | encerra |

## Linguagem

```text
A = 10;
B = 11;
B = 111 + A * B;
Show ( B );
```

| Operação | Sintaxe | Observação |
| --- | --- | --- |
| Soma | `a + b` | |
| Subtração | `a - b` | erro se o resultado for negativo |
| Multiplicação | `a * b` | |
| Divisão | `a / b` | divisão inteira; erro se `b = 0` |
| Exponenciação | `a ^ b` | associativa à direita |
| Logaritmo | `log ( a )` | `floor(log2 a)`; erro se `a = 0` |

Precedência, da maior para a menor: parênteses e `log`, `^`, `*` e `/`, `+` e `-`.

- Números: somente `0` e `1` (ex.: `1010`).
- Variáveis: começam com letra ou `_` e podem ter letras, dígitos e `_`.
- `Show` e `log` são palavras reservadas.
- Comentários: `// até o fim da linha`.
- Usar uma variável antes de atribuir valor a ela gera erro semântico.

## Gramática (LL(1))

```text
<programa>  ::= <lista_cmd>
<lista_cmd> ::= <cmd> <lista_cmd> | î
<cmd>       ::= ID #1 "=" <expr> ";" #2
              | Show "(" <expr> ")" ";" #3
<expr>      ::= <termo> <expr_r>
<expr_r>    ::= "+" <termo> #4 <expr_r> | "-" <termo> #5 <expr_r> | î
<termo>     ::= <pot> <termo_r>
<termo_r>   ::= "*" <pot> #6 <termo_r> | "/" <pot> #7 <termo_r> | î
<pot>       ::= <fator> <pot_r>
<pot_r>     ::= "^" <pot> #8 | î
<fator>     ::= BIN #9 | ID #10 | "(" <expr> ")" | log "(" <expr> ")" #11
```

## Ações semânticas

O interpretador usa uma pilha de valores (`BigInteger`) e uma tabela de variáveis.

| Ação | Efeito |
| --- | --- |
| #1 | guarda o nome da variável de destino |
| #2 | desempilha o valor e o atribui à variável |
| #3 | desempilha o valor e o exibe em binário |
| #4 – #8 | desempilham dois operandos e empilham `+`, `-`, `*`, `/`, `^` |
| #9 | empilha o número binário lido |
| #10 | empilha o valor da variável |
| #11 | desempilha o valor e empilha `log2` dele |
