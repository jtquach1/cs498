package algorithms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.DFAMove.convertToMoves;
import static algorithms.DFAState.convertToStates;

class DFA extends FSA {
    // For printing DOT output
    private State phi;

    // For unit testing
    DFA(
            Alphabet alphabet,
            Set<State> states,
            State start,
            Set<State> finalStates,
            Set<Move> moves,
            State phi
    ) {
        super(alphabet, states, start, finalStates, moves);
        this.phi = phi;
    }

    DFA(
            Alphabet alphabet,
            Set<DFAState> dfaStates,
            DFAState dfaStart,
            Set<DFAState> dfaFinalStates,
            Set<DFAMove> dfaMoves,
            State phi,
            boolean convertingFromMinDFA
    ) {
        super(
                alphabet,
                convertToStates(dfaStates),
                dfaStart.convertToState(),
                convertToStates(dfaFinalStates),
                convertToMoves(dfaMoves)
        );
        boolean everyStateConsumesEntireAlphabet = true;
        for (State from : this.getStates()) {
            Set<Character> consumedChars = this.getMoves()
                    .stream()
                    .filter(move -> move.hasFrom(from))
                    .map(Move::getConsumed)
                    .collect(Collectors.toSet());
            for (Character consumed : this.getAlphabet()) {
                if (!consumedChars.contains(consumed)) {
                    this.addMove(from, consumed, phi);
                    everyStateConsumesEntireAlphabet = false;
                }
            }
        }
        setPhi(phi, convertingFromMinDFA, everyStateConsumesEntireAlphabet);
    }

    static DFAState epsilonClosure(Set<State> states, Set<Move> moves, int index) {
        Set<State> closure = epsilonClosure(states, moves);
        DFAState state = new DFAState(index);
        state.addAll(closure);
        return state;
    }

    static DFAState epsilonClosure(State state, Set<Move> moves, int index) {
        Set<State> closure = epsilonClosure(state, moves);
        DFAState dfaState = new DFAState(index);
        dfaState.addAll(closure);
        return dfaState;
    }

    private static boolean representsEmptyLanguage(NFA nfa) {
        Alphabet alphabet = new Alphabet();
        return nfa.getAlphabet().equals(alphabet);
    }

    static DFA NFAtoDFA(NFA nfa) {
        Alphabet alphabet = nfa.getAlphabet();
        State nfaStart = nfa.getStart();
        Set<Move> nfaMoves = nfa.getMoves();
        Set<State> nfaFinalStates = nfa.getFinalStates();

        if (representsEmptyLanguage(nfa)) {
            return dfaRepresentingEmptyLanguage();
        }
        int index = 0;
        DFAState dfaStart = epsilonClosure(nfaStart, nfaMoves, index);
        Set<DFAState> dfaStates = new TreeSet<>();
        dfaStates.add(dfaStart);
        Set<DFAMove> dfaMoves = new TreeSet<>();
        Stack<DFAState> stack = new Stack<>();
        stack.push(dfaStart);
        index++;

        index = updateIndexAndComputeStates(alphabet, nfaMoves, index, dfaStates, dfaMoves, stack);
        Set<DFAState> dfaFinalStates = getDFAFinalStates(dfaStates, nfaFinalStates);
        State phi = new State(index);

        return new DFA(alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves, phi, false);
    }

    @NotNull
    private static DFA dfaRepresentingEmptyLanguage() {
        Alphabet alphabet = new Alphabet();
        State dfaStart = new State(0);
        Set<State> dfaStates = new TreeSet<>();
        dfaStates.add(dfaStart);

        Set<State> dfaFinalStates = new TreeSet<>();
        dfaFinalStates.add(dfaStart);

        Set<Move> dfaMoves = new TreeSet<>();
        return new DFA(alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves, null);
    }

    static DFA DFAtoMinDFA(DFA dfa) {
        if (dfa.equals(dfaRepresentingEmptyLanguage())) {
            return dfa;
        }

        Partition partition = dfa.getPartition();
        return dfa.createDFAFromPartition(partition);
    }

    public static void main(String[] args) {
        String flag = Arrays
                .stream(args)
                .filter(arg -> arg.startsWith("-"))
                .findFirst()
                .orElse(null);
        String infix = Arrays
                .stream(args)
                .filter(arg -> !arg.startsWith("-"))
                .findFirst()
                .orElse("");
        try {
            NFA nfa = NFA.regexToNFA(infix);
            DFA dfa = DFA.NFAtoDFA(nfa);
            if (flag != null && flag.equals("-m")) {
                dfa = DFA.DFAtoMinDFA(dfa);
            }
            String dot = dfa.toString();
            System.out.println(dot);
        } catch (Exception e) {
            System.out.println("Invalid regular expression");
        }
    }

