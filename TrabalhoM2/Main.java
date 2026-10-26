import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/*
 * Uso:
 *   java Main                -> modo interativo: digite os comandos e expressoes no terminal
 *   java Main programa.txt   -> interpreta um arquivo
 */
public class Main
{
    private static final PrintStream out = System.out;

    public static void main(String[] args) throws IOException
    {
        if (args.length > 0)
        {
            String fonte = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
            if (!executar(fonte, new Semantico(out), false))
                System.exit(1);
        }
        else
        {
            interativo();
        }
    }

    private static void interativo() throws IOException
    {
        // O mesmo Semantico e reutilizado, entao as variaveis persistem entre as linhas
        Semantico semantico = new Semantico(out);
        semantico.setEcoAtribuicoes(true);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        out.println("==============================================================");
        out.println("   INTERPRETADOR DE NUMEROS BINARIOS - Trabalho M2 de LFA");
        out.println("==============================================================");
        instrucoes();

        while (true)
        {
            out.print(">> ");
            out.flush();
            String linha = in.readLine();
            if (linha == null)
                break;

            linha = linha.trim();
            if (linha.isEmpty())
                continue;
            if (linha.equals("FIM") || linha.equals(":sair"))
                break;

            if (linha.startsWith(":"))
                comandoInterno(linha, semantico);
            else
                executar(completar(linha), semantico, true);
        }

        out.println("Encerrado.");
    }

    private static void instrucoes()
    {
        out.println();
        out.println("COMO USAR");
        out.println("  Digite um comando por linha e pressione ENTER.");
        out.println("  Todos os numeros devem ser escritos em binario (somente 0 e 1).");
        out.println("  O ponto e virgula (;) no final da linha e opcional.");
        out.println();
        out.println("COMANDOS DA LINGUAGEM");
        out.println("  A = 1010;             atribui um valor ou expressao a variavel A");
        out.println("  Show ( A );           exibe o valor de A em binario");
        out.println("  111 + A * 10          expressao sozinha: exibe o resultado");
        out.println();
        out.println("OPERACOES (da maior para a menor precedencia)");
        out.println("  ( )      parenteses           log(x)  logaritmo na base 2");
        out.println("  ^        exponenciacao");
        out.println("  *   /    multiplicacao e divisao inteira");
        out.println("  +   -    soma e subtracao (o resultado nao pode ser negativo)");
        out.println();
        out.println("COMANDOS DO TERMINAL");
        out.println("  :vars     lista as variaveis (binario e decimal)");
        out.println("  :limpar   apaga todas as variaveis");
        out.println("  :ajuda    mostra estas instrucoes novamente");
        out.println("  FIM       encerra o programa");
        out.println();
        out.println("EXEMPLO");
        out.println("  >> A = 10");
        out.println("  >> B = 11");
        out.println("  >> B = 111 + A * B");
        out.println("  >> Show ( B )         (mostra 1101)");
        out.println();
    }

    /*
     * Facilita a digitacao no terminal:
     *  - o ";" final e opcional;
     *  - uma expressao sozinha (sem "=" e sem Show) e exibida, como se fosse Show ( expr );
     */
    private static String completar(String linha)
    {
        boolean temPontoVirgula = linha.endsWith(";");
        String semPontoVirgula = temPontoVirgula ? linha.substring(0, linha.length() - 1) : linha;

        if (!linha.contains("=") && !linha.startsWith("Show") && !semPontoVirgula.contains(";"))
            return "Show ( " + semPontoVirgula + " );";

        return temPontoVirgula ? linha : linha + ";";
    }

    private static void comandoInterno(String linha, Semantico semantico)
    {
        switch (linha)
        {
            case ":vars":
                Map<String, BigInteger> variaveis = semantico.getVariaveis();
                if (variaveis.isEmpty())
                    out.println("Nenhuma variavel definida.");
                for (Map.Entry<String, BigInteger> v : variaveis.entrySet())
                    out.println(v.getKey() + " = " + v.getValue().toString(2) + "   (decimal " + v.getValue() + ")");
                break;

            case ":limpar":
                semantico.getVariaveis().clear();
                out.println("Variaveis apagadas.");
                break;

            case ":ajuda":
                instrucoes();
                break;

            default:
                out.println("Comando desconhecido: " + linha + " (digite :ajuda para ver as instrucoes)");
        }
    }

    private static boolean executar(String fonte, Semantico semantico, boolean interativo)
    {
        // ignora o BOM que editores do Windows colocam no inicio do arquivo
        if (fonte.startsWith("﻿"))
            fonte = fonte.substring(1);

        Lexico lexico = new Lexico(fonte);
        Sintatico sintatico = new Sintatico();

        try
        {
            sintatico.parse(lexico, semantico);
            return true;
        }
        catch (LexicalError e)
        {
            erro("lexico", e, fonte, interativo);
        }
        catch (SyntaticError e)
        {
            erro("sintatico", e, fonte, interativo);
        }
        catch (SemanticError e)
        {
            erro("semantico", e, fonte, interativo);
        }
        return false;
    }

    private static void erro(String tipo, AnalysisError e, String fonte, boolean interativo)
    {
        if (interativo)
        {
            // aponta a posicao do erro na linha digitada
            out.println("   " + fonte);
            out.println("   " + " ".repeat(Math.max(0, Math.min(e.getPosition(), fonte.length()))) + "^");
            out.println("Erro " + tipo + ": " + e.getMessage());
            return;
        }

        int linha = 1, coluna = 1;
        for (int i = 0; i < e.getPosition() && i < fonte.length(); i++)
        {
            if (fonte.charAt(i) == '\n')
            {
                linha++;
                coluna = 1;
            }
            else
                coluna++;
        }
        out.println("Erro " + tipo + " (linha " + linha + ", coluna " + coluna + "): " + e.getMessage());
    }
}
