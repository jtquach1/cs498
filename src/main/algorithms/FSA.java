import org.jetbrains.annotations.NotNull;

import javax.json.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

class FSA {
    private final Alphabet alphabet;
    private final Set<State> states;
    private final Set<State> finalStates;
    private final Set<Move> moves;
    private State start;

    FSA() {
        alphabet = new Alphabet();
        states = new TreeSet<>();
        start = new State();
        finalStates = new TreeSet<>();
        moves = new TreeSet<>();
        states.add(start);
    }

    FSA(Alphabet alphabet, Set<State> states, State start,
        Set<State> finalStates, Set<Move> moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    static FSA convertJsonToFsa(String json) {
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
        Set<Move> moves = new TreeSet<>();
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
        Set<State> finalStates = new TreeSet<>();
        JsonArray jsonFinalStates = fsa.getJsonArray("finalStates");
        for (int i = 0; i < jsonFinalStates.toArray().length; i++) {
            finalStates.add(new State(jsonFinalStates.getInt(i)));
        }
        return finalStates;
    }

    @NotNull
    private static Set<State> convertJSONToStates(JsonObject fsa) {
        Set<State> states = new TreeSet<>();
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

    FSA deepClone() {
        Alphabet alphabet = new Alphabet(this.getAlphabet());
        Set<State> states = new TreeSet<>(this.getStates());
        State start = this.getStart();
        Set<State> finalStates = new TreeSet<>(this.getFinalStates());
        Set<Move> moves = new TreeSet<>(this.getMoves());

        return new FSA(alphabet, states, start, finalStates, moves);
    }

    void addSymbol(Character newSymbol) {
        alphabet.addSymbol(newSymbol);
    }

    void addState(State state) {
        states.add(state);
    }

    void addFinalState(State state) {
        finalStates.add(state);
    }

    void removeFinalStates() {
        finalStates.clear();
    }

    void addMove(State from, Character consumed, State to) {
        moves.add(new Move(from, consumed, to));
    }

    void addMove(Move move) {
        moves.add(move);
    }

    Alphabet getAlphabet() {
        return alphabet;
    }

    Set<State> getStates() {
        return states;
    }

    State getStart() {
        return start;
    }

    void setStart(State state) {
        start = state;
    }

    Set<State> getFinalStates() {
        return finalStates;
    }

    Set<Move> getMoves() {
        return moves;
    }

    @Override
    public String toString() {
        JsonArrayBuilder alphabet = createJsonAlphabet();
        JsonArrayBuilder states = createJsonStates();
        JsonArrayBuilder finalStates = createJsonFinalStates();
        JsonArrayBuilder moves = createJsonMoves();
        JsonObject fsa = createJsonFsa(alphabet, states, finalStates, moves);

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(fsa);
        jsonWriter.close();
        return stringWriter.getBuffer().toString();
    }

    private JsonObject createJsonFsa(JsonArrayBuilder alphabet,
                                     JsonArrayBuilder states,
                                     JsonArrayBuilder finalStates,
                                     JsonArrayBuilder moves) {
        JsonObject fsa = Json.createObjectBuilder()
                .add("alphabet", alphabet)
                .add("states", states)
                .add("start", this.start.getId())
                .add("finalStates", finalStates)
                .add("moves", moves)
                .build();
        return fsa;
    }

    private JsonArrayBuilder createJsonMoves() {
        JsonArrayBuilder moves = Json.createArrayBuilder();
        for (Move m : this.moves) {
            JsonObject move = Json.createObjectBuilder()
                    .add("from", m.getFrom().getId())
                    .add("consumed", m.getConsumed().toString())
                    .add("to", m.getTo().getId())
                    .build();
            moves.add(move);
        }
        return moves;
    }

    private JsonArrayBuilder createJsonFinalStates() {
        JsonArrayBuilder finalStates = Json.createArrayBuilder();
        for (State s : this.finalStates) {
            finalStates.add(s.getId());
        }
        return finalStates;
    }

    private JsonArrayBuilder createJsonStates() {
        JsonArrayBuilder states = Json.createArrayBuilder();
        for (State s : this.states) {
            states.add(s.getId());
        }
        return states;
    }

    private JsonArrayBuilder createJsonAlphabet() {
        JsonArrayBuilder alphabet = Json.createArrayBuilder();
        for (Character c : this.alphabet) {
            alphabet.add(c.toString());
        }
        return alphabet;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FSA fsa = (FSA) o;
        return Objects.equals(alphabet, fsa.alphabet)
                && Objects.equals(states, fsa.states)
                && Objects.equals(finalStates, fsa.finalStates)
                && Objects.equals(moves, fsa.moves)
                && Objects.equals(start, fsa.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alphabet, states, finalStates, moves, start);
    }
}


