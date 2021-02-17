import java.util.HashSet;
import java.util.Stack;

public class NFA extends FSA {
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};

    public NFA concatenate(NFA other) {
        NFA result = this;
        result.connectOriginalFinalStatesToOtherStart(other);
        result.removeOriginalFinalStates();
        result.copyStates(other);
        result.copyAlphabet(other);
        result.copyMoves(other);
        result.copyFinalStates(other);
        return result;
    }

    public NFA kleeneStar() {
        NFA result = this;
        result.connectOriginalFinalStatesToOriginalStart();
        result.addNewFinal();
        result.addNewStart();
        result.connectNewStartToNewFinal();
        return result;
    }

    public NFA alternate(NFA other) {
        NFA result = this;
        result.addNewStart();
        result.addNewFinal();
        NFA helperFinal = new NFA();
        return result;
    }

    public void regularExpressionToNFA(String regularExpression) {
        int regexLength = regularExpression.length();
        Stack<Integer> operatorIndices = new Stack<>();

    }

    private void copyFinalStates(NFA other) {
        for (State state : other.getFinalStates()) {
            this.addFinalState(state);
        }
    }

    private void removeOriginalFinalStates() {
        this.removeFinalStates();
    }

    private void copyAlphabet(NFA other) {
        for (Character consumed : other.getAlphabet()) {
            this.addSymbol(consumed);
        }
    }

    private void copyStates(NFA other) {
        for (State state : other.getStates()) {
            this.addState(state);
        }
    }

    private void connectOriginalFinalStatesToOtherStart(NFA other) {
        HashSet<State> finalStates = this.getFinalStates();
        State otherStart = other.getStart();
        for (State finalState : finalStates) {
            this.addMove(finalState, EPSILON, otherStart);
        }
    }

    private void copyMoves(NFA other) {
        HashSet<Move> otherMoves = other.getMoves();
        for (Move move : otherMoves) {
            this.addMove(move);
        }
    }

    private void connectNewStartToNewFinal() {
        // There is only one new final state, but this is more convenient to write.
        for (State newFinalState : getFinalStates()) {
            addMove(getStart(), EPSILON, newFinalState);
        }
    }

    private void addNewFinal() {
        HashSet<State> finalStates = getFinalStates();
        State newFinal = new State();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, newFinal);
        }
        removeFinalStates();
        addFinalState(newFinal);
    }

    private void connectOriginalFinalStatesToOriginalStart() {
        State oldStart = getStart();
        HashSet<State> finalStates = getFinalStates();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, oldStart);
        }
    }

    private void addNewStart() {
        State oldStart = getStart();
        State newStart = new State();
        addNewStart(oldStart, newStart);
    }

    private void addNewStart(State oldStart, State newStart) {
        addState(newStart);
        addMove(newStart, EPSILON, oldStart);
        setStart(newStart);

    }

}
