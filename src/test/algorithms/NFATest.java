package algorithms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NFATest {

    static NFA makeNFA(Integer stateId) {
        return new NFA(
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
    void regexToNFA() {
        NFA expected = makeNFA(4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Utility.addFinalStates(expected, 11);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, NFA.EPSILON, 5),
                Utility.makeMove(4, NFA.EPSILON, 0),
                Utility.makeMove(4, NFA.EPSILON, 2),
                Utility.makeMove(5, NFA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, NFA.EPSILON, 6),
                Utility.makeMove(7, NFA.EPSILON, 9),
                Utility.makeMove(8, NFA.EPSILON, 6),
                Utility.makeMove(8, NFA.EPSILON, 9),
                Utility.makeMove(9, NFA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));
        NFA actual = NFA.regexToNFA("(a|b)a*b");
        assertEquals(expected, actual);

        State.setIdCounter(0);
        expected = makeNFA(0);
        Utility.addSymbols(expected, NFA.EPSILON);
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected, Utility.makeMove(0, NFA.EPSILON, 1));
        actual = NFA.regexToNFA("");
        assertEquals(expected, actual);
    }

    @Test
    void makeSingle() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected, Utility.makeMove(0, 'a', 1));

        NFA actual = NFA.makeSingle('a');
        assertEquals(expected, actual);
    }

    @Test
    void concatenate() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 3);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 2),
                Utility.makeMove(2, 'b', 3));

        NFA first = makeNFA(0);
        Utility.addSymbols(first, 'a');
        Utility.addStates(first, 0, 1);
        Utility.addFinalStates(first, 1);
        Utility.addMoves(first, Utility.makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        Utility.addSymbols(second, 'b');
        Utility.addStates(second, 2, 3);
        Utility.addFinalStates(second, 3);
        Utility.addMoves(second, Utility.makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.concatenate(first, second);
        assertEquals(expected, actual);
    }

    @Test
    void kleeneStar() {
        NFA expected = makeNFA(2);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 3);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 0),
                Utility.makeMove(1, NFA.EPSILON, 3),
                Utility.makeMove(2, NFA.EPSILON, 0),
                Utility.makeMove(2, NFA.EPSILON, 3));

        NFA actual = makeNFA(0);
        Utility.addSymbols(actual, 'a');
        Utility.addStates(actual, 0, 1);
        Utility.addFinalStates(actual, 1);
        Utility.addMoves(actual, Utility.makeMove(0, 'a', 1));
        State.setIdCounter(2);

        actual = NFA.kleeneStar(actual);
        assertEquals(expected, actual);
    }

    @Test
    void alternate() {
        NFA expected = makeNFA(4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5);
        Utility.addFinalStates(expected, 5);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, NFA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, NFA.EPSILON, 5),
                Utility.makeMove(4, NFA.EPSILON, 0),
                Utility.makeMove(4, NFA.EPSILON, 2));

        NFA first = makeNFA(0);
        Utility.addSymbols(first, 'a');
        Utility.addStates(first, 0, 1);
        Utility.addFinalStates(first, 1);
        Utility.addMoves(first, Utility.makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        Utility.addSymbols(second, 'b');
        Utility.addStates(second, 2, 3);
        Utility.addFinalStates(second, 3);
        Utility.addMoves(second, Utility.makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.alternate(first, second);
        assertEquals(expected, actual);
    }
}