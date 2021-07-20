package algorithms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.DFAMove.convertToMoves;
import static algorithms.DFAState.convertToStates;
import static algorithms.Utility.makePartition;

class DFA extends FSA {
    // For printing DOT output
    private State phi;

    DFA(Alphabet alphabet, States states, State start, States finalStates, Moves moves) {
        super(alphabet, states, start, finalStates, moves);
    }

    DFA(Alphabet alphabet, States states, State start, States finalStates, Moves moves, State phi) {
        super(alphabet, states, start, finalStates, moves);
        this.phi = phi;
    }

    DFA(Alphabet alphabet, DFAStates dfaStates, DFAState dfaStart, DFAStates dfaFinalStates,
        DFAMoves dfaMoves, State phi, boolean convertingToMinDFA) {
        super(alphabet, convertToStates(dfaStates), dfaStart.convertToState(),
                convertToStates(dfaFinalStates), convertToMoves(dfaMoves));

        if (phi != null) {
            boolean everyStateConsumesEntireAlphabet = true;

            for (State from : states) {
                Set<Character> consumedChars = moves
                        .stream()
                        .filter(move -> move.hasFrom(from))
                        .map(Move::getConsumed)
                        .collect(Collectors.toCollection(TreeSet::new));

                for (Character consumed : alphabet) {
                    if (!consumedChars.contains(consumed)) {
                        addMove(from, consumed, phi);
                        everyStateConsumesEntireAlphabet = false;
                    }
                }
            }

            if (!everyStateConsumesEntireAlphabet || convertingToMinDFA) {
                this.phi = phi;
                addState(phi);
                for (Character consumed : alphabet) {
                    addMove(phi, consumed, phi);
                }
            }
        }
    }