    private static int updateIndexAndComputeStates(
            Alphabet alphabet,
            Set<Move> nfaMoves,
            Integer index,
            Set<DFAState> dfaStates,
            Set<DFAMove> dfaMoves,
            Stack<DFAState> stack
    ) {
        while (!stack.isEmpty()) {
            DFAState from = stack.pop();

            for (Character consumed : alphabet) {
                Set<State> reachableStates = getReachableStates(from, nfaMoves, consumed);
                DFAState to = epsilonClosure(reachableStates, nfaMoves, index);

                if (!to.isEmpty()) {

                    if (to.isNewState(dfaStates)) {
                        dfaStates.add(to);
                        stack.push(to);
                        index++;
                    } else {
                        to.updateWithExistingId(dfaStates);
                    }

                    dfaMoves.add(new DFAMove(from, consumed, to));
                }
            }
        }
        return index;
    }

    @NotNull
    private static Set<DFAState> getDFAFinalStates(
            Set<DFAState> dfaStates,
            Set<State> nfaFinalStates
    ) {
        Set<DFAState> dfaFinalStates = new TreeSet<>();
        for (DFAState dfaState : dfaStates) {
            for (State nfaState : dfaState.getStates()) {
                if (nfaFinalStates.contains(nfaState)) {
                    dfaFinalStates.add(dfaState);
                }
            }
        }

        return dfaFinalStates;
    }

    private static Set<State> getReachableStates(
            DFAState states,
            Set<Move> moves,
            Character consumed
    ) {
        return getReachableStates(states.getStates(), moves, consumed);
    }

    private static Set<State> getReachableStates(
            Set<State> states,
            Set<Move> moves,
            Character consumed
    ) {
        Set<State> validTos = new TreeSet<>();
        for (State from : states) {
            Set<State> validStates = moves
                    .stream()
                    .filter(move -> move.hasFrom(from) && move.hasConsumed(consumed))
                    .map(Move::getTo)
                    .collect(Collectors.toSet());
            validTos.addAll(validStates);
        }
        return validTos;
    }

    private static Set<State> epsilonClosure(State state, Set<Move> moves) {
        Set<State> states = new TreeSet<>();
        states.add(state);
        return epsilonClosure(states, moves);
    }

    private static Set<State> epsilonClosure(Set<State> states, Set<Move> moves) {
        Stack<State> stack = new Stack<>(states);
        Set<State> closure = new TreeSet<>(states);

        while (!stack.isEmpty()) {
            State from = stack.pop();
            Set<State> validTos = moves
                    .stream()
                    .filter(move -> move.hasFrom(from) && move.hasConsumed(EPSILON))
                    .map(Move::getTo)
                    .collect(Collectors.toSet());

            for (State to : validTos) {
                if (!closure.contains(to)) {
                    stack.push(to);
                    closure.add(to);
                }
            }
        }

        return closure;
    }

    private static boolean isStillSplitting(Partition partition, Partition previous) {
        return !partition.equals(previous);
    }

    private static DFAState findDFAState(Set<DFAState> dfaStates, State state) {
        return dfaStates
                .stream()
                .filter((dfaState) -> dfaState.getStates().contains(state))
                .findAny()
                .orElse(null);
    }

    State getPhi() {
        return this.phi;
    }

    private void setPhi(
            State phi,
            boolean convertingFromMinDFA,
            boolean everyStateConsumesEntireAlphabet
    ) {
        if (!everyStateConsumesEntireAlphabet || convertingFromMinDFA) {
            this.phi = phi;
            this.addState(phi);
            for (Character consumed : this.getAlphabet()) {
                this.addMove(phi, consumed, phi);
            }
        }
    }

    @NotNull
    Partition getPartition() {
        Set<Move> moves = this.getMoves();
        Partition partition = this.initializePartition();
        Partition previous;

        boolean splittingOccurs = true;
        while (splittingOccurs) {
            // Iterating over the partition while mutating it will throw a concurrency exception
            previous = (Partition) partition.clone();

            for (PSet set : previous) {
                if (set.size() > 1) {
                    for (Character consumed : this.getAlphabet()) {
                        for (State from : set) {
                            State to = from.getTo(moves, consumed);
                            PSet targetSet = partition.getExistingSetContainingState(to);
                            PSet included = targetSet.getIncludedStates(moves, set, consumed);
                            PSet excluded = targetSet.getExcludedStates(moves, set, consumed);
                            if (!excluded.isEmpty()) {
                                partition.replaceSet(set, included, excluded);
                                break;
                            }
                        }
                    }
                }
            }
            splittingOccurs = isStillSplitting(partition, previous);
        }

        return partition;
    }

    @NotNull
    private Partition initializePartition() {
        Partition partition = new Partition();
        PSet finalStates = new PSet();
        finalStates.addAll(this.getFinalStates());

        PSet S = new PSet();
        S.addAll(this.getStates());
        S.removeAll(finalStates);

        partition.add(S);
        partition.add(finalStates);
        return partition;
    }

