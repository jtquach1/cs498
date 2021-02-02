import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FSA {
    protected Alphabet alphabet;
    protected HashSet<State> states;
    protected State start;
    protected HashSet<State> finalStates;
    protected HashMap<State, Transition> moves;

    public FSA() {
        this.alphabet = new Alphabet();
        this.states = new HashSet<>();
        this.start = new State();
        this.finalStates = new HashSet<>();
        this.moves = new HashMap<>();
    }

    public HashSet<Character> getSymbols() {
        return this.alphabet.getSymbols();
    }

    public void addSymbol(Character newSymbol) {
        this.alphabet.addSymbol(newSymbol);
    }

    public void addState(State state) {
        this.states.add(state);
    }

    public void setStart(State state) {
        this.start = state;
    }

    public void addFinalState(State state) {
        this.finalStates.add(state);
    }

    public void addMove(State from, Character consumed, State to) {
        Transition transition = this.moves.get(from);
        if (transition == null) {
            transition = new Transition();
            List<State> others = new ArrayList<>();
            others.add(to);
            transition.put(consumed, others);
            this.moves.put(from,  transition);
        }
        else {
            List<State> others = transition.get(consumed);
            others.add(to);
        }
    }
}


