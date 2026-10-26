public interface ScannerConstants
{
    // Token reconhecido em cada estado do automato (-1 = estado nao final, 0 = ignorado)
    int[] TOKEN_STATE =
    {
        -1, // 0  inicial
         2, // 1  ID
         3, // 2  BIN
         6, // 3  "="
         7, // 4  ";"
         8, // 5  "("
         9, // 6  ")"
        10, // 7  "+"
        11, // 8  "-"
        12, // 9  "*"
        13, // 10 "/"
        14, // 11 "^"
         0, // 12 espacos
         0  // 13 comentario "//"
    };

    // Casos especiais do token ID (palavras reservadas)
    String[] SPECIAL_CASES_KEYS = { "Show", "log" };
    int[] SPECIAL_CASES_VALUES = { 4, 5 };

    String[] SCANNER_ERROR =
    {
        "Caractere nao esperado",
        "", "", "", "", "", "", "", "", "", "", "", "", ""
    };
}
