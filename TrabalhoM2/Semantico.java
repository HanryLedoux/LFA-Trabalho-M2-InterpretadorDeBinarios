import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Acoes semanticas (ver gramatica.gals):
 *  #1  guarda o nome da variavel que recebera a atribuicao
 *  #2  atribui o topo da pilha a variavel guardada em #1
 *  #3  exibe o topo da pilha (Show)
 *  #4  soma            #5  subtracao
 *  #6  multiplicacao   #7  divisao inteira
 *  #8  exponenciacao   #9  empilha numero binario
 *  #10 empilha valor de variavel
 *  #11 log na base 2 (parte inteira)
 */
public class Semantico implements Constants
{
    private static final int MAX_EXPOENTE = 100000;

    private final Map<String, BigInteger> variaveis = new LinkedHashMap<>();
    private final Deque<BigInteger> pilha = new ArrayDeque<>();
    private final PrintStream saida;
    private String variavelDestino;
    private boolean ecoAtribuicoes;

    public Semantico()
    {
        this(System.out);
    }

    public Semantico(PrintStream saida)
    {
        this.saida = saida;
    }

    public Map<String, BigInteger> getVariaveis()
    {
        return variaveis;
    }

    // No modo interativo, cada atribuicao exibe o valor que a variavel recebeu
    public void setEcoAtribuicoes(boolean ecoAtribuicoes)
    {
        this.ecoAtribuicoes = ecoAtribuicoes;
    }

    public void executeAction(int action, Token token) throws SemanticError
    {
        BigInteger a, b;

        switch (action)
        {
            case 1:
                variavelDestino = token.getLexeme();
                break;

            case 2:
                variaveis.put(variavelDestino, pilha.pop());
                if (ecoAtribuicoes)
                    saida.println(variavelDestino + " = " + variaveis.get(variavelDestino).toString(2));
                break;

            case 3:
                saida.println(pilha.pop().toString(2));
                break;

            case 4:
                b = pilha.pop();
                a = pilha.pop();
                pilha.push(a.add(b));
                break;

            case 5:
                b = pilha.pop();
                a = pilha.pop();
                if (a.compareTo(b) < 0)
                    throw new SemanticError("Subtracao com resultado negativo ("+ a.toString(2) + " - " + b.toString(2) + ")", token.getPosition());
                pilha.push(a.subtract(b));
                break;

            case 6:
                b = pilha.pop();
                a = pilha.pop();
                pilha.push(a.multiply(b));
                break;

            case 7:
                b = pilha.pop();
                a = pilha.pop();
                if (b.signum() == 0)
                    throw new SemanticError("Divisao por zero", token.getPosition());
                pilha.push(a.divide(b));
                break;

            case 8:
                b = pilha.pop();
                a = pilha.pop();
                if (b.compareTo(BigInteger.valueOf(MAX_EXPOENTE)) > 0)
                    throw new SemanticError("Expoente muito grande (maximo " + Integer.toBinaryString(MAX_EXPOENTE) + ")", token.getPosition());
                pilha.push(a.pow(b.intValue()));
                break;

            case 9:
                pilha.push(new BigInteger(token.getLexeme(), 2));
                break;

            case 10:
                BigInteger valor = variaveis.get(token.getLexeme());
                if (valor == null)
                    throw new SemanticError("Variavel \"" + token.getLexeme() + "\" nao inicializada", token.getPosition());
                pilha.push(valor);
                break;

            case 11:
                a = pilha.pop();
                if (a.signum() == 0)
                    throw new SemanticError("Logaritmo de zero nao e definido", token.getPosition());
                pilha.push(BigInteger.valueOf(a.bitLength() - 1));
                break;

            default:
                throw new SemanticError("Acao semantica desconhecida: #" + action, token.getPosition());
        }
    }
}
