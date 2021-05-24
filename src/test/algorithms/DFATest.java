package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static algorithms.Utility.makeDFA;
import static algorithms.Utility.makeNFA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DFATest {

    @BeforeEach
    void setUp() {
        State.setIdCounter(0);
    }

    @Test
    void epsilonClosureForOneState() {
        int id = 0;
        Set<Move> moves = new TreeSet<>();
        Utility.addMoves(moves,
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

        Set<State> states = new TreeSet<>();
        Utility.addStates(states, 1, 5, 6, 8, 9, 10);
        DFAState expected = new DFAState(id);
        expected.addAll(states);

        DFAState actual = DFA.epsilonClosure(new State(1), moves, id);
        assertEquals(expected, actual);
    }

    @Test
    void testEpsilonClosureForSetOfStates() {
        int id = 0;
        Set<Move> moves = new TreeSet<>();
        Utility.addMoves(moves,
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

        Set<State> states = new TreeSet<>();
        Utility.addStates(states, 0, 2, 4, 6, 7, 9, 10);
        DFAState expected = new DFAState(id);
        expected.addAll(states);

        states.clear();
        Utility.addStates(states, 4, 7);
        DFAState actual = DFA.epsilonClosure(states, moves, id);

        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAConcatenate() {
        NFA nfa = makeNFA(0);
        Utility.addSymbols(nfa, 'a', 'b');
        Utility.addStates(nfa, 0, 1, 2, 3);
        Utility.addFinalStates(nfa, 3);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, FSA.EPSILON, 2),
                Utility.makeMove(2, 'b', 3)
        );

        DFA expected = makeDFA(0, 3);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 2);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 3),
                Utility.makeMove(1, 'b', 2),
                Utility.makeMove(1, 'a', 3),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 3)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAKleeneStar() {
        NFA nfa = makeNFA(2);
        Utility.addSymbols(nfa, 'a');
        Utility.addStates(nfa, 0, 1, 2, 3);
        Utility.addFinalStates(nfa, 3);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, FSA.EPSILON, 0),
                Utility.makeMove(1, FSA.EPSILON, 3),
                Utility.makeMove(2, FSA.EPSILON, 0),
                Utility.makeMove(2, FSA.EPSILON, 3)
        );

        DFA expected = makeDFA(0);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 0, 1);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, 'a', 1)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAAlternate() {
        NFA nfa = makeNFA(0);
        Utility.addSymbols(nfa, 'a', 'b');
        Utility.addStates(nfa, 0, 1, 2, 3, 4);
        Utility.addFinalStates(nfa, 2, 4);
        Utility.addMoves(nfa,
                Utility.makeMove(0, FSA.EPSILON, 1),
                Utility.makeMove(1, 'a', 2),
                Utility.makeMove(0, FSA.EPSILON, 3),
                Utility.makeMove(3, 'b', 4)
        );

        DFA expected = makeDFA(0, 3);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 1, 2);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 2),
                Utility.makeMove(1, 'a', 3),
                Utility.makeMove(1, 'b', 3),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 3)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFANoEpsilon() {
        NFA nfa = makeNFA(0);
        Utility.addSymbols(nfa, 'a');
        Utility.addStates(nfa, 0, 1);
        Utility.addFinalStates(nfa, 1);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 0),
                Utility.makeMove(0, 'a', 1)
        );

        DFA expected = makeDFA(0);
        Utility.addSymbols(expected, 'a');
        Utility.addStates(expected, 0, 1);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, 'a', 1)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAIdentifiers() {
        NFA nfa = makeNFA(12);
        Utility.addSymbols(nfa, '$', 'S', '_', 'd', 's');
        Utility.addStates(nfa,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
        );
        Utility.addFinalStates(nfa, 25);
        Utility.addMoves(nfa,
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

        DFA expected = makeDFA(0, 8);
        Utility.addSymbols(expected, '$', 'S', '_', 'd', 's');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        Utility.addFinalStates(expected, 1, 2, 3, 4, 5, 6, 7);
        Utility.addMoves(expected,
                Utility.makeMove(1, 'd', 6),
                Utility.makeMove(2, 'd', 6),
                Utility.makeMove(0, 'd', 8),
                Utility.makeMove(4, 'd', 6),
                Utility.makeMove(1, '$', 8),
                Utility.makeMove(3, 'd', 6),
                Utility.makeMove(2, '$', 8),
                Utility.makeMove(6, 'd', 6),
                Utility.makeMove(3, '$', 8),
                Utility.makeMove(5, 'd', 6),
                Utility.makeMove(4, '$', 8),
                Utility.makeMove(6, '$', 8),
                Utility.makeMove(7, 'd', 6),
                Utility.makeMove(5, '$', 8),
                Utility.makeMove(8, '$', 8),
                Utility.makeMove(8, 'd', 8),
                Utility.makeMove(7, '$', 8),
                Utility.makeMove(0, 's', 4),
                Utility.makeMove(1, 's', 7),
                Utility.makeMove(2, 's', 7),
                Utility.makeMove(4, 's', 7),
                Utility.makeMove(3, 's', 7),
                Utility.makeMove(6, 's', 7),
                Utility.makeMove(5, 's', 7),
                Utility.makeMove(7, 's', 7),
                Utility.makeMove(8, 's', 8),
                Utility.makeMove(0, '_', 3),
                Utility.makeMove(1, '_', 8),
                Utility.makeMove(2, '_', 8),
                Utility.makeMove(3, '_', 8),
                Utility.makeMove(5, '_', 8),
                Utility.makeMove(4, '_', 8),
                Utility.makeMove(0, 'S', 2),
                Utility.makeMove(7, '_', 8),
                Utility.makeMove(6, '_', 8),
                Utility.makeMove(8, '_', 8),
                Utility.makeMove(1, 'S', 5),
                Utility.makeMove(2, 'S', 5),
                Utility.makeMove(3, 'S', 5),
                Utility.makeMove(4, 'S', 5),
                Utility.makeMove(6, 'S', 5),
                Utility.makeMove(5, 'S', 5),
                Utility.makeMove(7, 'S', 5),
                Utility.makeMove(0, '$', 1),
                Utility.makeMove(8, 'S', 8)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAIntegers() {
        NFA nfa = makeNFA(76);
        Utility.addSymbols(nfa, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        Utility.addStates(nfa,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                75, 76, 77);
        Utility.addFinalStates(nfa, 77);
        Utility.addMoves(nfa,
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

        DFA expected = makeDFA(0, 21);
        Utility.addSymbols(expected,
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        );
        Utility.addStates(expected,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21
        );
        Utility.addFinalStates(expected,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        );
        Utility.addMoves(expected,
                Utility.makeMove(11, '0', 11),
                Utility.makeMove(13, '4', 15),
                Utility.makeMove(12, '2', 13),
                Utility.makeMove(15, '8', 19),
                Utility.makeMove(14, '6', 17),
                Utility.makeMove(19, '0', 11),
                Utility.makeMove(20, '2', 13),
                Utility.makeMove(21, '4', 21),
                Utility.makeMove(3, '1', 12),
                Utility.makeMove(4, '3', 14),
                Utility.makeMove(5, '5', 16),
                Utility.makeMove(7, '9', 20),
                Utility.makeMove(6, '7', 18),
                Utility.makeMove(11, '1', 12),
                Utility.makeMove(13, '5', 16),
                Utility.makeMove(12, '3', 14),
                Utility.makeMove(15, '9', 20),
                Utility.makeMove(14, '7', 18),
                Utility.makeMove(19, '1', 12),
                Utility.makeMove(20, '3', 14),
                Utility.makeMove(21, '5', 21),
                Utility.makeMove(2, '0', 11),
                Utility.makeMove(3, '2', 13),
                Utility.makeMove(4, '4', 15),
                Utility.makeMove(5, '6', 17),
                Utility.makeMove(6, '8', 19),
                Utility.makeMove(11, '2', 13),
                Utility.makeMove(10, '0', 11),
                Utility.makeMove(13, '6', 17),
                Utility.makeMove(12, '4', 15),
                Utility.makeMove(14, '8', 19),
                Utility.makeMove(19, '2', 13),
                Utility.makeMove(18, '0', 11),
                Utility.makeMove(20, '4', 15),
                Utility.makeMove(21, '6', 21),
                Utility.makeMove(2, '1', 12),
                Utility.makeMove(3, '3', 14),
                Utility.makeMove(4, '5', 16),
                Utility.makeMove(5, '7', 18),
                Utility.makeMove(6, '9', 20),
                Utility.makeMove(11, '3', 14),
                Utility.makeMove(10, '1', 12),
                Utility.makeMove(13, '7', 18),
                Utility.makeMove(12, '5', 16),
                Utility.makeMove(14, '9', 20),
                Utility.makeMove(19, '3', 14),
                Utility.makeMove(18, '1', 12),
                Utility.makeMove(20, '5', 16),
                Utility.makeMove(21, '7', 21),
                Utility.makeMove(2, '2', 13),
                Utility.makeMove(3, '4', 15),
                Utility.makeMove(4, '6', 17),
                Utility.makeMove(5, '8', 19),
                Utility.makeMove(9, '0', 11),
                Utility.makeMove(11, '4', 15),
                Utility.makeMove(10, '2', 13),
                Utility.makeMove(1, '0', 21),
                Utility.makeMove(13, '8', 19),
                Utility.makeMove(12, '6', 17),
                Utility.makeMove(17, '0', 11),
                Utility.makeMove(19, '4', 15),
                Utility.makeMove(18, '2', 13),
                Utility.makeMove(20, '6', 17),
                Utility.makeMove(21, '8', 21),
                Utility.makeMove(2, '3', 14),
                Utility.makeMove(3, '5', 16),
                Utility.makeMove(4, '7', 18),
                Utility.makeMove(5, '9', 20),
                Utility.makeMove(9, '1', 12),
                Utility.makeMove(11, '5', 16),
                Utility.makeMove(1, '1', 21),
                Utility.makeMove(10, '3', 14),
                Utility.makeMove(13, '9', 20),
                Utility.makeMove(12, '7', 18),
                Utility.makeMove(17, '1', 12),
                Utility.makeMove(19, '5', 16),
                Utility.makeMove(18, '3', 14),
                Utility.makeMove(20, '7', 18),
                Utility.makeMove(0, '0', 1),
                Utility.makeMove(21, '9', 21),
                Utility.makeMove(2, '4', 15),
                Utility.makeMove(3, '6', 17),
                Utility.makeMove(4, '8', 19),
                Utility.makeMove(9, '2', 13),
                Utility.makeMove(8, '0', 11),
                Utility.makeMove(1, '2', 21),
                Utility.makeMove(11, '6', 17),
                Utility.makeMove(10, '4', 15),
                Utility.makeMove(12, '8', 19),
                Utility.makeMove(17, '2', 13),
                Utility.makeMove(16, '0', 11),
                Utility.makeMove(19, '6', 17),
                Utility.makeMove(18, '4', 15),
                Utility.makeMove(20, '8', 19),
                Utility.makeMove(0, '1', 2),
                Utility.makeMove(2, '5', 16),
                Utility.makeMove(3, '7', 18),
                Utility.makeMove(4, '9', 20),
                Utility.makeMove(9, '3', 14),
                Utility.makeMove(1, '3', 21),
                Utility.makeMove(8, '1', 12),
                Utility.makeMove(11, '7', 18),
                Utility.makeMove(10, '5', 16),
                Utility.makeMove(12, '9', 20),
                Utility.makeMove(17, '3', 14),
                Utility.makeMove(16, '1', 12),
                Utility.makeMove(19, '7', 18),
                Utility.makeMove(18, '5', 16),
                Utility.makeMove(20, '9', 20),
                Utility.makeMove(0, '2', 3),
                Utility.makeMove(2, '6', 17),
                Utility.makeMove(3, '8', 19),
                Utility.makeMove(7, '0', 11),
                Utility.makeMove(1, '4', 21),
                Utility.makeMove(9, '4', 15),
                Utility.makeMove(8, '2', 13),
                Utility.makeMove(11, '8', 19),
                Utility.makeMove(10, '6', 17),
                Utility.makeMove(15, '0', 11),
                Utility.makeMove(17, '4', 15),
                Utility.makeMove(16, '2', 13),
                Utility.makeMove(19, '8', 19),
                Utility.makeMove(18, '6', 17),
                Utility.makeMove(0, '3', 4),
                Utility.makeMove(2, '7', 18),
                Utility.makeMove(3, '9', 20),
                Utility.makeMove(7, '1', 12),
                Utility.makeMove(1, '5', 21),
                Utility.makeMove(9, '5', 16),
                Utility.makeMove(8, '3', 14),
                Utility.makeMove(11, '9', 20),
                Utility.makeMove(10, '7', 18),
                Utility.makeMove(15, '1', 12),
                Utility.makeMove(17, '5', 16),
                Utility.makeMove(16, '3', 14),
                Utility.makeMove(19, '9', 20),
                Utility.makeMove(18, '7', 18),
                Utility.makeMove(0, '4', 5),
                Utility.makeMove(2, '8', 19),
                Utility.makeMove(1, '6', 21),
                Utility.makeMove(7, '2', 13),
                Utility.makeMove(6, '0', 11),
                Utility.makeMove(9, '6', 17),
                Utility.makeMove(8, '4', 15),
                Utility.makeMove(10, '8', 19),
                Utility.makeMove(15, '2', 13),
                Utility.makeMove(14, '0', 11),
                Utility.makeMove(17, '6', 17),
                Utility.makeMove(16, '4', 15),
                Utility.makeMove(18, '8', 19),
                Utility.makeMove(0, '5', 6),
                Utility.makeMove(2, '9', 20),
                Utility.makeMove(1, '7', 21),
                Utility.makeMove(7, '3', 14),
                Utility.makeMove(6, '1', 12),
                Utility.makeMove(9, '7', 18),
                Utility.makeMove(8, '5', 16),
                Utility.makeMove(10, '9', 20),
                Utility.makeMove(15, '3', 14),
                Utility.makeMove(14, '1', 12),
                Utility.makeMove(17, '7', 18),
                Utility.makeMove(16, '5', 16),
                Utility.makeMove(18, '9', 20),
                Utility.makeMove(0, '6', 7),
                Utility.makeMove(1, '8', 21),
                Utility.makeMove(5, '0', 11),
                Utility.makeMove(7, '4', 15),
                Utility.makeMove(6, '2', 13),
                Utility.makeMove(9, '8', 19),
                Utility.makeMove(8, '6', 17),
                Utility.makeMove(13, '0', 11),
                Utility.makeMove(15, '4', 15),
                Utility.makeMove(14, '2', 13),
                Utility.makeMove(17, '8', 19),
                Utility.makeMove(16, '6', 17),
                Utility.makeMove(0, '7', 8),
                Utility.makeMove(21, '0', 21),
                Utility.makeMove(1, '9', 21),
                Utility.makeMove(5, '1', 12),
                Utility.makeMove(7, '5', 16),
                Utility.makeMove(6, '3', 14),
                Utility.makeMove(9, '9', 20),
                Utility.makeMove(8, '7', 18),
                Utility.makeMove(13, '1', 12),
                Utility.makeMove(15, '5', 16),
                Utility.makeMove(14, '3', 14),
                Utility.makeMove(17, '9', 20),
                Utility.makeMove(16, '7', 18),
                Utility.makeMove(0, '8', 9),
                Utility.makeMove(21, '1', 21),
                Utility.makeMove(4, '0', 11),
                Utility.makeMove(5, '2', 13),
                Utility.makeMove(7, '6', 17),
                Utility.makeMove(6, '4', 15),
                Utility.makeMove(8, '8', 19),
                Utility.makeMove(13, '2', 13),
                Utility.makeMove(12, '0', 11),
                Utility.makeMove(15, '6', 17),
                Utility.makeMove(14, '4', 15),
                Utility.makeMove(16, '8', 19),
                Utility.makeMove(20, '0', 11),
                Utility.makeMove(0, '9', 10),
                Utility.makeMove(21, '2', 21),
                Utility.makeMove(4, '1', 12),
                Utility.makeMove(5, '3', 14),
                Utility.makeMove(7, '7', 18),
                Utility.makeMove(6, '5', 16),
                Utility.makeMove(8, '9', 20),
                Utility.makeMove(13, '3', 14),
                Utility.makeMove(12, '1', 12),
                Utility.makeMove(15, '7', 18),
                Utility.makeMove(14, '5', 16),
                Utility.makeMove(16, '9', 20),
                Utility.makeMove(20, '1', 12),
                Utility.makeMove(21, '3', 21),
                Utility.makeMove(3, '0', 11),
                Utility.makeMove(4, '2', 13),
                Utility.makeMove(5, '4', 15),
                Utility.makeMove(7, '8', 19),
                Utility.makeMove(6, '6', 17)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }
}