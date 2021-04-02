import org.javatuples.Triplet;
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
    private final Set<Triplet> moves;
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
        Set<State> finalStates, Set<Triplet> moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    public static String convertJsonToGraphviz(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject fsa = jsonReader.readObject();

        int startId = convertJSONToStartId(fsa);
        Set<Integer> finalStateIds = convertJSONToFinalStateIds(fsa);
        Set<Triplet> moves = convertJSONToMoves(fsa);

        StringBuilder sb = new StringBuilder();
        sb.append("digraph finite_state_machine {\n");
        sb.append("\trankdir=LR;\n");
        sb.append("\tsize=\"8,5\";\n");
        sb.append("\n");
        sb.append("\tnode [shape = doublecircle];\n");
        sb.append("\t");
        for (Integer id : finalStateIds) {
            sb.append(id + " ");
        }
        sb.append(";\n");
        sb.append("\n");
        sb.append("\tnode [shape = circle];\n");
        for (Triplet move : moves) {
            int fromId = ((State) move.getValue(0)).getId();
            String consumed = ((Character) move.getValue(1)).toString();
            int toId = ((State) move.getValue(2)).getId();
            sb.append("\t" + fromId + " -> " + toId + " [label = \"" + consumed + "\"];\n");
        }
        sb.append("\n");
        sb.append("\tnode [shape = none, label =\"\"];\n");
        sb.append("\tENTRY -> " + startId + ";\n");
        sb.append("}\n");

        return sb.toString();
    }

    private static int convertJSONToStartId(JsonObject fsa) {
        return fsa.getJsonNumber("start").intValue();
    }

    @NotNull
    private static Set<Triplet> convertJSONToMoves(JsonObject fsa) {
        Set<Triplet> moves = new TreeSet<>();
        JsonArray jsonMoves = fsa.getJsonArray("moves");
        for (int i = 0; i < jsonMoves.toArray().length; i++) {
            JsonObject jsonMove = jsonMoves.getJsonObject(i);
            State from = new State(jsonMove.getInt("from"));
            Character consumed = jsonMove.getString("consumed").charAt(0);
            State to = new State(jsonMove.getInt("to"));
            moves.add(new Triplet(from, consumed, to));
        }
        return moves;
    }

    @NotNull
    private static Set<Integer> convertJSONToFinalStateIds(JsonObject fsa) {
        Set<Integer> ids = new TreeSet<>();
        JsonArray jsonFinalStates = fsa.getJsonArray("finalStates");
        for (int i = 0; i < jsonFinalStates.toArray().length; i++) {
            ids.add(jsonFinalStates.getInt(i));
        }
        return ids;
    }

    FSA deepClone() {
        Alphabet alphabet = new Alphabet(this.getAlphabet());
        Set<State> states = new TreeSet<>(this.getStates());
        State start = this.getStart();
        Set<State> finalStates = new TreeSet<>(this.getFinalStates());
        Set<Triplet> moves = new TreeSet<>(this.getMoves());

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
        moves.add(new Triplet(from, consumed, to));
    }

    void addMove(Triplet move) {
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

    Set<Triplet> getMoves() {
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
        for (Triplet m : this.moves) {
            JsonObject move = Json.createObjectBuilder()
                    .add("from", ((State) m.getValue(0)).getId())
                    .add("consumed", ((Character) m.getValue(1)).toString())
                    .add("to", ((State) m.getValue(2)).getId())
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


