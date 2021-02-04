import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FSA {
    private Alphabet alphabet;
    private HashSet<State> states;
    private State start;
    private HashSet<State> finalStates;
    private HashMap<State, Transition> moves;
    protected int idCounter;

    public FSA() {
        alphabet = new Alphabet();
        states = new HashSet<>();
        start = new State(idCounter++);
        finalStates = new HashSet<>();
        moves = new HashMap<>();
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
        Transition transition = moves.get(from);
        if (transition == null) {
            transition = new Transition();
            List<State> others = new ArrayList<>();
            others.add(to);
            transition.put(consumed, others);
            moves.put(from, transition);
        } else {
            List<State> others = transition.get(consumed);
            others.add(to);
        }
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

    public HashMap<State, Transition> getMoves() {
        return moves;
    }
}


