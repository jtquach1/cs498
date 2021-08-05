package algorithms;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static algorithms.Utility.createDOTFiles;
import static algorithms.Utility.getNonFlag;

class FSA implements DOT {
    static final char EPSILON = '\u025B';
    static final char PHI = '\u03C6';

    protected final Alphabet alphabet;
    protected final States states;
    protected final States finalStates;
    protected final Moves moves;
    protected State start;

    FSA(Alphabet alphabet, States states, State start, States finalStates, Moves moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    public static void main(String[] args) throws IOException {
        TreeMap<String, String> arguments = getArguments(args);

        try {
            String inputRegex = arguments.get("inputRegex");
            String outputPrefix = arguments.get("outputPrefix");

            if (inputRegex == null) {
                inputRegex = "";
                System.out.println("Input regular expression not specified, using empty string by" +
                        " default");
            }

            if (outputPrefix == null) {
                System.out.println("ERROR: Output filename prefix not specified");
                return;
            }

            TreeMap<String, DOT> structures = getStructures(inputRegex);
            createDOTFiles(outputPrefix, structures);

        } catch (Exception e) {
            System.out.println("ERROR: Invalid regular expression");
            throw e;
        }
    }

    @NotNull
    private static TreeMap<String, DOT> getStructures(String inputRegex) {
        NFA nfa = NFA.regexToNFA(inputRegex);
        DFA dfa = DFA.NFAtoDFA(nfa);
        DFA minDfa = DFA.DFAtoMinDFA(dfa);

        TreeMap<String, DOT> structures = new TreeMap<>();
        structures.put("nfa", nfa);
        structures.put("dfa", dfa);
        structures.put("minDfa", minDfa);
        return structures;
    }

    private static TreeMap<String, String> getArguments(String[] args) {
        TreeMap<String, String> arguments = new TreeMap<>();

        for (int i = 0; i < args.length - 1; i++) {
            String nonFlag = getNonFlag(args, i + 1);

            if (args[i].equals("-i")) {
                arguments.put("inputRegex", nonFlag);
            }

            if (args[i].equals("-o")) {
                arguments.put("outputPrefix", nonFlag);
            }
        }
        return arguments;
    }

    @Override
    public String toDOT() {
        return String.valueOf(printFinalStates()) +
                printStates() +
                printMoves() +
                printStartState();
    }

    private StringBuilder printFinalStates() {
        String finalStates = this.finalStates
                .stream()
                .map(state -> Integer.toString(state.getId()))
                .collect(Collectors.joining(" "));

        StringBuilder sb = new StringBuilder();
        sb.append("\tnode [shape = doublecircle];\n\t");
        sb.append(finalStates);
        sb.append(";\n\n");
        return sb;
    }

    private StringBuilder printStates() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tnode [shape = circle];\n");

        for (State state : states) {
            String alternativeLabel = state.getAlternativeLabel();
            boolean generatedFromClosureOrPartition = alternativeLabel != null;
            if (generatedFromClosureOrPartition) {
                String idLabel = this.getStateLabel(state);
                sb.append("\t" + idLabel + " [label=\"" + idLabel + "\\n" + alternativeLabel +
                        "\"];\n");
            }
        }

        sb.append("\n");
        return sb;
    }

    private String getStateLabel(State state) {
        boolean thisIsDFAWithPhiState = this instanceof DFA && ((DFA) this).getPhi() != null;
        int stateId = state.getId();

        if (thisIsDFAWithPhiState) {
            boolean givenStateIsPhi = stateId == ((DFA) this).getPhi().getId();

            if (givenStateIsPhi) {
                return Character.toString(PHI);
            }
        }

        return Integer.toString(stateId);
    }

    private StringBuilder printMoves() {
        StringBuilder sb = new StringBuilder();
        Map<Move, Set<Character>> moveToLabel = getMoveToLabel();

        for (Move move : moveToLabel.keySet()) {
            String originalLabel = moveToLabel.get(move).toString();
            String label = originalLabel.substring(1, originalLabel.length() - 1);

            String from = this.getStateLabel(move.getFrom());
            String to = this.getStateLabel(move.getTo());

            sb.append("\t" + from + " -> " + to + " [label = \"" + label + "\"];\n");
        }

        sb.append("\n");
        return sb;
    }

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

    private StringBuilder printStartState() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> " + start.getId() + ";\n");
        return sb;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FSA other = (FSA) o;
        return Objects.equals(alphabet, other.alphabet)
                && Objects.equals(states, other.states)
                && Objects.equals(finalStates, other.finalStates)
                && Objects.equals(moves, other.moves)
                && Objects.equals(start, other.start);
    }

    States getStates() {
        return states;
    }

    States getFinalStates() {
        return finalStates;
    }

    Moves getMoves() {
        return moves;
    }
}

class Alphabet extends TreeSet<Character> {
    Alphabet(@NotNull Collection<? extends Character> alphabet) {
        super(alphabet);
    }

    Alphabet() {
    }
}

class State implements Comparable<State> {
    private static int idCounter;
    private final int id;
    private String alternativeLabel;

    State() {
        id = idCounter++;
    }

    State(int id) {
        this.id = id;
    }

    State(int id, States states) {
        this.id = id;
        String intermediate = states
                .stream()
                .map(State::toString)
                .collect(Collectors.joining(", "));
        alternativeLabel = "{" + intermediate + "}";
    }

    static void setIdCounter(int idCounter) {
        State.idCounter = idCounter;
    }

    State getTo(Moves moves, Character consumed) {
        Move move = moves
                .stream()
                .filter((m) -> m.hasFrom(this) && m.hasConsumed(consumed))
                .findFirst()
                .orElse(null);
        return move != null ? move.getTo() : null;
    }

    String getAlternativeLabel() {
        return alternativeLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State other = (State) o;
        return id == other.id && Objects.equals(alternativeLabel, other.alternativeLabel);
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public int compareTo(@NotNull State other) {
        return Comparator.comparing(State::getId)
                .thenComparing(state -> Objects.equals(this.alternativeLabel,
                        other.alternativeLabel))
                .compare(this, other);
    }

    int getId() {
        return id;
    }
}

class States extends TreeSet<State> implements Comparable<States> {
    States() {
    }

    States(@NotNull Collection<? extends State> c) {
        super(c);
    }

    @Override
    public int compareTo(@NotNull States other) {
        return Comparator
                .comparing(States::toString)
                .compare(this, other);
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

    State getFrom() {
        return from;
    }

    Character getConsumed() {
        return consumed;
    }

    State getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move other = (Move) o;
        return Objects.equals(from, other.from)
                && Objects.equals(consumed, other.consumed)
                && Objects.equals(to, other.to);
    }
}

class Moves extends TreeSet<Move> {
    Moves() {
    }

    Moves(@NotNull Collection<? extends Move> c) {
        super(c);
    }
}
