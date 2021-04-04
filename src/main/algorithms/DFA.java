package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
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
        State dfaStart = new State();
        Map<State, Set<State>> closureMap = new TreeMap<>();
        Set<State> dfaStates = new TreeSet<>(Collections.singletonList(dfaStart));
        Set<Move> dfaMoves = new TreeSet<>();
        Stack<State> stack = new Stack<>(dfaStart);

        closureMap.put(dfaStart, getDFAStart(nfa));

        Alphabet nfaAlphabet = nfa.getAlphabet();
        Set<Move> nfaMoves = nfa.getMoves();

        while (!stack.isEmpty()) {
            State from = stack.pop();
            Set<State> fromClosure = closureMap.get(from);

            for (Character consumed : nfaAlphabet) {
                Set<State> reachableStates = getReachableStates(fromClosure, nfaMoves, consumed);
                Set<State> closureTo = epsilonClosure(reachableStates, nfaMoves);

                if (!closureTo.isEmpty()) {
                    State to = new State();
                    boolean isNewState = !dfaStates.contains(to);

                    if (isNewState) {
                        dfaStates.add(to);
                        stack.push(to);
                        dfaMoves.add(new Move(from, consumed, to));
                        closureMap.put(to, closureTo);
                    } else {
                        State.setIdCounter(to.getId() - 1);
                        dfaMoves.add(new Move(from, consumed, to));
                    }
                }
            }
        }

        Set<State> dfaFinalStates = new TreeSet<>();
        Set<State> nfaFinalStates = nfa.getFinalStates();

        for (State dfaState : dfaStates) {
            for (State nfaState : closureMap.get(dfaState)) {
                if (nfaFinalStates.contains(nfaState)) {
                    dfaFinalStates.add(dfaState);
                }
            }
        }

        return new DFA(nfaAlphabet, dfaStates, dfaStart, dfaFinalStates, dfaMoves);
    }

    private static boolean stateAlreadyExists(Set<State> dfaStates, Map<State, Set<State>> closureMap) {
        for (State dfaState : dfaStates) {
            for (State nfaState : closureMap.get(dfaState)) {

            }
        }

        return true;
    }

    @NotNull
    private static Set<State> getDFAStart(NFA nfa) {
        State nfaStart = nfa.getStart();
        Set<Move> nfaMoves = nfa.getMoves();
        Set<State> dfaStart = epsilonClosure(nfaStart, nfaMoves);
        return dfaStart;
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