    static DFA NFAtoDFA(NFA nfa) {
        int index = 0;
        DFAState dfaStart = epsilonClosure(nfa.start, nfa.moves, index++);
        DFAStates dfaStates = new DFAStates(Collections.singleton(dfaStart));
        DFAMoves dfaMoves = new DFAMoves();
        Stack<DFAState> stack = new Stack<>(Collections.singleton(dfaStart));

        while (!stack.isEmpty()) {
            DFAState from = stack.pop();

            for (Character consumed : nfa.alphabet) {
                States reachableStates = getReachableStates(from, nfa.moves, consumed);
                DFAState to = epsilonClosure(reachableStates, nfa.moves, index);

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

        DFAStates dfaFinalStates = getDFAFinalStates(dfaStates, nfa.finalStates);
        State phi = new State(index);

        // DFA states already consume every letter of the alphabet
        return new DFA(nfa.alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves, phi, false);
    }

    static DFAState epsilonClosure(State state, Moves moves, int index) {
        States closure = epsilonClosure(state, moves);
        return new DFAState(index, closure);
    }

    private static States epsilonClosure(State state, Moves moves) {
        States states = new States();
        states.add(state);
        return epsilonClosure(states, moves);
    }

    private static States epsilonClosure(States states, Moves moves) {
        Stack<State> stack = new Stack<>(states);
        States closure = new States(states);

        while (!stack.isEmpty()) {
            State from = stack.pop();
            States validTos = moves
                    .stream()
                    .filter(move -> move.hasFrom(from) && move.hasConsumed(EPSILON))
                    .map(Move::getTo)
                    .collect(Collectors.toCollection(States::new));

            for (State to : validTos) {
                if (!closure.contains(to)) {
                    stack.push(to);
                    closure.add(to);
                }
            }
        }

        return closure;
    }

    private static States getReachableStates(DFAState dfaState, Moves moves, Character consumed) {
        States states = dfaState.getStates();
        States validTos = new States();

        for (State from : states) {
            States validStates = moves
                    .stream()
                    .filter(move -> move.hasFrom(from) && move.hasConsumed(consumed))
                    .map(Move::getTo)
                    .collect(Collectors.toCollection(States::new));
            validTos.addAll(validStates);
        }

        return validTos;
    }

    static DFAState epsilonClosure(States states, Moves moves, int index) {
        States closure = epsilonClosure(states, moves);
        return new DFAState(index, closure);
    }

    @NotNull
    private static DFAStates getDFAFinalStates(DFAStates dfaStates, States nfaFinalStates) {
        DFAStates dfaFinalStates = new DFAStates();
        for (DFAState dfaState : dfaStates) {
            for (State nfaState : dfaState.getStates()) {
                if (nfaFinalStates.contains(nfaState)) {
                    dfaFinalStates.add(dfaState);
                }
            }
        }

        return dfaFinalStates;
    }

    static DFA DFAtoMinDFA(DFA dfa) {
        Partition partition = dfa.getPartition();
        return dfa.createDFAFromPartition(partition);
    }

    @NotNull
    Partition getPartition() {
        Partition partition = this.initializePartition();
        Partition previous;

        boolean splittingOccurs = true;
        while (splittingOccurs) {
            // Iterating over the partition while mutating it will throw a concurrency exception
            previous = (Partition) partition.clone();

            for (PSet set : previous) {
                if (set.size() > 1) {
                    for (Character consumed : alphabet) {
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

    private static boolean isStillSplitting(Partition partition, Partition previous) {
        return !partition.equals(previous);
    }

    @NotNull
    private Partition initializePartition() {
        PSet finalStates = new PSet(this.finalStates);
        PSet states = new PSet(this.states);
        states.removeAll(finalStates);

        return makePartition(states, finalStates);
    }

    private DFA createDFAFromPartition(Partition partition) {
        DFAStates dfaStates = partition.convertToDfaStates();
        DFAState dfaStart = findDFAState(dfaStates, start);
        State phi = this.phi != null ? findDFAState(dfaStates, this.phi).convertToState() : null;

        DFAStates dfaFinalStates = finalStates
                .stream()
                .map((state) -> findDFAState(dfaStates, state))
                .collect(Collectors.toCollection(DFAStates::new));

        DFAMoves dfaMoves = moves
                .stream()
                .map((move) -> new DFAMove(
                        findDFAState(dfaStates, move.getFrom()),
                        move.getConsumed(),
                        findDFAState(dfaStates, move.getTo())
                ))
                .collect(Collectors.toCollection(DFAMoves::new));

        return new DFA(alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves, phi, true);
    }

    private static DFAState findDFAState(DFAStates dfaStates, State state) {
        return dfaStates
                .stream()
                .filter((dfaState) -> dfaState.getStates().contains(state))
                .findFirst()
                .orElse(null);
    }

    State getPhi() {
        return this.phi;
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
    static Moves convertToMoves(DFAMoves dfaMoves) {
        return dfaMoves
                .stream()
                .map(DFAMove::convertToMove)
                .collect(Collectors.toCollection(Moves::new));
    }

    Move convertToMove() {
        return new Move(
                from.convertToState(),
                consumed,
                to.convertToState()
        );
    }

    @Override
    public int compareTo(@NotNull DFAMove other) {
        return Comparator.comparing(DFAMove::getFrom)
                .thenComparing(DFAMove::getConsumed)
                .thenComparing(DFAMove::getTo)
                .compare(this, other);
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
    public int hashCode() {
        return Objects.hash(from, consumed, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAMove other = (DFAMove) o;
        return Objects.equals(from, other.from)
                && Objects.equals(consumed, other.consumed)
                && Objects.equals(to, other.to);
    }
}

class DFAMoves extends TreeSet<DFAMove> {
    public DFAMoves() {
    }

    public DFAMoves(@NotNull Collection<? extends DFAMove> c) {
        super(c);
    }
}

class DFAState implements Comparable<DFAState> {
    private final States states;
    private int id;

    DFAState(int id, States states) {
        this.id = id;
        this.states = states;
    }

    @NotNull
    static States convertToStates(DFAStates dfaStates) {
        return dfaStates
                .stream()
                .map(DFAState::convertToState)
                .collect(Collectors.toCollection(States::new));
    }

    @NotNull
    State convertToState() {
        return new State(id, states);
    }

    boolean isEmpty() {
        return states.isEmpty();
    }

    void updateWithExistingId(DFAStates dfaStates) {
        dfaStates
                .stream()
                .filter(dfaState -> dfaState.states.equals(states))
                .findFirst()
                .ifPresent(match -> this.id = match.id);
    }

    boolean isNewState(DFAStates dfaStates) {
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

    int getId() {
        return id;
    }

    States getStates() {
        return states;
    }

    @Override
    public int hashCode() {
        return Objects.hash(states, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAState other = (DFAState) o;
        return this.states.equals(other.states);
    }
}

class DFAStates extends TreeSet<DFAState> {
    public DFAStates() {
    }

    public DFAStates(@NotNull Collection<? extends DFAState> c) {
        super(c);
    }
}

class Partition extends TreeSet<PSet> {
    Partition() {
    }

    Partition(@NotNull Collection<? extends PSet> c) {
        super(c);
    }

    PSet getExistingSetContainingState(State from) {
        return this
                .stream()
                .filter((set) -> set.contains(from))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    DFAStates convertToDfaStates() {
        DFAStates dfaStates = new DFAStates();
        int id = 0;

        /* We only create states out of non-empty partitions. A Partition set might be empty
        in the case that we are making a minimal DFA representing the empty language. */
        for (PSet set : this) {
            if (!set.isEmpty()) {
                States states = new States(set);
                DFAState dfaState = new DFAState(id, states);
                dfaStates.add(dfaState);
                id++;
            }
        }

        return dfaStates;
    }

    void replaceSet(PSet set, PSet included, PSet excluded) {
        this.remove(set);
        this.add(included);
        this.add(excluded);
    }
}

class PSet extends States implements Comparable<PSet> {
    PSet(@NotNull Collection<? extends State> states) {
        super(states);
    }

    PSet getIncludedStates(Moves moves, PSet set, Character consumed) {
        PSet included = new PSet();
        included.addAll(set
                .stream()
                .filter((from) -> this.contains(getTo(moves, consumed, from)))
                .collect(Collectors.toCollection(PSet::new)));
        return included;
    }

    PSet() {
    }

    @Nullable
    private State getTo(Moves moves, Character consumed, State from) {
        Move move = moves
                .stream()
                .filter((m) -> m.hasFrom(from) && m.hasConsumed(consumed))
                .findFirst()
                .orElse(null);
        return move != null ? move.getTo() : null;
    }

    PSet getExcludedStates(Moves moves, PSet set, Character consumed) {
        return set
                .stream()
                .filter((from) -> !this.contains(getTo(moves, consumed, from)))
                .collect(Collectors.toCollection(PSet::new));
    }

    @Override
    public int compareTo(@NotNull PSet other) {
        return Comparator.comparing(PSet::toString)
                .compare(this, other);
    }
}