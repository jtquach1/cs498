package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static algorithms.Utility.makeNFA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NFATest {

    @BeforeEach
    void setUp() {
        State.setIdCounter(0);
    }

    @Test
    void regexToNFABasic() {
        NFA expected = makeNFA(4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Utility.addFinalStates(expected, 11);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, FSA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, FSA.EPSILON, 5),
                Utility.makeMove(4, FSA.EPSILON, 0),
                Utility.makeMove(4, FSA.EPSILON, 2),
                Utility.makeMove(5, FSA.EPSILON, 8),
                Utility.makeMove(6, 'a', 7),
                Utility.makeMove(7, FSA.EPSILON, 6),
                Utility.makeMove(7, FSA.EPSILON, 9),
                Utility.makeMove(8, FSA.EPSILON, 6),
                Utility.makeMove(8, FSA.EPSILON, 9),
                Utility.makeMove(9, FSA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11));
        NFA actual = NFA.regexToNFA("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAEmpty() {
        NFA expected = makeNFA(0);
        Utility.addSymbols(expected, FSA.EPSILON);
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected, Utility.makeMove(0, FSA.EPSILON, 1));
        NFA actual = NFA.regexToNFA("");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAIdentifiers() {
        NFA expected = makeNFA(12);
        Utility.addSymbols(expected, '$', 'S', '_', 'd', 's');
        Utility.addStates(expected,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
        );
        Utility.addFinalStates(expected, 25);
        Utility.addMoves(expected,
                Utility.makeMove(0, '$', 1),
                Utility.makeMove(1, FSA.EPSILON, 13),
                Utility.makeMove(2, '_', 3),
                Utility.makeMove(3, FSA.EPSILON, 11),
                Utility.makeMove(4, 's', 5),
                Utility.makeMove(5, FSA.EPSILON, 9),
                Utility.makeMove(6, 'S', 7),
                Utility.makeMove(7, FSA.EPSILON, 9),
                Utility.makeMove(8, FSA.EPSILON, 4),
                Utility.makeMove(8, FSA.EPSILON, 6),
                Utility.makeMove(9, FSA.EPSILON, 11),
                Utility.makeMove(10, FSA.EPSILON, 2),
                Utility.makeMove(10, FSA.EPSILON, 8),
                Utility.makeMove(11, FSA.EPSILON, 13),
                Utility.makeMove(12, FSA.EPSILON, 0),
                Utility.makeMove(12, FSA.EPSILON, 10),
                Utility.makeMove(13, FSA.EPSILON, 24),
                Utility.makeMove(14, 's', 15),
                Utility.makeMove(15, FSA.EPSILON, 23),
                Utility.makeMove(16, 'S', 17),
                Utility.makeMove(17, FSA.EPSILON, 21),
                Utility.makeMove(18, 'd', 19),
                Utility.makeMove(19, FSA.EPSILON, 21),
                Utility.makeMove(20, FSA.EPSILON, 16),
                Utility.makeMove(20, FSA.EPSILON, 18),
                Utility.makeMove(21, FSA.EPSILON, 23),
                Utility.makeMove(22, FSA.EPSILON, 14),
                Utility.makeMove(22, FSA.EPSILON, 20),
                Utility.makeMove(23, FSA.EPSILON, 22),
                Utility.makeMove(23, FSA.EPSILON, 25),
                Utility.makeMove(24, FSA.EPSILON, 22),
                Utility.makeMove(24, FSA.EPSILON, 25)
        );

        NFA actual = NFA.regexToNFA("($|_|s|S)" +
                "(s|S|d)*");

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
                Utility.makeMove(1, FSA.EPSILON, 2),
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
                Utility.makeMove(1, FSA.EPSILON, 0),
                Utility.makeMove(1, FSA.EPSILON, 3),
                Utility.makeMove(2, FSA.EPSILON, 0),
                Utility.makeMove(2, FSA.EPSILON, 3));

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
                Utility.makeMove(1, FSA.EPSILON, 5),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, FSA.EPSILON, 5),
                Utility.makeMove(4, FSA.EPSILON, 0),
                Utility.makeMove(4, FSA.EPSILON, 2));

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