public class Lexico implements Constants
{
    private int position;
    private String input;

    public Lexico()
    {
        this("");
    }

    public Lexico(String input)
    {
        setInput(input);
    }

    public void setInput(String input)
    {
        this.input = input;
        setPosition(0);
    }

    public void setPosition(int pos)
    {
        position = pos;
    }

    public Token nextToken() throws LexicalError
    {
        if ( ! hasInput() )
            return null;

        int start = position;

        int state = 0;
        int endState = -1;
        int end = -1;

        while (hasInput())
        {
            state = nextState(nextChar(), state);

            if (state < 0)
                break;

            if (tokenForState(state) >= 0)
            {
                endState = state;
                end = position;
            }
        }

        if (endState < 0)
            throw new LexicalError(SCANNER_ERROR[0] + ": '" + input.charAt(start) + "'", start);

        position = end;

        int token = tokenForState(endState);

        if (token == 0)
            return nextToken();

        String lexeme = input.substring(start, end);
        token = lookupToken(token, lexeme);
        return new Token(token, lexeme, start);
    }

    // Automato finito deterministico das expressoes regulares de gramatica.gals
    private int nextState(char c, int state)
    {
        switch (state)
        {
            case 0:
                if (isLetter(c)) return 1;
                if (c == '0' || c == '1') return 2;
                switch (c)
                {
                    case '=': return 3;
                    case ';': return 4;
                    case '(': return 5;
                    case ')': return 6;
                    case '+': return 7;
                    case '-': return 8;
                    case '*': return 9;
                    case '/': return 10;
                    case '^': return 11;
                    case ' ': case '\t': case '\n': case '\r': return 12;
                    default: return -1;
                }
            case 1:
                return isLetter(c) || isDigit(c) ? 1 : -1;
            case 2:
                return c == '0' || c == '1' ? 2 : -1;
            case 10:
                return c == '/' ? 13 : -1;
            case 12:
                return c == ' ' || c == '\t' || c == '\n' || c == '\r' ? 12 : -1;
            case 13:
                return c != '\n' && c != '\r' ? 13 : -1;
            default:
                return -1;
        }
    }

    private static boolean isLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private int tokenForState(int state)
    {
        if (state < 0 || state >= TOKEN_STATE.length)
            return -1;

        return TOKEN_STATE[state];
    }

    public int lookupToken(int base, String key)
    {
        if (base != t_ID)
            return base;

        for (int i = 0; i < SPECIAL_CASES_KEYS.length; i++)
            if (SPECIAL_CASES_KEYS[i].equals(key))
                return SPECIAL_CASES_VALUES[i];

        return base;
    }

    private boolean hasInput()
    {
        return position < input.length();
    }

    private char nextChar()
    {
        if (hasInput())
            return input.charAt(position++);
        else
            return (char) -1;
    }
}
