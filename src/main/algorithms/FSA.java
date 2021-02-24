import java.util.HashSet;
import java.util.Objects;

public class FSA {
    private Alphabet alphabet;
    private HashSet<State> states;
    private State start;
    private HashSet<State> finalStates;
    private HashSet<Move> moves;
    private JSONElement e;

    public FSA() {
        alphabet = new Alphabet();
        states = new HashSet<>();
        start = new State();
        finalStates = new HashSet<>();
        moves = new HashSet<>();
        states.add(start);
    }

    public FSA(Alphabet alphabet, HashSet<State> states, State start,
               HashSet<State> finalStates, HashSet<Move> moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    public FSA clone() {
        Alphabet alphabet = new Alphabet();
        HashSet<State> states = new HashSet<>();
        State start = this.getStart();
        HashSet<State> finalStates = new HashSet<>();
        HashSet<Move> moves = new HashSet<>();

        alphabet.addAll(this.getAlphabet());
        states.addAll(this.getStates());
        finalStates.addAll(this.getFinalStates());
        moves.addAll(this.getMoves());

        return new FSA(alphabet, states, start, finalStates, moves);
    }

    public void addSymbol(Character newSymbol) {
        alphabet.addSymbol(newSymbol);
    }

    public void addState(State state) {
        states.add(state);
    }

    public void addFinalState(State state) {
        finalStates.add(state);
    }

    public void removeFinalStates() {
        finalStates.clear();
    }

    public void addMove(State from, Character consumed, State to) {
        moves.add(new Move(from, consumed, to));
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public HashSet<State> getStates() {
        return states;
    }

    public State getStart() {
        return start;
    }

    public void setStart(State state) {
        start = state;
    }

    public HashSet<State> getFinalStates() {
        return finalStates;
    }

    public HashSet<Move> getMoves() {
        return moves;
    }

    public void toJSON() {
        JSONElement e = new JSONElement();
        alphabet.toJSON(e);

        JSONElement e1 = new JSONElement();
        e.addChild("States", e1);
        for (State state : states) {
            state.toJSON(e1);
        }

        JSONElement e2 = new JSONElement();
        e.addChild("Start", e2);
        start.toJSON(e2);

        JSONElement e3 = new JSONElement();
        e.addChild("FinalStates", e3);
        for (State state : finalStates) {
            state.toJSON(e3);
        }

        JSONElement e4 = new JSONElement();
        e.addChild("Moves", e4);
        for (Move move : moves) {
            move.toJSON(e4);
        }

        this.e = e;
    }

    @Override
    public String toString() {
        toJSON();
        return e.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FSA fsa = (FSA) o;
        boolean isAlphabetEqual = Objects.equals(alphabet, fsa.alphabet);
        boolean areStatesEqual = Objects.equals(states, fsa.states);
        boolean isStartEqual = Objects.equals(start, fsa.start);
        boolean areFinalStatesEqual = Objects.equals(finalStates, fsa.finalStates);
        boolean areMovesEqual = Objects.equals(moves, fsa.moves);
        boolean isJSONEqual = Objects.equals(e, fsa.e);
        return isAlphabetEqual && areStatesEqual && isStartEqual && areFinalStatesEqual && areMovesEqual && isJSONEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alphabet, states, start, finalStates, moves, e);
    }
}


