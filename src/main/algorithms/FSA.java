import java.util.HashMap;
import java.util.HashSet;

public class FSA {
    protected Alphabet alphabet;
    protected HashSet<State> states;
    protected State start;
    protected HashSet<State> finalStates;
    protected HashMap<State, Character> moves;

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

}


