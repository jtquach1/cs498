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
    void regexToNFAIntegers() {
        NFA expected = makeNFA(76);
        Utility.addSymbols(expected, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        Utility.addStates(expected,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                75, 76, 77);
        Utility.addFinalStates(expected, 77);
        Utility.addMoves(expected,
                Utility.makeMove(0, '0', 1),
                Utility.makeMove(1, FSA.EPSILON, 77),
                Utility.makeMove(2, '1', 3),
                Utility.makeMove(3, FSA.EPSILON, 35),
                Utility.makeMove(4, '2', 5),
                Utility.makeMove(5, FSA.EPSILON, 33),
                Utility.makeMove(6, '3', 7),
                Utility.makeMove(7, FSA.EPSILON, 31),
                Utility.makeMove(8, '4', 9),
                Utility.makeMove(9, FSA.EPSILON, 29),
                Utility.makeMove(10, '5', 11),
                Utility.makeMove(11, FSA.EPSILON, 27),
                Utility.makeMove(12, '6', 13),
                Utility.makeMove(13, FSA.EPSILON, 25),
                Utility.makeMove(14, '7', 15),
                Utility.makeMove(15, FSA.EPSILON, 23),
                Utility.makeMove(16, '8', 17),
                Utility.makeMove(17, FSA.EPSILON, 21),
                Utility.makeMove(18, '9', 19),
                Utility.makeMove(19, FSA.EPSILON, 21),
                Utility.makeMove(20, FSA.EPSILON, 16),
                Utility.makeMove(20, FSA.EPSILON, 18),
                Utility.makeMove(21, FSA.EPSILON, 23),
                Utility.makeMove(22, FSA.EPSILON, 14),
                Utility.makeMove(22, FSA.EPSILON, 20),
                Utility.makeMove(23, FSA.EPSILON, 25),
                Utility.makeMove(24, FSA.EPSILON, 12),
                Utility.makeMove(24, FSA.EPSILON, 22),
                Utility.makeMove(25, FSA.EPSILON, 27),
                Utility.makeMove(26, FSA.EPSILON, 10),
                Utility.makeMove(26, FSA.EPSILON, 24),
                Utility.makeMove(27, FSA.EPSILON, 29),
                Utility.makeMove(28, FSA.EPSILON, 8),
                Utility.makeMove(28, FSA.EPSILON, 26),
                Utility.makeMove(29, FSA.EPSILON, 31),
                Utility.makeMove(30, FSA.EPSILON, 6),
                Utility.makeMove(30, FSA.EPSILON, 28),
                Utility.makeMove(31, FSA.EPSILON, 33),
                Utility.makeMove(32, FSA.EPSILON, 4),
                Utility.makeMove(32, FSA.EPSILON, 30),
                Utility.makeMove(33, FSA.EPSILON, 35),
                Utility.makeMove(34, FSA.EPSILON, 2),
                Utility.makeMove(34, FSA.EPSILON, 32),
                Utility.makeMove(35, FSA.EPSILON, 74),
                Utility.makeMove(36, '0', 37),
                Utility.makeMove(37, FSA.EPSILON, 73),
                Utility.makeMove(38, '1', 39),
                Utility.makeMove(39, FSA.EPSILON, 71),
                Utility.makeMove(40, '2', 41),
                Utility.makeMove(41, FSA.EPSILON, 69),
                Utility.makeMove(42, '3', 43),
                Utility.makeMove(43, FSA.EPSILON, 67),
                Utility.makeMove(44, '4', 45),
                Utility.makeMove(45, FSA.EPSILON, 65),
                Utility.makeMove(46, '5', 47),
                Utility.makeMove(47, FSA.EPSILON, 63),
                Utility.makeMove(48, '6', 49),
                Utility.makeMove(49, FSA.EPSILON, 61),
                Utility.makeMove(50, '7', 51),
                Utility.makeMove(51, FSA.EPSILON, 59),
                Utility.makeMove(52, '8', 53),
                Utility.makeMove(53, FSA.EPSILON, 57),
                Utility.makeMove(54, '9', 55),
                Utility.makeMove(55, FSA.EPSILON, 57),
                Utility.makeMove(56, FSA.EPSILON, 52),
                Utility.makeMove(56, FSA.EPSILON, 54),
                Utility.makeMove(57, FSA.EPSILON, 59),
                Utility.makeMove(58, FSA.EPSILON, 50),
                Utility.makeMove(58, FSA.EPSILON, 56),
                Utility.makeMove(59, FSA.EPSILON, 61),
                Utility.makeMove(60, FSA.EPSILON, 48),
                Utility.makeMove(60, FSA.EPSILON, 58),
                Utility.makeMove(61, FSA.EPSILON, 63),
                Utility.makeMove(62, FSA.EPSILON, 46),
                Utility.makeMove(62, FSA.EPSILON, 60),
                Utility.makeMove(63, FSA.EPSILON, 65),
                Utility.makeMove(64, FSA.EPSILON, 44),
                Utility.makeMove(64, FSA.EPSILON, 62),
                Utility.makeMove(65, FSA.EPSILON, 67),
                Utility.makeMove(66, FSA.EPSILON, 42),
                Utility.makeMove(66, FSA.EPSILON, 64),
                Utility.makeMove(67, FSA.EPSILON, 69),
                Utility.makeMove(68, FSA.EPSILON, 40),
                Utility.makeMove(68, FSA.EPSILON, 66),
                Utility.makeMove(69, FSA.EPSILON, 71),
                Utility.makeMove(70, FSA.EPSILON, 38),
                Utility.makeMove(70, FSA.EPSILON, 68),
                Utility.makeMove(71, FSA.EPSILON, 73),
                Utility.makeMove(72, FSA.EPSILON, 36),
                Utility.makeMove(72, FSA.EPSILON, 70),
                Utility.makeMove(73, FSA.EPSILON, 72),
                Utility.makeMove(73, FSA.EPSILON, 75),
                Utility.makeMove(74, FSA.EPSILON, 72),
                Utility.makeMove(74, FSA.EPSILON, 75),
                Utility.makeMove(75, FSA.EPSILON, 77),
                Utility.makeMove(76, FSA.EPSILON, 0),
                Utility.makeMove(76, FSA.EPSILON, 34));
        NFA actual = NFA.regexToNFA("0|((1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*)");
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