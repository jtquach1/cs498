package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class DFA extends NFA {
    DFA() {
        super();
    }

    DFA(Alphabet alphabet, Set<State> states, State start,
        Set<State> finalStates, Set<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
    }

    static Set<State> epsilonClosure(State state, Set<Move> moves) {
        Set<State> states = new TreeSet<>();
        states.add(state);
        return epsilonClosure(states, moves);
    }

    static Set<State> epsilonClosure(Set<State> states, Set<Move> moves) {
        Stack<State> stack = new Stack<>(states);
        Set<State> closure = new TreeSet<>(states);

        while (!stack.isEmpty()) {
            State from = stack.pop();
            Set<State> validTos = moves
                    .stream()
                    .filter(move -> move.getFrom().equals(from) && move.getConsumed().equals(EPSILON))
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

    static DFAState epsilonClosureDFA(Set<State> states, Set<Move> moves) {
        Set<State> closure = epsilonClosure(states, moves);
        DFAState state = new DFAState();
        state.addAll(closure);
        return state;
    }

    static DFAState epsilonClosureDFA(State states, Set<Move> moves) {
        Set<State> closure = epsilonClosure(states, moves);
        DFAState state = new DFAState();
        state.addAll(closure);
        return state;
    }

    static Set<State> getReachableStates(DFAState states, Set<Move> moves, Character consumed) {
        return getReachableStates(states.getStates(), moves, consumed);
    }

    static Set<State> getReachableStates(Set<State> states, Set<Move> moves, Character consumed) {
        Set<State> validTos = new TreeSet<>();
        for (State from : states) {
            Set<State> validStates = moves
                    .stream()
                    .filter(move -> move.getFrom().equals(from) && move.getConsumed().equals(consumed))
                    .map(Move::getTo)
                    .collect(Collectors.toSet());
            validTos.addAll(validStates);
        }
        return validTos;
    }

    static DFA NFAtoDFA(NFA nfa) {
        Alphabet nfaAlphabet = nfa.getAlphabet();
        Set<Move> nfaMoves = nfa.getMoves();
        State nfaStart = nfa.getStart();

        DFAState dfaStart = epsilonClosureDFA(nfaStart, nfaMoves);
        Set<DFAState> dfaStates = new TreeSet<>();
        dfaStates.add(dfaStart);
        Set<DFAMove> dfaMoves = new TreeSet<>();
        Stack<DFAState> stack = new Stack<>();
        stack.push(dfaStart);


        while (!stack.isEmpty()) {
            DFAState from = stack.pop();

            for (Character consumed : nfaAlphabet) {
                Set<State> reachableStates = getReachableStates(from, nfaMoves, consumed);
                DFAState to = epsilonClosureDFA(reachableStates, nfaMoves);

                if (!to.isEmpty()) {
                    // doing TreeSet.contains() causes an infinite loop
                    boolean isNewState = dfaStates
                            .stream()
                            .noneMatch(s -> s.getStates().equals(to.getStates()));

                    if (isNewState) {
                        dfaStates.add(to);
                        stack.push(to);
                    } else {
                        to.setId(to.getId() - 1);
                        DFAState.setIdCounter(to.getId());
                    }

                    dfaMoves.add(new DFAMove(from, consumed, to));
                }
            }
        }


        Set<State> dfaFinalStates = new TreeSet<>();
        Set<State> nfaFinalStates = nfa.getFinalStates();

        for (DFAState dfaState : dfaStates) {
            for (State nfaState : dfaState.getStates()) {
                if (nfaFinalStates.contains(nfaState)) {
                    dfaFinalStates.add(new State(dfaState.getId()));
                }
            }
        }

        return new DFA(nfaAlphabet,
                dfaStates.stream().map(d -> new State(d.getId())).collect(Collectors.toSet()),
                new State(dfaStart.getId()),
                dfaFinalStates,
                dfaMoves.stream().map(m -> new Move(
                        new State(m.getFrom().getId()),
                        m.getConsumed(),
                        new State(m.getTo().getId()))).collect(Collectors.toSet()));
    }

    DFA deepClone() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new TreeSet<>();
        State start = this.getStart();
        Set<State> finalStates = new TreeSet<>();
        Set<Move> moves = new TreeSet<>();

        alphabet.addAll(this.getAlphabet());
        states.addAll(this.getStates());
        finalStates.addAll(this.getFinalStates());
        moves.addAll(this.getMoves());

        return new DFA(alphabet, states, start, finalStates, moves);
    }

}
