public class NFA extends FSA {
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};

    public static NFA regularExpressionToNFA(String regularExpression) {
        NFA nfa = new NFA();
        return nfa;
    }
}
