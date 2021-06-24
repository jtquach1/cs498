package algorithms;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

class FSA {
    static final char EPSILON = '\u025B';
    static final char PHI = '\u03C6';

    private final Alphabet alphabet;
    private final Set<State> states;
    private final Set<State> finalStates;
    private final Set<Move> moves;
    private State start;

    FSA(Alphabet alphabet, Set<State> states, State start, Set<State> finalStates,
        Set<Move> moves) {
        this.alphabet = alphabet;
        this.states = states;
        this.start = start;
        this.finalStates = finalStates;
        this.moves = moves;
    }

    FSA(State start) {
        alphabet = new Alphabet();
        states = new TreeSet<>();
        finalStates = new TreeSet<>();
        moves = new TreeSet<>();
        states.add(start);
        this.start = start;
    }

    public static void main(String[] args) throws IOException {
        String inputRegex = null;
        String outputPrefix = null;
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-i")) {
                    inputRegex = getNonFlag(args, i + 1);
                }
                if (args[i].equals("-o")) {
                    outputPrefix = getNonFlag(args, i + 1);
                }
            }
            if (inputRegex == null) {
                inputRegex = "";
            }

            NFA nfa = NFA.regexToNFA(inputRegex);
            DFA dfa = DFA.NFAtoDFA(nfa);
            DFA minDfa = DFA.DFAtoMinDFA(dfa);

            TreeMap<String, String> FSAToDOT = new TreeMap<>();
            FSAToDOT.put("nfa", nfa.toString());
            FSAToDOT.put("dfa", dfa.toString());
            FSAToDOT.put("minDfa", minDfa.toString());

            PrintWriter writer;
            for (String fsaType : FSAToDOT.keySet()) {
                String fileName = outputPrefix + "." + fsaType + ".dot";
                writer = new PrintWriter(fileName, StandardCharsets.UTF_8);
                writer.print(FSAToDOT.get(fsaType));
                writer.close();
            }

        } catch (NullPointerException e) {
            if (outputPrefix == null) {
                System.out.println("ERROR: Output filename prefix not specified");
            }
        } catch (Exception e) {
            System.out.println("ERROR: Invalid regular expression");
            throw e;
        }
    }

    private static String getNonFlag(String[] args, int index) {
        String result = args[index];
        if (result.startsWith("-")) {
            return null;
        }
        return result;
    }

    void addSymbol(Character symbol) {
        alphabet.add(symbol);
    }

    void removeSymbol(Character symbol) {
        alphabet.remove(symbol);
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
    public int hashCode() {
        return Objects.hash(alphabet, states, finalStates, moves, start);
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

    @Override
    public String toString() {
        return toDOT();
    }

    private String toDOT() {
        StringBuilder sb = new StringBuilder();
        printHeader(sb);
        printFinalStates(sb);
        printStates(sb);
        printMoves(sb);
        printStartState(sb);
        printFooter(sb);

        return sb.toString();
    }

    private static void printFooter(StringBuilder sb) {
        sb.append("}\n");
    }

    private static void printHeader(StringBuilder sb) {
        sb.append("digraph finite_state_machine {\n" +
                "\trankdir=LR;\n" +
                "\tsize=\"8,5\";\n" +
                "\n");
    }

    private void printFinalStates(StringBuilder sb) {
        sb.append("\tnode [shape = doublecircle];\n\t");
        for (State finalState : finalStates) {
            sb.append(finalState.getId() + " ");
        }
        sb.append(";\n\n");
    }

    private void printStates(StringBuilder sb) {
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
    }

    private String getStateLabel(State state) {
        boolean thisIsDFAWithPhiState = this instanceof DFA && ((DFA) this).getPhi() != null;
        int stateId = state.getId();

        if (thisIsDFAWithPhiState) {
            boolean isGivenStatePhi = stateId == ((DFA) this).getPhi().getId();

            if (isGivenStatePhi) {
                return Character.toString(PHI);
            }
        }

        return Integer.toString(stateId);
    }

    private void printMoves(StringBuilder sb) {
        Map<Move, Set<Character>> moveToLabel = getMoveToLabel();
        for (Move move : moveToLabel.keySet()) {
            String originalLabel = moveToLabel.get(move).toString();
            String label = originalLabel.substring(1, originalLabel.length() - 1);

            String from = this.getStateLabel(move.getFrom());
            String to = this.getStateLabel(move.getTo());

            sb.append("\t" + from + " -> " + to + " [label = \"" + label + "\"];\n");
        }
        sb.append("\n");
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

    private void printStartState(StringBuilder sb) {
        sb.append("\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> " + start.getId() + ";\n");
    }
}

class Alphabet extends TreeSet<Character> {
    Alphabet(@NotNull Collection<? extends Character> alphabet) {
        super(alphabet);
    }

    Alphabet() {
        super();
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

    State(int id, Set<State> states) {
        this.id = id;
        String oldLabel = states.toString();
        oldLabel = oldLabel.substring(1, oldLabel.length() - 1);
        this.alternativeLabel = "{" + oldLabel + "}";
    }

    static void setIdCounter(int idCounter) {
        State.idCounter = idCounter;
    }

    State getTo(Set<Move> moves, Character consumed) {
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
    public int compareTo(@NotNull State other) {
        return Comparator.comparing(State::getId)
                .compare(this, other);
    }

    int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
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
    public int hashCode() {
        return Objects.hash(from, consumed, to);
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
}

