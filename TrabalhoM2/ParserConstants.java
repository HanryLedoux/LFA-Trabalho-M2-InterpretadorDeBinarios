public interface ParserConstants
{
    int START_SYMBOL = 15;

    int FIRST_NON_TERMINAL    = 15;
    int FIRST_SEMANTIC_ACTION = 25;

    // Linhas: <programa> .. <fator>
    // Colunas: $ ID BIN Show log "=" ";" "(" ")" "+" "-" "*" "/" "^"
    int[][] PARSER_TABLE =
    {
        {  0,  0, -1,  0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // <programa>
        {  2,  1, -1,  1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // <lista_cmd>
        { -1,  3, -1,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // <cmd>
        { -1,  5,  5, -1,  5, -1, -1,  5, -1, -1, -1, -1, -1, -1 }, // <expr>
        { -1, -1, -1, -1, -1, -1,  8, -1,  8,  6,  7, -1, -1, -1 }, // <expr_r>
        { -1,  9,  9, -1,  9, -1, -1,  9, -1, -1, -1, -1, -1, -1 }, // <termo>
        { -1, -1, -1, -1, -1, -1, 12, -1, 12, 12, 12, 10, 11, -1 }, // <termo_r>
        { -1, 13, 13, -1, 13, -1, -1, 13, -1, -1, -1, -1, -1, -1 }, // <pot>
        { -1, -1, -1, -1, -1, -1, 15, -1, 15, 15, 15, 15, 15, 14 }, // <pot_r>
        { -1, 17, 16, -1, 19, -1, -1, 18, -1, -1, -1, -1, -1, -1 }  // <fator>
    };

    int[][] PRODUCTIONS =
    {
        { 16 },                     //  0 <programa>  ::= <lista_cmd>
        { 17, 16 },                 //  1 <lista_cmd> ::= <cmd> <lista_cmd>
        {  0 },                     //  2 <lista_cmd> ::= î
        {  2, 26,  6, 18,  7, 27 }, //  3 <cmd>       ::= ID #1 "=" <expr> ";" #2
        {  4,  8, 18,  9,  7, 28 }, //  4 <cmd>       ::= Show "(" <expr> ")" ";" #3
        { 20, 19 },                 //  5 <expr>      ::= <termo> <expr_r>
        { 10, 20, 29, 19 },         //  6 <expr_r>    ::= "+" <termo> #4 <expr_r>
        { 11, 20, 30, 19 },         //  7 <expr_r>    ::= "-" <termo> #5 <expr_r>
        {  0 },                     //  8 <expr_r>    ::= î
        { 22, 21 },                 //  9 <termo>     ::= <pot> <termo_r>
        { 12, 22, 31, 21 },         // 10 <termo_r>   ::= "*" <pot> #6 <termo_r>
        { 13, 22, 32, 21 },         // 11 <termo_r>   ::= "/" <pot> #7 <termo_r>
        {  0 },                     // 12 <termo_r>   ::= î
        { 24, 23 },                 // 13 <pot>       ::= <fator> <pot_r>
        { 14, 22, 33 },             // 14 <pot_r>     ::= "^" <pot> #8
        {  0 },                     // 15 <pot_r>     ::= î
        {  3, 34 },                 // 16 <fator>     ::= BIN #9
        {  2, 35 },                 // 17 <fator>     ::= ID #10
        {  8, 18,  9 },             // 18 <fator>     ::= "(" <expr> ")"
        {  5,  8, 18,  9, 36 }      // 19 <fator>     ::= log "(" <expr> ")" #11
    };

    String[] PARSER_ERROR =
    {
        "",
        "Era esperado fim de programa",
        "Era esperado um identificador",
        "Era esperado um numero binario",
        "Era esperado Show",
        "Era esperado log",
        "Era esperado \"=\"",
        "Era esperado \";\"",
        "Era esperado \"(\"",
        "Era esperado \")\"",
        "Era esperado \"+\"",
        "Era esperado \"-\"",
        "Era esperado \"*\"",
        "Era esperado \"/\"",
        "Era esperado \"^\"",
        "Era esperado um comando (atribuicao ou Show)", // <programa>
        "Era esperado um comando (atribuicao ou Show)", // <lista_cmd>
        "Era esperado um comando (atribuicao ou Show)", // <cmd>
        "Era esperada uma expressao",                   // <expr>
        "Era esperado operador, \";\" ou \")\"",        // <expr_r>
        "Era esperada uma expressao",                   // <termo>
        "Era esperado operador, \";\" ou \")\"",        // <termo_r>
        "Era esperada uma expressao",                   // <pot>
        "Era esperado operador, \";\" ou \")\"",        // <pot_r>
        "Era esperado numero, variavel, \"(\" ou log"  // <fator>
    };
}
