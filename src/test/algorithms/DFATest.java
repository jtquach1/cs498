package algorithms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DFATest {

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
        State.setIdCounter(0);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void epsilonClosureForOneState() {
        Set<Move> moves = new TreeSet<>();
        Utility.addMoves(moves,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, DFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, DFA.EPSILON, 5),
                Utility.makeMove(4, DFA.EPSILON, 0),
                Utility.makeMove(4, DFA.EPSILON, 2),
                Utility.makeMove(5, DFA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, DFA.EPSILON, 6),
                Utility.makeMove(7, DFA.EPSILON, 9),
                Utility.makeMove(8, DFA.EPSILON, 6),
                Utility.makeMove(8, DFA.EPSILON, 9),
                Utility.makeMove(9, DFA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));

        Set<State> expected = new TreeSet<>();
        Utility.addStates(expected, 1, 5, 6, 8, 9, 10);
        Set<State> actual = DFA.epsilonClosure(new State(1), moves);
        assertEquals(expected, actual);
    }

    @Test
    void testEpsilonClosureForSetOfStates() {
        Set<Move> moves = new TreeSet<>();
        Utility.addMoves(moves,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, DFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, DFA.EPSILON, 5),
                Utility.makeMove(4, DFA.EPSILON, 0),
                Utility.makeMove(4, DFA.EPSILON, 2),
                Utility.makeMove(5, DFA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, DFA.EPSILON, 6),
                Utility.makeMove(7, DFA.EPSILON, 9),
                Utility.makeMove(8, DFA.EPSILON, 6),
                Utility.makeMove(8, DFA.EPSILON, 9),
                Utility.makeMove(9, DFA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));

        Set<State> states = new TreeSet<>();
        Utility.addStates(states, 4, 7);

        Set<State> expected = new TreeSet<>();
        Utility.addStates(expected, 0, 2, 4, 6, 7, 9, 10);
        Set<State> actual = DFA.epsilonClosure(states, moves);
        assertEquals(expected, actual);
    }

    @Test
    void getReachableStates() {
        Set<Move> moves = new TreeSet<>();
        Utility.addMoves(moves,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, DFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, DFA.EPSILON, 5),
                Utility.makeMove(4, DFA.EPSILON, 0),
                Utility.makeMove(4, DFA.EPSILON, 2),
                Utility.makeMove(5, DFA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, DFA.EPSILON, 6),
                Utility.makeMove(7, DFA.EPSILON, 9),
                Utility.makeMove(8, DFA.EPSILON, 6),
                Utility.makeMove(8, DFA.EPSILON, 9),
                Utility.makeMove(9, DFA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));
    }

    @Test
    void NFAtoDFA() {
//        NFA nfa = NFATest.makeNFA(4);
//        Utility.addSymbols(nfa, 'a', 'b');
//        Utility.addStates(nfa, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
//        Utility.addFinalStates(nfa, 11);
//        Utility.addMoves(nfa,
//                Utility.makeMove(0, 'a', 1),
//                Utility.makeMove(1, NFA.EPSILON, 5),
//                Utility.makeMove(2, 'b', 3),
//                Utility.makeMove(3, NFA.EPSILON, 5),
//                Utility.makeMove(4, NFA.EPSILON, 0),
//                Utility.makeMove(4, NFA.EPSILON, 2),
//                Utility.makeMove(5, NFA.EPSILON, 8),
//                Utility.makeMove(6, 'a', 7),
//                Utility.makeMove(7, NFA.EPSILON, 6),
//                Utility.makeMove(7, NFA.EPSILON, 9),
//                Utility.makeMove(8, NFA.EPSILON, 6),
//                Utility.makeMove(8, NFA.EPSILON, 9),
//                Utility.makeMove(9, NFA.EPSILON, 10),
//                Utility.makeMove(10, 'b', 11));

        NFA nfa = NFATest.makeNFA(0);
        Utility.addSymbols(nfa, 'a', 'b');
        Utility.addStates(nfa, 0, 1);
        Utility.addFinalStates(nfa, 1);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 0),
                Utility.makeMove(0, 'b', 0),
                Utility.makeMove(0, 'b', 1)
        );

        DFA expected = makeDFA(0);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 0),
                Utility.makeMove(0, 'b', 1),
                Utility.makeMove(1, 'a', 0),
                Utility.makeMove(1, 'b', 1)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }
}