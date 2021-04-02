import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DFATest {

    private static void addSymbols(DFA dfa, Character... symbols) {
        for (Character symbol : symbols) {
            dfa.addSymbol(symbol);
        }
    }

    private static void addStates(DFA dfa, Integer... stateIds) {
        for (Integer id : stateIds) {
            dfa.addState(new State(id));
        }
    }

    private static void addStates(Set<State> states, Integer... stateIds) {
        for (Integer id : stateIds) {
            states.add(new State(id));
        }
    }

    private static void addFinalStates(DFA dfa, Integer... stateIds) {
        for (Integer id : stateIds) {
            dfa.addFinalState(new State(id));
        }
    }

    private static void addMoves(DFA dfa, Move... moves) {
        for (Move move : moves) {
            dfa.addMove(move);
        }
    }

    private static void addMoves(Set<Move> moveSet, Move... moves) {
        moveSet.addAll(Arrays.asList(moves));
    }

    private static Move makeMove(Integer fromId, Character consumed, Integer toId) {
        return new Move(new State(fromId), consumed, new State(toId));
    }

    private static DFA makeDFA(Integer stateId) {
        return new DFA(
                new Alphabet(),
                new TreeSet<>(),
                new State(stateId),
                new TreeSet<>(), new TreeSet<>()
        );
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deepClone() {
    }

    @Test
    void epsilonClosureForOneState() {
        Set<Move> moves = new TreeSet<>();
        addMoves(moves,
                makeMove(0, 'a', 1),
                makeMove(1, DFA.EPSILON, 5),
                makeMove(2, 'b', 3),
                makeMove(3, DFA.EPSILON, 5),
                makeMove(4, DFA.EPSILON, 0),
                makeMove(4, DFA.EPSILON, 2),
                makeMove(5, DFA.EPSILON, 8),
                makeMove(6, 'a', 7),
                makeMove(7, DFA.EPSILON, 6),
                makeMove(7, DFA.EPSILON, 9),
                makeMove(8, DFA.EPSILON, 6),
                makeMove(8, DFA.EPSILON, 9),
                makeMove(9, DFA.EPSILON, 10),
                makeMove(10, 'b', 11));

        Set<State> expected = new TreeSet<>();
        addStates(expected, 0, 4, 2);
        Set<State> actual = DFA.epsilonClosure(new State(4), moves);
        assertEquals(expected, actual);
    }

    @Test
    void testEpsilonClosureForSetOfStates() {
        Set<Move> moves = new TreeSet<>();
        addMoves(moves,
                makeMove(0, 'a', 1),
                makeMove(1, DFA.EPSILON, 5),
                makeMove(2, 'b', 3),
                makeMove(3, DFA.EPSILON, 5),
                makeMove(4, DFA.EPSILON, 0),
                makeMove(4, DFA.EPSILON, 2),
                makeMove(5, DFA.EPSILON, 8),
                makeMove(6, 'a', 7),
                makeMove(7, DFA.EPSILON, 6),
                makeMove(7, DFA.EPSILON, 9),
                makeMove(8, DFA.EPSILON, 6),
                makeMove(8, DFA.EPSILON, 9),
                makeMove(9, DFA.EPSILON, 10),
                makeMove(10, 'b', 11));

        Set<State> startClosure = new TreeSet<>();
        addStates(startClosure, 0, 2, 4);
        startClosure = DFA.getReachableStates(startClosure, moves, 'a');

        Set<State> expected = new TreeSet<>();
        addStates(expected, 1, 5, 6, 8, 9, 10);
        Set<State> actual = DFA.epsilonClosure(startClosure, moves);
        assertEquals(expected, actual);
    }
}