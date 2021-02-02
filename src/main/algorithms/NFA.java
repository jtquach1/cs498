public class NFA extends FSA {
    private NFA nfa;
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};

    public static NFA regularExpressionToNFA(String regularExpression) {
        NFA nfa = new NFA();
        return nfa;
    }

    public static NFA concatenate(NFA self, NFA other, Character literal) {
        NFA nfa = new NFA();
        return nfa;
    }

    public static NFA kleeneStar(NFA self) {
        NFA nfa = new NFA();
        return nfa;
    }

    public static NFA alternate(NFA self, NFA other) {
        NFA nfa = new NFA();
        return nfa;
    }

}
