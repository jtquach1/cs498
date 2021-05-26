package algorithms;

import org.jetbrains.annotations.NotNull;

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
            State phi
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
        if (!everyStateConsumesEntireAlphabet) {
            this.phi = phi;
            this.addState(phi);
            for (Character consumed : this.getAlphabet()) {
                this.addMove(phi, consumed, phi);
            }
        }
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

    static DFA NFAtoDFA(NFA nfa) {
        Alphabet alphabet = nfa.getAlphabet();
        State nfaStart = nfa.getStart();
        Set<Move> nfaMoves = nfa.getMoves();
        Set<State> nfaFinalStates = nfa.getFinalStates();

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

        return new DFA(alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves, phi);
    }

    static DFA DFAtoMinDFA(DFA dfa) {
        Partition partition = dfa.getPartition();
        return dfa.createDFAFromPartition(partition);
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

    @NotNull
    Partition getPartition() {
        Set<Move> moves = this.getMoves();

        Partition partition = this.initializePartition();
        Partition previous;

        boolean splittingOccurs = true;
        while (splittingOccurs) {
            // Iterating over partition while mutating it will throw a concurrency exception
            previous = (Partition) partition.clone();

            for (PSet set : previous) {
                if (set.size() > 1) {
                    for (Character consumed : this.getAlphabet()) {
                        for (State from : set) {
                            State to = from.getTo(moves, consumed);
                            PSet targetSet = partition.getSetContainingState(to);
                            PSet included = targetSet.getInboundStates(moves, set, consumed);
                            PSet excluded = targetSet.getOutboundStates(moves, set, consumed);
                            if (!excluded.isEmpty()) {
                                partition.remove(set);
                                partition.add(included);
                                partition.add(excluded);
                                break;
                            }
                        }
                    }
                }
            }
            if (partition.equals(previous)) {
                splittingOccurs = false;
            }
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

        return new DFA(alphabet, dfaStates, start, finalDfaStates, moves, phi);
    }

    private DFAState findDFAState(Set<DFAState> dfaStates, State state) {
        DFAState converted = null;

        for (DFAState dfaState : dfaStates) {
            if (dfaState.getStates().contains(state)) {
                converted = dfaState;
            }
        }
        return converted;
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
        return this.from;
    }

    Character getConsumed() {
        return this.consumed;
    }

    DFAState getTo() {
        return this.to;
    }

    @Override
    public int compareTo(@NotNull DFAMove other) {
        return Comparator.comparing(DFAMove::getFrom)
                .thenComparing(DFAMove::getConsumed)
                .thenComparing(DFAMove::getTo)
                .compare(this, other);
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

    @Override
    public int hashCode() {
        return Objects.hash(from, consumed, to);
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
        return new State(this.getId());
    }

    int getId() {
        return this.id;
    }

    TreeSet<State> getStates() {
        return this.states;
    }

    void addAll(Set<State> closure) {
        states.addAll(closure);
    }

    boolean isEmpty() {
        return states.isEmpty();
    }

    void updateWithExistingId(Set<DFAState> dfaStates) {
        Object[] match = dfaStates.stream()
                .filter(s -> s.states.equals(this.states))
                .distinct()
                .toArray();

        if (match.length != 0) {
            int id = Integer.parseInt(match[0].toString());
            this.id = id;
        }
    }

    boolean isNewState(Set<DFAState> dfaStates) {
        return dfaStates
                .stream()
                .noneMatch(s -> s.states.equals(this.states));
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
        DFAState state = (DFAState) o;
        return this.states.equals(state.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, states);
    }

    @Override
    public String toString() {
        return id + "";
    }
}

class Partition extends TreeSet<PSet> {
    Partition() {
        super();
    }

    PSet getSetContainingState(State from) {
        PSet targetSet = new PSet();
        for (PSet set : this) {
            if (set.contains(from)) {
                targetSet = set;
            }
        }
        return targetSet;
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

}

class PSet extends TreeSet<State> implements Comparable<PSet> {
    PSet() {
        super();
    }

    PSet getInboundStates(Set<Move> moves, PSet set, Character consumed) {
        PSet included = new PSet();
        for (State from : set) {
            State to = null;
            for (Move move : moves) {
                if (move.getFrom().equals(from) && move.getConsumed().equals(consumed)) {
                    to = move.getTo();
                }
            }
            if (this.contains(to)) {
                included.add(from);
            }
        }
        return included;
    }

    PSet getOutboundStates(Set<Move> moves, PSet set, Character consumed) {
        PSet excluded = new PSet();
        for (State from : set) {
            State to = null;
            for (Move move : moves) {
                if (move.getFrom().equals(from) && move.getConsumed().equals(consumed)) {
                    to = move.getTo();
                }
            }
            if (!this.contains(to)) {
                excluded.add(from);
            }
        }
        return excluded;
    }

    @Override
    public int compareTo(@NotNull PSet other) {
        return Comparator.comparing(PSet::toString)
                .compare(this, other);
    }
}