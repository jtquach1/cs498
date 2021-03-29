import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import javax.json.*;

public class FSA {
    private final Alphabet alphabet;
    private final HashSet<State> states;
    private State start;
    private final HashSet<State> finalStates;
    private final HashSet<Move> moves;

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

    public static FSA convert(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        return new FSA();
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

    @Override
    public String toString() {
        JsonArrayBuilder alphabet = Json.createArrayBuilder();
        for (Character c : this.alphabet) {
            alphabet.add(c.toString());
        }

        JsonArrayBuilder states = Json.createArrayBuilder();
        for (State s : this.states) {
            states.add(s.getId());
        }

        JsonArrayBuilder finalStates = Json.createArrayBuilder();
        for (State s : this.finalStates) {
            finalStates.add(s.getId());
        }

        JsonArrayBuilder moves = Json.createArrayBuilder();
        for (Move m : this.moves) {
            JsonObject move = Json.createObjectBuilder()
                    .add("from", m.getFrom().getId())
                    .add("consumed", m.getConsumed().toString())
                    .add("to", m.getTo().getId())
                    .build();
            moves.add(move);
        }

        JsonObject fsa = Json.createObjectBuilder()
                .add("alphabet", alphabet)
                .add("states", states)
                .add("start", this.start.getId())
                .add("finalStates", finalStates)
                .add("moves", moves)
                .build();

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(fsa);
        jsonWriter.close();
        return stringWriter.getBuffer().toString();
    }
}


