import java.util.HashSet;
import java.util.Stack;

public class NFA extends FSA {
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};

    public NFA() {
        super();
    }

    public NFA(Alphabet alphabet, HashSet<State> states, State start,
               HashSet<State> finalStates, HashSet<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
    }


    public NFA(String regex) {
        int regexLength = regex.length();
        Stack<Integer> operatorIndices = new Stack<>();
    }

    public static NFA convert(String json) {
        FSA fsa = FSA.convert(json);
        return new NFA(
                fsa.getAlphabet(),
                fsa.getStates(),
                fsa.getStart(),
                fsa.getFinalStates(),
                fsa.getMoves()
        );
    }

    public static void main(String[] args) {
        NFA first;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('a');
            HashSet<State> states = new HashSet<>();
            State start = new State();
            HashSet<State> finalStates = new HashSet<>();
            State last = new State();
            HashSet<Move> moves = new HashSet<>();
            Move move = new Move(start, 'a', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            first = new NFA(alphabet, states, start, finalStates, moves);
        }

        NFA second;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('b');
            HashSet<State> states = new HashSet<>();
            State start = new State();
            HashSet<State> finalStates = new HashSet<>();
            State last = new State();
            HashSet<Move> moves = new HashSet<>();
            Move move = new Move(start, 'b', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            second = new NFA(alphabet, states, start, finalStates, moves);
        }

        NFA concatTest = first.concatenate(second);

        String firstJson = first.toString();
        String secondJson = second.toString();
        String concatJson = concatTest.toString();

        NFA converted = NFA.convert(firstJson);
    }

    public NFA clone() {
        Alphabet alphabet = new Alphabet();
        HashSet<State> states = new HashSet<>();
        State start = this.getStart();
        HashSet<State> finalStates = new HashSet<>();
        HashSet<Move> moves = new HashSet<>();

        alphabet.addAll(this.getAlphabet());
        states.addAll(this.getStates());
        finalStates.addAll(this.getFinalStates());
        moves.addAll(this.getMoves());

        return new NFA(alphabet, states, start, finalStates, moves);
    }

    public NFA concatenate(NFA other) {
        NFA result = this.clone();
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
