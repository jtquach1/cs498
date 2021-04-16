package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.DFAMove.convertToMoves;
import static algorithms.DFAState.convertToState;
import static algorithms.DFAState.convertToStates;

class DFA extends FSA {
    Map<State, DFAState> closureMap;

    DFA(Alphabet alphabet, Set<State> states, State start,
        Set<State> finalStates, Set<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
        this.closureMap = new TreeMap<>();
    }

    DFA(Alphabet alphabet, Set<DFAState> dfaStates, DFAState dfaStart,
        Set<DFAState> dfaFinalStates, Set<DFAMove> dfaMoves) {
        super(
                alphabet,
                convertToStates(dfaStates),
                convertToState(dfaStart),
                convertToStates(dfaFinalStates),
                convertToMoves(dfaMoves)
        );

        this.closureMap = new TreeMap<>();
        for (DFAState dfaState : dfaStates) {
            closureMap.put(convertToState(dfaState), dfaState);
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

        int index = 0;
        DFAState dfaStart = epsilonClosure(nfaStart, nfaMoves, index++);
        Set<DFAState> dfaStates = new TreeSet<>();
        dfaStates.add(dfaStart);
        Set<DFAMove> dfaMoves = new TreeSet<>();
        Stack<DFAState> stack = new Stack<>();
        stack.push(dfaStart);

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

        Set<State> nfaFinalStates = nfa.getFinalStates();
        Set<DFAState> dfaFinalStates = getDFAFinalStates(dfaStates, nfaFinalStates);

        return new DFA(alphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves);
    }

    @NotNull
    private static Set<DFAState> getDFAFinalStates(Set<DFAState> dfaStates, Set<State> nfaFinalStates) {
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

    private static Set<State> getReachableStates(DFAState states, Set<Move> moves, Character consumed) {
        return getReachableStates(states.getStates(), moves, consumed);
    }

    private static Set<State> getReachableStates(Set<State> states, Set<Move> moves, Character consumed) {
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
                .map(dfaMove -> new Move(
                        convertToState(dfaMove.getFrom()),
                        dfaMove.getConsumed(),
                        convertToState(dfaMove.getTo())))
                .collect(Collectors.toSet());
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
    static State convertToState(DFAState dfaState) {
        return new State(dfaState.getId());
    }

    @NotNull
    static Set<State> convertToStates(Set<DFAState> dfaStates) {
        return dfaStates
                .stream()
                .map(DFAState::convertToState)
                .collect(Collectors.toSet());
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
