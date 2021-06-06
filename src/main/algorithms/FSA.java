package algorithms;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args) {
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
            System.out.println(e);
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
    public String toString() {
        return toDOT();
    }

    private String toDOT() {
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

    private void printStartState(int startId, StringBuilder sb) {
        sb.append("\n" +
                "\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> " + startId + ";\n" +
                "}\n"
        );
    }
}

class Alphabet extends TreeSet<Character> {
    Alphabet() {
        super();
    }
}

class State implements Comparable<State> {
    private static int idCounter;
    private final int id;
    private String closureLabel;

    State() {
        id = idCounter++;
    }

    State(int id) {
        this.id = id;
    }

    State(int id, String closureLabel) {
        this.id = id;
        this.closureLabel = closureLabel;
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
        State to = move != null ? move.getTo() : null;
        return to;
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