    private DFA createDFAFromPartition(Partition partition) {
        Alphabet alphabet = this.getAlphabet();
        Set<DFAState> dfaStates = partition.convertToDfaStates();

        DFAState start = findDFAState(dfaStates, this.getStart());
        State phi = findDFAState(dfaStates, this.getPhi()).convertToState();

        Set<DFAState> finalDfaStates = this
                .getFinalStates()
                .stream()
                .map((state) -> findDFAState(dfaStates, state))
                .collect(Collectors.toSet());

        Set<DFAMove> moves = this
                .getMoves()
                .stream()
                .map((move) -> new DFAMove(
                        findDFAState(dfaStates, move.getFrom()),
                        move.getConsumed(),
                        findDFAState(dfaStates, move.getTo())
                ))
                .collect(Collectors.toSet());

        // DFA states already consume every letter of the alphabet
        return new DFA(
                alphabet,
                dfaStates,
                start,
                finalDfaStates,
                moves,
                phi,
                true
        );
    }


}

class DFAMove implements Comparable<DFAMove> {
    private final DFAState from;
    private final Character consumed;
    private final DFAState to;

    DFAMove(DFAState from, Character consumed, DFAState to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    @NotNull
    static Set<Move> convertToMoves(Set<DFAMove> dfaMoves) {
        return dfaMoves
                .stream()
                .map(DFAMove::convertToMove)
                .collect(Collectors.toSet());
    }

    Move convertToMove() {
        return new Move(
                this.getFrom().convertToState(),
                this.getConsumed(),
                this.getTo().convertToState()
        );
    }

    DFAState getFrom() {
        return from;
    }

    Character getConsumed() {
        return consumed;
    }

    DFAState getTo() {
        return to;
    }

    @Override
    public int compareTo(@NotNull DFAMove other) {
        return Comparator.comparing(DFAMove::getFrom)
                .thenComparing(DFAMove::getConsumed)
                .thenComparing(DFAMove::getTo)
                .compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, consumed, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAMove move = (DFAMove) o;
        return Objects.equals(from, move.from)
                && Objects.equals(consumed, move.consumed)
                && Objects.equals(to, move.to);
    }

}


class DFAState implements Comparable<DFAState> {
    private final TreeSet<State> states;
    private int id;

    DFAState(int id) {
        this.id = id;
        this.states = new TreeSet<>();
    }

    @NotNull
    static Set<State> convertToStates(Set<DFAState> dfaStates) {
        return dfaStates
                .stream()
                .map(DFAState::convertToState)
                .collect(Collectors.toSet());
    }

    @NotNull
    State convertToState() {
        return new State(id);
    }

    int getId() {
        return id;
    }

    TreeSet<State> getStates() {
        return states;
    }

    void addAll(Set<State> set) {
        states.addAll(set);
    }

    boolean isEmpty() {
        return states.isEmpty();
    }

    void updateWithExistingId(Set<DFAState> dfaStates) {
        dfaStates
                .stream()
                .filter(dfaState -> dfaState.states.equals(states))
                .findFirst()
                .ifPresent(match -> this.id = match.getId());
    }

    boolean isNewState(Set<DFAState> dfaStates) {
        return dfaStates
                .stream()
                .noneMatch(dfaState -> dfaState.states.equals(states));
    }

    @Override
    public int compareTo(@NotNull DFAState other) {
        return Comparator.comparing(DFAState::getId)
                .thenComparing(s -> s.getStates().toString())
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAState dfaState = (DFAState) o;
        return this.states.equals(dfaState.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(states, id);
    }
}

class Partition extends TreeSet<PSet> {
    Partition() {
        super();
    }

    PSet getExistingSetContainingState(State from) {
        return this
                .stream()
                .filter((set) -> set.contains(from))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    Set<DFAState> convertToDfaStates() {
        Set<DFAState> dfaStates = new TreeSet<>();
        int id = 0;
        for (PSet set : this) {
            DFAState dfaState = new DFAState(id);
            dfaState.addAll(set);
            dfaStates.add(dfaState);
            id++;
        }
        return dfaStates;
    }

    void replaceSet(PSet set, PSet included, PSet excluded) {
        this.remove(set);
        this.add(included);
        this.add(excluded);
    }
}

class PSet extends TreeSet<State> implements Comparable<PSet> {
    PSet() {
        super();
    }

    PSet getIncludedStates(Set<Move> moves, PSet set, Character consumed) {
        PSet included = new PSet();
        included.addAll(set
                .stream()
                .filter((from) -> this.contains(getTo(moves, consumed, from)))
                .collect(Collectors.toSet()));
        return included;
    }

    PSet getExcludedStates(Set<Move> moves, PSet set, Character consumed) {
        PSet excluded = new PSet();
        excluded.addAll(set
                .stream()
                .filter((from) -> !this.contains(getTo(moves, consumed, from)))
                .collect(Collectors.toSet()));
        return excluded;
    }

    @Nullable
    private State getTo(Set<Move> moves, Character consumed, State from) {
        Move move = moves
                .stream()
                .filter((m) -> m.hasFrom(from) && m.hasConsumed(consumed))
                .findFirst()
                .orElse(null);
        return move != null ? move.getTo() : null;
    }

    @Override
    public int compareTo(@NotNull PSet other) {
        return Comparator.comparing(PSet::toString)
                .compare(this, other);
    }
}