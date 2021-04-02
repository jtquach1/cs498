import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DFA extends NFA {
    DFA() {
        super();
    }

    DFA(Alphabet alphabet, Set<State> states, State start,
        Set<State> finalStates, Set<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
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

    static DFA nfaToDfa(NFA nfa) {
        return new DFA();
    }

}
