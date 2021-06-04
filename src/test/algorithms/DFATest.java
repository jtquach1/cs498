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
    void epsilonClosureForSetOfStates() {
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
    void NFAToDFABasic() {
        NFA nfa = makeNFA(4);
        Utility.addSymbols(nfa, 'a', 'b');
        Utility.addStates(nfa, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Utility.addFinalStates(nfa, 11);
        Utility.addMoves(nfa,
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
                Utility.makeMove(10, 'b', 11)
        );

        DFA expected = makeDFA(0, 5);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4, 5);
        Utility.addFinalStates(expected, 4);
        Utility.addMoves(expected,
                Utility.makeMove(1, 'a', 3),
                Utility.makeMove(1, 'b', 4),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 4),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 4),
                Utility.makeMove(4, 'a', 5),
                Utility.makeMove(5, 'b', 5),
                Utility.makeMove(4, 'b', 5),
                Utility.makeMove(5, 'a', 5),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 2)
        );

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void NFAToDFABasic2() {
        NFA nfa = makeNFA(0);
        Utility.addSymbols(nfa, 'a', 'b');
        Utility.addStates(nfa, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        Utility.addFinalStates(nfa, 11);
        Utility.addMoves(nfa,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, FSA.EPSILON, 8),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(3, FSA.EPSILON, 7),
                Utility.makeMove(4, 'b', 5),
                Utility.makeMove(5, FSA.EPSILON, 7),
                Utility.makeMove(6, FSA.EPSILON, 2),
                Utility.makeMove(6, FSA.EPSILON, 4),
                Utility.makeMove(7, FSA.EPSILON, 6),
                Utility.makeMove(7, FSA.EPSILON, 9),
                Utility.makeMove(8, FSA.EPSILON, 6),
                Utility.makeMove(8, FSA.EPSILON, 9),
                Utility.makeMove(9, FSA.EPSILON, 10),
                Utility.makeMove(10, 'b', 11)
        );

        DFA expected = makeDFA(0, 4);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3, 4);
        Utility.addFinalStates(expected, 3);
        Utility.addMoves(expected,
                Utility.makeMove(2, 'a', 2),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, 'b', 3),
                Utility.makeMove(3, 'a', 2),
                Utility.makeMove(4, 'b', 4),
                Utility.makeMove(4, 'a', 4),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, 'a', 2),
                Utility.makeMove(1, 'b', 3),
                Utility.makeMove(0, 'b', 4)
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
    void NFAtoDFAEmpty() {
        NFA nfa = makeNFA(0);
        Utility.addStates(nfa, 0, 1);
        Utility.addFinalStates(nfa, 1);
        Utility.addMoves(nfa,
                Utility.makeMove(0, FSA.EPSILON, 1)
        );

        DFA expected = makeDFA(0);
        Utility.addStates(expected, 0);
        Utility.addFinalStates(expected, 0);

        DFA actual = DFA.NFAtoDFA(nfa);
        assertEquals(expected, actual);
    }

    @Test
    void getPartition() {
        Partition expected = new Partition();
        Utility.addPSets(expected,
                Utility.makePSet(0),
                Utility.makePSet(1, 2, 3),
                Utility.makePSet(4),
                Utility.makePSet(5)
        );

        DFA dfa = makeDFA(0, 5);
        Utility.addSymbols(dfa, 'a', 'b');
        Utility.addStates(dfa, 0, 1, 2, 3, 4, 5);
        Utility.addFinalStates(dfa, 4);
        Utility.addMoves(dfa,
                Utility.makeMove(1, 'a', 3),
                Utility.makeMove(1, 'b', 4),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 4),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 4),
                Utility.makeMove(4, 'a', 5),
                Utility.makeMove(5, 'b', 5),
                Utility.makeMove(4, 'b', 5),
                Utility.makeMove(5, 'a', 5),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 2)
        );
        Partition actual = dfa.getPartition();

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFABasic() {
        DFA dfa = makeDFA(0, 5);
        Utility.addSymbols(dfa, 'a', 'b');
        Utility.addStates(dfa, 0, 1, 2, 3, 4, 5);
        Utility.addFinalStates(dfa, 4);
        Utility.addMoves(dfa,
                Utility.makeMove(1, 'a', 3),
                Utility.makeMove(1, 'b', 4),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 4),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 4),
                Utility.makeMove(4, 'a', 5),
                Utility.makeMove(5, 'b', 5),
                Utility.makeMove(4, 'b', 5),
                Utility.makeMove(5, 'a', 5),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 2)
        );
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(0, 3);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 2);
        Utility.addMoves(expected,
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(0, 'b', 1),
                Utility.makeMove(1, 'a', 1),
                Utility.makeMove(1, 'b', 2),
                Utility.makeMove(2, 'a', 3),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(3, 'b', 3)
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFABasic2() {
        DFA dfa = makeDFA(0, 4);
        Utility.addSymbols(dfa, 'a', 'b');
        Utility.addStates(dfa, 0, 1, 2, 3, 4);
        Utility.addFinalStates(dfa, 3);
        Utility.addMoves(dfa,
                Utility.makeMove(2, 'a', 2),
                Utility.makeMove(2, 'b', 3),
                Utility.makeMove(3, 'b', 3),
                Utility.makeMove(3, 'a', 2),
                Utility.makeMove(4, 'b', 4),
                Utility.makeMove(4, 'a', 4),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, 'a', 2),
                Utility.makeMove(1, 'b', 3),
                Utility.makeMove(0, 'b', 4)
        );
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(0, 3);
        Utility.addSymbols(expected, 'a', 'b');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 2);
        Utility.addMoves(expected,
                Utility.makeMove(3, 'b', 3),
                Utility.makeMove(3, 'a', 3),
                Utility.makeMove(0, 'a', 1),
                Utility.makeMove(1, 'a', 1),
                Utility.makeMove(0, 'b', 3),
                Utility.makeMove(1, 'b', 2),
                Utility.makeMove(2, 'a', 1),
                Utility.makeMove(2, 'b', 2)
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAIdentifiers() {
        DFA dfa = makeDFA(0, 8);
        Utility.addSymbols(dfa, '$', 'S', '_', 'd', 's');
        Utility.addStates(dfa, 0, 1, 2, 3, 4, 5, 6, 7, 8);
        Utility.addFinalStates(dfa, 1, 2, 3, 4, 5, 6, 7);
        Utility.addMoves(dfa,
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
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(0, 2);
        Utility.addSymbols(expected, '$', 'S', '_', 'd', 's');
        Utility.addStates(expected, 0, 1, 2);
        Utility.addFinalStates(expected, 1);
        Utility.addMoves(expected,
                Utility.makeMove(1, '_', 2),
                Utility.makeMove(2, '_', 2),
                Utility.makeMove(0, 'S', 1),
                Utility.makeMove(0, 's', 1),
                Utility.makeMove(1, 'S', 1),
                Utility.makeMove(1, 's', 1),
                Utility.makeMove(2, 'S', 2),
                Utility.makeMove(2, 's', 2),
                Utility.makeMove(0, '$', 1),
                Utility.makeMove(0, 'd', 2),
                Utility.makeMove(1, 'd', 1),
                Utility.makeMove(1, '$', 2),
                Utility.makeMove(2, '$', 2),
                Utility.makeMove(2, 'd', 2),
                Utility.makeMove(0, '_', 1)
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAIntegers() {
        String regex = "0|((1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*)";
        NFA nfa = NFA.regexToNFA(regex);
        DFA dfa = DFA.NFAtoDFA(nfa);
        DFA actual = DFA.DFAtoMinDFA(dfa);

        DFA expected = makeDFA(0, 3);
        Utility.addSymbols(expected, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        Utility.addStates(expected, 0, 1, 2, 3);
        Utility.addFinalStates(expected, 1, 2);
        Utility.addMoves(expected,
                Utility.makeMove(0, '9', 2),
                Utility.makeMove(0, '7', 2),
                Utility.makeMove(1, '9', 3),
                Utility.makeMove(2, '9', 2),
                Utility.makeMove(0, '5', 2),
                Utility.makeMove(1, '7', 3),
                Utility.makeMove(2, '7', 2),
                Utility.makeMove(3, '9', 3),
                Utility.makeMove(0, '3', 2),
                Utility.makeMove(1, '5', 3),
                Utility.makeMove(2, '5', 2),
                Utility.makeMove(3, '7', 3),
                Utility.makeMove(0, '1', 2),
                Utility.makeMove(1, '3', 3),
                Utility.makeMove(2, '3', 2),
                Utility.makeMove(3, '5', 3),
                Utility.makeMove(2, '1', 2),
                Utility.makeMove(3, '3', 3),
                Utility.makeMove(1, '1', 3),
                Utility.makeMove(3, '1', 3),
                Utility.makeMove(0, '8', 2),
                Utility.makeMove(0, '6', 2),
                Utility.makeMove(1, '8', 3),
                Utility.makeMove(2, '8', 2),
                Utility.makeMove(3, '8', 3),
                Utility.makeMove(0, '4', 2),
                Utility.makeMove(1, '6', 3),
                Utility.makeMove(2, '6', 2),
                Utility.makeMove(3, '6', 3),
                Utility.makeMove(0, '2', 2),
                Utility.makeMove(1, '4', 3),
                Utility.makeMove(2, '4', 2),
                Utility.makeMove(0, '0', 1),
                Utility.makeMove(3, '4', 3),
                Utility.makeMove(1, '2', 3),
                Utility.makeMove(2, '2', 2),
                Utility.makeMove(3, '2', 3),
                Utility.makeMove(1, '0', 3),
                Utility.makeMove(2, '0', 2),
                Utility.makeMove(3, '0', 3)
        );

        assertEquals(expected, actual);
    }

    @Test
    void DFAtoMinDFAEmpty() {
        DFA dfa = makeDFA(0);
        Utility.addStates(dfa, 0);
        Utility.addFinalStates(dfa, 0);

        DFA expected = makeDFA(0);
        Utility.addStates(expected, 0);
        Utility.addFinalStates(expected, 0);

        DFA actual = DFA.DFAtoMinDFA(dfa);
        assertEquals(dfa, actual);
    }
}