import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class NFA extends FSA {
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};
    private NFA nfa;

    public void regularExpressionToNFA(String regularExpression) {
        int regexLength = regularExpression.length();
        Stack<Integer> operatorIndices = new Stack<>();

    }

    public void concatenate(NFA other) {
        connectOriginalFinalStatesToOtherStart(other);
        removeOriginalFinalStates();
        copyStates(other);
        copyAlphabet(other);
        copyMoves(other);
        copyFinalStates(other);
    }

    private void copyFinalStates(NFA other) {
        for (State state : other.getFinalStates())
            nfa.addFinalState(state);
    }

    private void removeOriginalFinalStates() {
        nfa.removeFinalStates();
    }

    private void copyAlphabet(NFA other) {
        for (Character consumed : other.getSymbols())
            nfa.addSymbol(consumed);
    }

    private void copyStates(NFA other) {
        for (State state : other.getStates())
            nfa.addState(state);
    }

    private void connectOriginalFinalStatesToOtherStart(NFA other) {
        HashSet<State> finalStates = nfa.getFinalStates();
        State otherStart = other.getStart();
        for (State finalState : finalStates) {
            nfa.addMove(finalState, EPSILON, otherStart);
        }
    }

    private void copyMoves(NFA other) {
        HashMap<State, Transition> otherMoves = other.getMoves();
        for (State from : otherMoves.keySet()) {
            Transition transition = otherMoves.get(from);
            for (Character consumed : transition.keySet())
                for (State to : transition.get(consumed))
                    nfa.addMove(from, consumed, to);
        }
    }

    public void kleeneStar() {
    }

    public void alternate(NFA... others) {
        NFA nfa = new NFA();
    }

}
