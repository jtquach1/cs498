import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FSA {
    private Alphabet alphabet;
    private HashSet<State> states;
    private State start;
    private HashSet<State> finalStates;
    private HashSet<Move> moves;
    private final JSONElement e = new JSONElement();

    public FSA() {
        alphabet = new Alphabet();
        states = new HashSet<>();
        start = new State();
        finalStates = new HashSet<>();
        moves = new HashSet<>();
    }

    public HashSet<Character> getSymbols() {
        return alphabet.getSymbols();
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

    @Override
    public String toString() {
        toJSON();
        return e.toString();
    }

    public void toJSON() {
        alphabet.toJSON(e);

        JSONElement e1 = new JSONElement();
        for (State state : states) {
            state.toJSON(e1);
        }
        e.addChild("States", e1);

        JSONElement e2 = new JSONElement();
        start.toJSON(e2);
        e.addChild("Start", e2);

        JSONElement e3 = new JSONElement();
        for (State state : finalStates) {
            state.toJSON(e3);
        }
        e.addChild("FinalStates", e3);

        JSONElement e4 = new JSONElement();
        for (Move move : moves) {
            move.toJSON(e4);
        }
        e.addChild("Moves", e4);
    }
}


