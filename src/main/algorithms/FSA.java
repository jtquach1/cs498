package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

class FSA {
    static final char EPSILON = '\u025B';
    static final char PHI = '\u03C6';

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

    FSA(
            Alphabet alphabet,
            Set<State> states,
            State start,
            Set<State> finalStates,
            Set<Move> moves
    ) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    // can have a flag to determine whether you want to show garbage state for DFA
    // or actually don't suppress the garbage state
    public String toDOT() {
        int startId = start.getId();
        Set<Integer> finalStateIds = finalStates
                .stream()
                .map(State::getId)
                .collect(Collectors.toSet());
        Map<Move, Set<Character>> moveToLabel = getMoveToLabel();

        StringBuilder sb = new StringBuilder();
        printFinalStates(finalStateIds, sb);
        printMoves(moveToLabel, sb);
        printStartState(startId, sb);

        return sb.toString();
    }

    private void printStartState(int startId, StringBuilder sb) {
        sb.append("\n" +
                "\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> " + startId + ";\n" +
                "}\n"
        );
    }

    private void printFinalStates(Set<Integer> finalStateIds, StringBuilder sb) {
        sb.append("digraph finite_state_machine {\n" +
                "\trankdir=LR;\n" +
                "\tsize=\"8,5\";\n" +
                "\n" +
                "\tnode [shape = doublecircle];\n" +
                "\t"
        );
        for (Integer id : finalStateIds) {
            sb.append(id + " ");
        }
    }

    private void printMoves(
            Map<Move, Set<Character>> moveToLabel,
            StringBuilder sb
    ) {
        sb.append(";\n" +
                "\n" +
                "\tnode [shape = circle];" +
                "\n"
        );

        for (Move move : moveToLabel.keySet()) {
            String originalLabel = moveToLabel.get(move).toString();
            String label = originalLabel.substring(1, originalLabel.length() - 1);

            int fromId = move.getFrom().getId();
            int toId = move.getTo().getId();
            String from = getStateLabel(fromId);
            String to = getStateLabel(toId);

            sb.append("\t" + from + " -> " + to + " [label = \"" + label + "\"];\n");
        }
    }

    private String getStateLabel(int stateId) {
        boolean isDFAWithPhiState = this instanceof DFA && ((DFA) this).getPhi() != null;

        if (isDFAWithPhiState) {
            boolean hasPhi = stateId == ((DFA) this).getPhi().getId();

            if (hasPhi) {
                return Character.toString(PHI);
            }
        }

        return Integer.toString(stateId);
    }

    @NotNull
    private Map<Move, Set<Character>> getMoveToLabel() {
        Map<Move, Set<Character>> moveToLabel = new TreeMap<>();

        // Display multiple characters for one arrow
        for (Move move : moves) {
            Move key = new Move(move.getFrom(), '\u0000', move.getTo());
            Set<Character> label;

            // Does the arrow already exist?
            if (moveToLabel.containsKey(key)) {
                label = moveToLabel.get(key);
            } else {
                label = new TreeSet<>();
            }
            label.add(move.getConsumed());
            moveToLabel.put(key, label);
        }
        return moveToLabel;
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
        return toDOT();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        FSA fsa = (FSA) other;
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

    void addSymbol(Character newSymbol) {
        add(newSymbol);
    }
}

class State implements Comparable<State> {
    private static int idCounter;
    private final int id;

    State() {
        id = idCounter++;
    }

    State(int id) {
        this.id = id;
    }

    static void setIdCounter(int idCounter) {
        State.idCounter = idCounter;
    }

    int getId() {
        return id;
    }

    State getTo(Set<Move> moves, Character consumed) {
        Move move = moves
                .stream()
                .filter((m) -> m.hasFrom(this) && m.hasConsumed(consumed))
                .findFirst()
                .orElse(null);
        State to = move != null ? move.getTo() : null;
        return to;
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
        return from;
    }

    Character getConsumed() {
        return consumed;
    }

    State getTo() {
        return to;
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

