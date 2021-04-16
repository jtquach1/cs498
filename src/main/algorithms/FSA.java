package algorithms;

import org.jetbrains.annotations.NotNull;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.StringWriter;
import java.util.Comparator;
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
        JsonArrayBuilder alphabet = createJSONAlphabet();
        JsonArrayBuilder states = createJSONStates();
        JsonArrayBuilder finalStates = createJSONFinalStates();
        JsonArrayBuilder moves = createJSONMoves();
        JsonObject fsa = createJSONFSA(alphabet, states, finalStates, moves);

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = Json.createWriter(stringWriter);
        jsonWriter.writeObject(fsa);
        jsonWriter.close();
        return stringWriter.getBuffer().toString();
    }

    private JsonObject createJSONFSA(JsonArrayBuilder alphabet,
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

    private JsonArrayBuilder createJSONMoves() {
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

    private JsonArrayBuilder createJSONFinalStates() {
        JsonArrayBuilder finalStates = Json.createArrayBuilder();
        for (State s : this.finalStates) {
            finalStates.add(s.getId());
        }
        return finalStates;
    }

    private JsonArrayBuilder createJSONStates() {
        JsonArrayBuilder states = Json.createArrayBuilder();
        for (State s : this.states) {
            states.add(s.getId());
        }
        return states;
    }

    private JsonArrayBuilder createJSONAlphabet() {
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

class Alphabet extends TreeSet<Character> {
    Alphabet() {
        super();
    }

    Alphabet(Alphabet alphabet) {
        addAll(alphabet);
    }

    void addSymbol(Character newSymbol) {
        this.add(newSymbol);
    }
}

class State implements Comparable<State> {
    private static int idCounter;
    private final int id;

    State() {
        this.id = idCounter++;
    }

    State(int id) {
        this.id = id;
    }

    static void setIdCounter(int idCounter) {
        State.idCounter = idCounter;
    }

    int getId() {
        return this.id;
    }

    @Override
    public int compareTo(@NotNull State other) {
        return Comparator.comparing(State::getId)
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "";
    }
}

class Move implements Comparable<Move> {
    private final State from;
    private final Character consumed;
    private final State to;

    Move(State from, Character consumed, State to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    State getFrom() {
        return this.from;
    }

    Character getConsumed() {
        return this.consumed;
    }

    State getTo() {
        return this.to;
    }

    boolean hasConsumed(Character consumed) {
        return this.consumed.equals(consumed);
    }

    boolean hasFrom(State from) {
        return this.from.equals(from);
    }

    @Override
    public int compareTo(@NotNull Move other) {
        return Comparator.comparing(Move::getFrom)
                .thenComparing(Move::getConsumed)
                .thenComparing(Move::getTo)
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(from, move.from)
                && Objects.equals(consumed, move.consumed)
                && Objects.equals(to, move.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, consumed, to);
    }
}

