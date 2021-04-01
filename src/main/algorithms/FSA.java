import org.jetbrains.annotations.NotNull;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class FSA {
    private final Alphabet alphabet;
    private final Set<State> states;
    private final Set<State> finalStates;
    private final Set<Move> moves;
    private State start;

    public FSA() {
        alphabet = new Alphabet();
        states = new HashSet<>();
        start = new State();
        finalStates = new HashSet<>();
        moves = new HashSet<>();
        states.add(start);
    }

    public FSA(Alphabet alphabet, Set<State> states, State start,
               Set<State> finalStates, Set<Move> moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    public static FSA convert(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject fsa = jsonReader.readObject();

        Alphabet alphabet = convertJSONToAlphabet(fsa);
        Set<State> states = convertJSONToStates(fsa);
        State start = convertJSONToStart(fsa);
        Set<State> finalStates = convertJSONToFinalStates(fsa);
        Set<Move> moves = convertJSONToMoves(fsa);

        return new FSA(alphabet, states, start, finalStates, moves);
    }

    @NotNull
    private static State convertJSONToStart(JsonObject fsa) {
        int id = fsa.getJsonNumber("start").intValue();
        return new State(id);
    }

    @NotNull
    private static Set<Move> convertJSONToMoves(JsonObject fsa) {
        Set<Move> moves = new HashSet<>();
        JsonArray jsonMoves = fsa.getJsonArray("moves");
        for (int i = 0; i < jsonMoves.toArray().length; i++) {
            JsonObject jsonMove = jsonMoves.getJsonObject(i);
            State from = new State(jsonMove.getInt("from"));
            Character consumed = jsonMove.getString("consumed").charAt(0);
            State to = new State(jsonMove.getInt("to"));
            moves.add(new Move(from, consumed, to));
        }
        return moves;
    }

    @NotNull
    private static Set<State> convertJSONToFinalStates(JsonObject fsa) {
        Set<State> finalStates = new HashSet<>();
        JsonArray jsonFinalStates = fsa.getJsonArray("finalStates");
        for (int i = 0; i < jsonFinalStates.toArray().length; i++) {
            finalStates.add(new State(jsonFinalStates.getInt(i)));
        }
        return finalStates;
    }

    @NotNull
    private static Set<State> convertJSONToStates(JsonObject fsa) {
        Set<State> states = new HashSet<>();
        JsonArray jsonStates = fsa.getJsonArray("states");
        for (int i = 0; i < jsonStates.toArray().length; i++) {
            states.add(new State(jsonStates.getInt(i)));
        }
        return states;
    }

    @NotNull
    private static Alphabet convertJSONToAlphabet(JsonObject fsa) {
        Alphabet alphabet = new Alphabet();
        JsonArray jsonAlphabet = fsa.getJsonArray("alphabet");
        for (int i = 0; i < jsonAlphabet.toArray().length; i++) {
            alphabet.addSymbol(jsonAlphabet.getString(i).charAt(0));
        }
        return alphabet;
    }

    public FSA clone() {
        Alphabet alphabet = new Alphabet(this.getAlphabet());
        Set<State> states = new HashSet<>(this.getStates());
        State start = this.getStart();
        Set<State> finalStates = new HashSet<>(this.getFinalStates());
        Set<Move> moves = new HashSet<>(this.getMoves());

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

    public Set<State> getStates() {
        return states;
    }

    public State getStart() {
        return start;
    }

    public void setStart(State state) {
        start = state;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public Set<Move> getMoves() {
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


