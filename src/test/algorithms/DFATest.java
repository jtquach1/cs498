package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static algorithms.FSA.EPSILON;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DFATest {

    @BeforeEach
    void setUp() {
        State.setIdCounter(0);
    }

    @Test
    void epsilonClosureForOneState() {
        int id = 0;
        Moves moves = makeMoves(
                makeMove(0, 'a', 1),
                makeMove(1, EPSILON, 5),
                makeMove(2, 'b', 3),
                makeMove(3, EPSILON, 5),
                makeMove(4, EPSILON, 0),
                makeMove(4, EPSILON, 2),
                makeMove(5, EPSILON, 8),
                makeMove(6, 'a', 7),
                makeMove(7, EPSILON, 6),
                makeMove(7, EPSILON, 9),
                makeMove(8, EPSILON, 6),
                makeMove(8, EPSILON, 9),
                makeMove(9, EPSILON, 10),
                makeMove(10, 'b', 11)
        );

        States states = makeStates(1, 5, 6, 8, 9, 10);
        DFAState expected = new DFAState(id, states);

        DFAState actual = DFA.epsilonClosure(new State(1), moves, id);
        assertEquals(expected, actual);
    }

    @Test
    void epsilonClosureForSetOfStates() {
        DFAState expected = new DFAState(
                0,
                makeStates(0, 2, 4, 6, 7, 9, 10)
        );

        DFAState actual = DFA.epsilonClosure(
                makeStates(4, 7),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 5),
                        makeMove(2, 'b', 3),
                        makeMove(3, EPSILON, 5),
                        makeMove(4, EPSILON, 0),
                        makeMove(4, EPSILON, 2),
                        makeMove(5, EPSILON, 8),
                        makeMove(6, 'a', 7),
                        makeMove(7, EPSILON, 6),
                        makeMove(7, EPSILON, 9),
                        makeMove(8, EPSILON, 6),
                        makeMove(8, EPSILON, 9),
                        makeMove(9, EPSILON, 10),
                        makeMove(10, 'b', 11)
                ),
                0
        );

        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAConcatenate() {
        NFA nfa = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3),
                new State(0),
                makeStates(3),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 2),
                        makeMove(2, 'b', 3)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1, 2)),
                        makeState(2, makeStates(3)),
                        new State(3)
                ),
                makeState(0, makeStates(0)),
                makeStates(2),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 3),
                        makeMove(1, 'b', 2),
                        makeMove(1, 'a', 3),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 3),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 3)
                ),
                new State(3)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAKleeneStar() {
        NFA nfa = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1, 2, 3),
                new State(2),
                makeStates(3),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 0),
                        makeMove(1, EPSILON, 3),
                        makeMove(2, EPSILON, 0),
                        makeMove(2, EPSILON, 3)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a'),
                makeStates(
                        makeState(0, makeStates(0, 2, 3)),
                        makeState(1, makeStates(0, 1, 3))
                ),
                makeState(0, makeStates(0, 2, 3)),
                makeStates(0, 1),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, 'a', 1)
                )
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAAlternate() {
        NFA nfa = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4),
                new State(0),
                makeStates(2, 4),
                makeMoves(
                        makeMove(0, EPSILON, 1),
                        makeMove(1, 'a', 2),
                        makeMove(0, EPSILON, 3),
                        makeMove(3, 'b', 4)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0, 1, 3)),
                        makeState(1, makeStates(2)),
                        makeState(2, makeStates(4)),
                        new State(3)
                ),
                makeState(0, makeStates(0, 1, 3)),
                makeStates(1, 2),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 2),
                        makeMove(1, 'a', 3),
                        makeMove(1, 'b', 3),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 3),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 3)
                ),
                new State(3)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFANoEpsilon() {
        NFA nfa = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(
                        makeMove(0, 'a', 0),
                        makeMove(0, 'a', 1)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(0, 1))
                ),
                makeState(0, makeStates(0)),
                makeStates(1),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, 'a', 1)
                )
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFABasic() {
        NFA nfa = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                new State(4),
                makeStates(11),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 5),
                        makeMove(2, 'b', 3),
                        makeMove(3, EPSILON, 5),
                        makeMove(4, EPSILON, 0),
                        makeMove(4, EPSILON, 2),
                        makeMove(5, EPSILON, 8),
                        makeMove(6, 'a', 7),
                        makeMove(7, EPSILON, 6),
                        makeMove(7, EPSILON, 9),
                        makeMove(8, EPSILON, 6),
                        makeMove(8, EPSILON, 9),
                        makeMove(9, EPSILON, 10),
                        makeMove(10, 'b', 11)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0, 2, 4)),
                        makeState(1, makeStates(1, 5, 6, 8, 9, 10)),
                        makeState(2, makeStates(3, 5, 6, 8, 9, 10)),
                        makeState(3, makeStates(6, 7, 9, 10)),
                        makeState(4, makeStates(11)),
                        new State(5)
                ),
                makeState(0, makeStates(0, 2, 4)),
                makeStates(4),
                makeMoves(
                        makeMove(1, 'a', 3),
                        makeMove(1, 'b', 4),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 4),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 4),
                        makeMove(4, 'a', 5),
                        makeMove(5, 'b', 5),
                        makeMove(4, 'b', 5),
                        makeMove(5, 'a', 5),
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 2)
                ),
                new State(5)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFABasic2() {
        NFA nfa = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                new State(0),
                makeStates(11),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 8),
                        makeMove(2, 'a', 3),
                        makeMove(3, EPSILON, 7),
                        makeMove(4, 'b', 5),
                        makeMove(5, EPSILON, 7),
                        makeMove(6, EPSILON, 2),
                        makeMove(6, EPSILON, 4),
                        makeMove(7, EPSILON, 6),
                        makeMove(7, EPSILON, 9),
                        makeMove(8, EPSILON, 6),
                        makeMove(8, EPSILON, 9),
                        makeMove(9, EPSILON, 10),
                        makeMove(10, 'b', 11)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1, 2, 4, 6, 8, 9, 10)),
                        makeState(2, makeStates(2, 3, 4, 6, 7, 9, 10)),
                        makeState(3, makeStates(2, 4, 5, 6, 7, 9, 10, 11)),
                        new State(4)
                ),
                makeState(0, makeStates(0)),
                makeStates(3),
                makeMoves(
                        makeMove(2, 'a', 2),
                        makeMove(2, 'b', 3),
                        makeMove(3, 'b', 3),
                        makeMove(3, 'a', 2),
                        makeMove(4, 'b', 4),
                        makeMove(4, 'a', 4),
                        makeMove(0, 'a', 1),
                        makeMove(1, 'a', 2),
                        makeMove(1, 'b', 3),
                        makeMove(0, 'b', 4)
                ),
                new State(4)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFAIdentifiers() {
        NFA nfa = makeNFA(
                makeAlphabet('$', 'S', '_', 'd', 's'),
                makeStates(
                        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                        14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
                ),
                new State(12),
                makeStates(25),
                makeMoves(
                        makeMove(0, '$', 1),
                        makeMove(1, EPSILON, 13),
                        makeMove(2, '_', 3),
                        makeMove(3, EPSILON, 11),
                        makeMove(4, 's', 5),
                        makeMove(5, EPSILON, 9),
                        makeMove(6, 'S', 7),
                        makeMove(7, EPSILON, 9),
                        makeMove(8, EPSILON, 4),
                        makeMove(8, EPSILON, 6),
                        makeMove(9, EPSILON, 11),
                        makeMove(10, EPSILON, 2),
                        makeMove(10, EPSILON, 8),
                        makeMove(11, EPSILON, 13),
                        makeMove(12, EPSILON, 0),
                        makeMove(12, EPSILON, 10),
                        makeMove(13, EPSILON, 24),
                        makeMove(14, 's', 15),
                        makeMove(15, EPSILON, 23),
                        makeMove(16, 'S', 17),
                        makeMove(17, EPSILON, 21),
                        makeMove(18, 'd', 19),
                        makeMove(19, EPSILON, 21),
                        makeMove(20, EPSILON, 16),
                        makeMove(20, EPSILON, 18),
                        makeMove(21, EPSILON, 23),
                        makeMove(22, EPSILON, 14),
                        makeMove(22, EPSILON, 20),
                        makeMove(23, EPSILON, 22),
                        makeMove(23, EPSILON, 25),
                        makeMove(24, EPSILON, 22),
                        makeMove(24, EPSILON, 25)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet('$', 'S', '_', 'd', 's'),
                makeStates(
                        makeState(0, makeStates(0, 2, 4, 6, 8, 10, 12)),
                        makeState(1, makeStates(1, 13, 14, 16, 18, 20, 22, 24, 25)),
                        makeState(2, makeStates(7, 9, 11, 13, 14, 16, 18, 20, 22, 24, 25)),
                        makeState(3, makeStates(3, 11, 13, 14, 16, 18, 20, 22, 24, 25)),
                        makeState(4, makeStates(5, 9, 11, 13, 14, 16, 18, 20, 22, 24, 25)),
                        makeState(5, makeStates(14, 16, 17, 18, 20, 21, 22, 23, 25)),
                        makeState(6, makeStates(14, 16, 18, 19, 20, 21, 22, 23, 25)),
                        makeState(7, makeStates(14, 15, 16, 18, 20, 22, 23, 25)),
                        new State(8)
                ),
                makeState(0, makeStates(0, 2, 4, 6, 8, 10, 12)),
                makeStates(1, 2, 3, 4, 5, 6, 7),
                makeMoves(
                        makeMove(1, 'd', 6),
                        makeMove(2, 'd', 6),
                        makeMove(0, 'd', 8),
                        makeMove(4, 'd', 6),
                        makeMove(1, '$', 8),
                        makeMove(3, 'd', 6),
                        makeMove(2, '$', 8),
                        makeMove(6, 'd', 6),
                        makeMove(3, '$', 8),
                        makeMove(5, 'd', 6),
                        makeMove(4, '$', 8),
                        makeMove(6, '$', 8),
                        makeMove(7, 'd', 6),
                        makeMove(5, '$', 8),
                        makeMove(8, '$', 8),
                        makeMove(8, 'd', 8),
                        makeMove(7, '$', 8),
                        makeMove(0, 's', 4),
                        makeMove(1, 's', 7),
                        makeMove(2, 's', 7),
                        makeMove(4, 's', 7),
                        makeMove(3, 's', 7),
                        makeMove(6, 's', 7),
                        makeMove(5, 's', 7),
                        makeMove(7, 's', 7),
                        makeMove(8, 's', 8),
                        makeMove(0, '_', 3),
                        makeMove(1, '_', 8),
                        makeMove(2, '_', 8),
                        makeMove(3, '_', 8),
                        makeMove(5, '_', 8),
                        makeMove(4, '_', 8),
                        makeMove(0, 'S', 2),
                        makeMove(7, '_', 8),
                        makeMove(6, '_', 8),
                        makeMove(8, '_', 8),
                        makeMove(1, 'S', 5),
                        makeMove(2, 'S', 5),
                        makeMove(3, 'S', 5),
                        makeMove(4, 'S', 5),
                        makeMove(6, 'S', 5),
                        makeMove(5, 'S', 5),
                        makeMove(7, 'S', 5),
                        makeMove(0, '$', 1),
                        makeMove(8, 'S', 8)
                ),
                new State(8)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAtoDFAEmpty() {
        NFA nfa = makeNFA(
                makeAlphabet(),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(
                        makeMove(0, EPSILON, 1)
                )
        );

        DFA expected = makeDFA(
                makeAlphabet(),
                makeStates(0),
                new State(0),
                makeStates(0),
                makeMoves()
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void getPartition() {
        Partition expected = makePartition(
                makePSet(0),
                makePSet(1, 2, 3),
                makePSet(4),
                makePSet(5)
        );

        DFA dfa = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4, 5),
                new State(0),
                makeStates(4),
                makeMoves(
                        makeMove(1, 'a', 3),
                        makeMove(1, 'b', 4),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 4),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 4),
                        makeMove(4, 'a', 5),
                        makeMove(5, 'b', 5),
                        makeMove(4, 'b', 5),
                        makeMove(5, 'a', 5),
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 2)
                ),
                new State(5)
        );

        Partition actual = dfa.getPartition();

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFABasic() {
        DFA dfa = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4, 5),
                new State(0),
                makeStates(4),
                makeMoves(
                        makeMove(1, 'a', 3),
                        makeMove(1, 'b', 4),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 4),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 4),
                        makeMove(4, 'a', 5),
                        makeMove(5, 'b', 5),
                        makeMove(4, 'b', 5),
                        makeMove(5, 'a', 5),
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 2)
                ),
                new State(5)
        );
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1, 2, 3)),
                        makeState(2, makeStates(4)),
                        makeState(3, makeStates(5))
                ),
                makeState(0, makeStates(0)),
                makeStates(2),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 1),
                        makeMove(1, 'a', 1),
                        makeMove(1, 'b', 2),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 3),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 3)
                ),
                makeState(3, makeStates(5))
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFABasic2() {
        DFA dfa = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4),
                new State(0),
                makeStates(3),
                makeMoves(
                        makeMove(2, 'a', 2),
                        makeMove(2, 'b', 3),
                        makeMove(3, 'b', 3),
                        makeMove(3, 'a', 2),
                        makeMove(4, 'b', 4),
                        makeMove(4, 'a', 4),
                        makeMove(0, 'a', 1),
                        makeMove(1, 'a', 2),
                        makeMove(1, 'b', 3),
                        makeMove(0, 'b', 4)
                ),
                new State(4)
        );
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1, 2)),
                        makeState(2, makeStates(3)),
                        makeState(3, makeStates(4))
                ),
                makeState(0, makeStates(0)),
                makeStates(2),
                makeMoves(
                        makeMove(3, 'b', 3),
                        makeMove(3, 'a', 3),
                        makeMove(0, 'a', 1),
                        makeMove(1, 'a', 1),
                        makeMove(0, 'b', 3),
                        makeMove(1, 'b', 2),
                        makeMove(2, 'a', 1),
                        makeMove(2, 'b', 2)
                ),
                makeState(3, makeStates(4))
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAIdentifiers() {
        DFA dfa = makeDFA(
                makeAlphabet('$', 'S', '_', 'd', 's'),
                makeStates(0, 1, 2, 3, 4, 5, 6, 7, 8),
                new State(0),
                makeStates(1, 2, 3, 4, 5, 6, 7),
                makeMoves(
                        makeMove(1, 'd', 6),
                        makeMove(2, 'd', 6),
                        makeMove(0, 'd', 8),
                        makeMove(4, 'd', 6),
                        makeMove(1, '$', 8),
                        makeMove(3, 'd', 6),
                        makeMove(2, '$', 8),
                        makeMove(6, 'd', 6),
                        makeMove(3, '$', 8),
                        makeMove(5, 'd', 6),
                        makeMove(4, '$', 8),
                        makeMove(6, '$', 8),
                        makeMove(7, 'd', 6),
                        makeMove(5, '$', 8),
                        makeMove(8, '$', 8),
                        makeMove(8, 'd', 8),
                        makeMove(7, '$', 8),
                        makeMove(0, 's', 4),
                        makeMove(1, 's', 7),
                        makeMove(2, 's', 7),
                        makeMove(4, 's', 7),
                        makeMove(3, 's', 7),
                        makeMove(6, 's', 7),
                        makeMove(5, 's', 7),
                        makeMove(7, 's', 7),
                        makeMove(8, 's', 8),
                        makeMove(0, '_', 3),
                        makeMove(1, '_', 8),
                        makeMove(2, '_', 8),
                        makeMove(3, '_', 8),
                        makeMove(5, '_', 8),
                        makeMove(4, '_', 8),
                        makeMove(0, 'S', 2),
                        makeMove(7, '_', 8),
                        makeMove(6, '_', 8),
                        makeMove(8, '_', 8),
                        makeMove(1, 'S', 5),
                        makeMove(2, 'S', 5),
                        makeMove(3, 'S', 5),
                        makeMove(4, 'S', 5),
                        makeMove(6, 'S', 5),
                        makeMove(5, 'S', 5),
                        makeMove(7, 'S', 5),
                        makeMove(0, '$', 1),
                        makeMove(8, 'S', 8)
                ),
                new State(8)
        );

        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(
                makeAlphabet('$', 'S', '_', 'd', 's'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1, 2, 3, 4, 5, 6, 7)),
                        makeState(2, makeStates(8))
                ),
                makeState(0, makeStates(0)),
                makeStates(1),
                makeMoves(
                        makeMove(1, '_', 2),
                        makeMove(2, '_', 2),
                        makeMove(0, 'S', 1),
                        makeMove(0, 's', 1),
                        makeMove(1, 'S', 1),
                        makeMove(1, 's', 1),
                        makeMove(2, 'S', 2),
                        makeMove(2, 's', 2),
                        makeMove(0, '$', 1),
                        makeMove(0, 'd', 2),
                        makeMove(1, 'd', 1),
                        makeMove(1, '$', 2),
                        makeMove(2, '$', 2),
                        makeMove(2, 'd', 2),
                        makeMove(0, '_', 1)
                ),
                makeState(2, makeStates(8))
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAIntegers() {
        String regex = "0|((1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*)";
        NFA nfa = NFA.regexToNFA(regex);
        DFA dfa = DFA.NFAtoDFA(nfa);
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(
                makeAlphabet('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'),
                makeStates(
                        makeState(0, makeStates(0)),
                        makeState(1, makeStates(1)),
                        makeState(2, makeStates(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                                16, 17, 18, 19, 20)),
                        makeState(3, makeStates(21))
                ),
                makeState(0, makeStates(0)),
                makeStates(1, 2),
                makeMoves(
                        makeMove(0, '9', 2),
                        makeMove(0, '7', 2),
                        makeMove(1, '9', 3),
                        makeMove(2, '9', 2),
                        makeMove(0, '5', 2),
                        makeMove(1, '7', 3),
                        makeMove(2, '7', 2),
                        makeMove(3, '9', 3),
                        makeMove(0, '3', 2),
                        makeMove(1, '5', 3),
                        makeMove(2, '5', 2),
                        makeMove(3, '7', 3),
                        makeMove(0, '1', 2),
                        makeMove(1, '3', 3),
                        makeMove(2, '3', 2),
                        makeMove(3, '5', 3),
                        makeMove(2, '1', 2),
                        makeMove(3, '3', 3),
                        makeMove(1, '1', 3),
                        makeMove(3, '1', 3),
                        makeMove(0, '8', 2),
                        makeMove(0, '6', 2),
                        makeMove(1, '8', 3),
                        makeMove(2, '8', 2),
                        makeMove(3, '8', 3),
                        makeMove(0, '4', 2),
                        makeMove(1, '6', 3),
                        makeMove(2, '6', 2),
                        makeMove(3, '6', 3),
                        makeMove(0, '2', 2),
                        makeMove(1, '4', 3),
                        makeMove(2, '4', 2),
                        makeMove(0, '0', 1),
                        makeMove(3, '4', 3),
                        makeMove(1, '2', 3),
                        makeMove(2, '2', 2),
                        makeMove(3, '2', 3),
                        makeMove(1, '0', 3),
                        makeMove(2, '0', 2),
                        makeMove(3, '0', 3)
                ),
                makeState(3, makeStates(21))
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAEmpty() {
        DFA dfa = makeDFA(
                makeAlphabet(),
                makeStates(0),
                new State(0),
                makeStates(0),
                makeMoves()
        );

        DFA expected = makeDFA(
                makeAlphabet(),
                makeStates(0),
                new State(0),
                makeStates(0),
                makeMoves()
        );

        DFA actual = DFA.DFAtoMinDFA(dfa);
        assertEquals(expected, actual);
    }

    @Test
    void toStringDFA() {
        String expected = "digraph finite_state_machine {\n" +
                "\trankdir=LR;\n" +
                "\tsize=\"8,5\";\n" +
                "\n" +
                "\tnode [shape = doublecircle];\n" +
                "\t4 ;\n" +
                "\n" +
                "\tnode [shape = circle];\n" +
                "\t0 [label=\"0\\n{0, 2, 4}\"];\n" +
                "\t1 [label=\"1\\n{1, 5, 6, 8, 9, 10}\"];\n" +
                "\t2 [label=\"2\\n{3, 5, 6, 8, 9, 10}\"];\n" +
                "\t3 [label=\"3\\n{6, 7, 9, 10}\"];\n" +
                "\t4 [label=\"4\\n{11}\"];\n" +
                "\n" +
                "\t0 -> 1 [label = \"a\"];\n" +
                "\t0 -> 2 [label = \"b\"];\n" +
                "\t1 -> 3 [label = \"a\"];\n" +
                "\t1 -> 4 [label = \"b\"];\n" +
                "\t2 -> 3 [label = \"a\"];\n" +
                "\t2 -> 4 [label = \"b\"];\n" +
                "\t3 -> 3 [label = \"a\"];\n" +
                "\t3 -> 4 [label = \"b\"];\n" +
                "\t4 -> φ [label = \"a, b\"];\n" +
                "\tφ -> φ [label = \"a, b\"];\n" +
                "\n" +
                "\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> 0;\n" +
                "}\n";

        DFA dfa = makeDFA(
                makeAlphabet('a', 'b'),
                makeStates(
                        makeState(0, makeStates(0, 2, 4)),
                        makeState(1, makeStates(1, 5, 6, 8, 9, 10)),
                        makeState(2, makeStates(3, 5, 6, 8, 9, 10)),
                        makeState(3, makeStates(6, 7, 9, 10)),
                        makeState(4, makeStates(11)),
                        new State(5)
                ),
                makeState(0, makeStates(0, 2, 4)),
                makeStates(4),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(0, 'b', 2),
                        makeMove(1, 'a', 3),
                        makeMove(1, 'b', 4),
                        makeMove(2, 'a', 3),
                        makeMove(2, 'b', 4),
                        makeMove(3, 'a', 3),
                        makeMove(3, 'b', 4),
                        makeMove(4, 'a', 5),
                        makeMove(4, 'b', 5),
                        makeMove(5, 'a', 5),
                        makeMove(5, 'b', 5)
                ),
                new State(5)
        );

        String actual = dfa.toString();

        assertEquals(expected, actual);
    }
}