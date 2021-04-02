import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class FSA {
    static final char EPSILON = '\u025B';

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

    public String toDOT() {
        int startId = getStart().getId();
        Set<Integer> finalStateIds = getFinalStates()
                .stream()
                .map(State::getId)
                .collect(Collectors.toSet());
        Set<Move> moves = getMoves();

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
        for (Move move : moves) {
            int fromId = move.getFrom().getId();
            String consumed = move.getConsumed().toString();
            int toId = move.getTo().getId();
            sb.append("\t" + fromId + " -> " + toId + " [label = \"" + consumed + "\"];\n");
        }
        sb.append("\n");
        sb.append("\tnode [shape = none, label =\"\"];\n");
        sb.append("\tENTRY -> " + startId + ";\n");
        sb.append("}\n");

        return sb.toString();
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
        return toJSON();
    }

    String toJSON() {
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


