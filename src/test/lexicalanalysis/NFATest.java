import org.javatuples.Triplet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NFATest {
    private static void addSymbols(NFA nfa, Character... symbols) {
        for (Character symbol : symbols) {
            nfa.addSymbol(symbol);
        }
    }

    private static void addStates(NFA nfa, Integer... stateIds) {
        for (Integer id : stateIds) {
            nfa.addState(new State(id));
        }
    }

    private static void addFinalStates(NFA nfa, Integer... stateIds) {
        for (Integer id : stateIds) {
            nfa.addFinalState(new State(id));
        }
    }

    private static void addMoves(NFA nfa, Triplet... moves) {
        for (Triplet move : moves) {
            nfa.addMove(move);
        }
    }

    private static Triplet makeMove(Integer fromId, Character consumed, Integer toId) {
        return new Triplet(new State(fromId), consumed, new State(toId));
    }

    private static NFA makeNFA(Integer stateId) {
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
    void regexToNfa() {
        NFA expected = makeNFA(4);
        addSymbols(expected, 'a', 'b');
        addStates(expected, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        addFinalStates(expected, 11);
        addMoves(expected,
                makeMove(0, 'a', 1),
                makeMove(1, NFA.EPSILON, 5),
                makeMove(2, 'b', 3),
                makeMove(3, NFA.EPSILON, 5),
                makeMove(4, NFA.EPSILON, 0),
                makeMove(4, NFA.EPSILON, 2),
                makeMove(5, NFA.EPSILON, 9),
                makeMove(6, 'a', 7),
                makeMove(7, NFA.EPSILON, 8),
                makeMove(8, NFA.EPSILON, 10),
                makeMove(9, NFA.EPSILON, 6),
                makeMove(9, NFA.EPSILON, 8),
                makeMove(10, 'b', 11));

        NFA actual = NFA.regexToNfa("(a|b)a*b");
        System.out.println(actual.toString());
        assertEquals(expected, actual);
    }

    @Test
    void makeSingle() {
        NFA expected = makeNFA(0);
        addSymbols(expected, 'a');
        addStates(expected, 0, 1);
        addFinalStates(expected, 1);
        addMoves(expected, makeMove(0, 'a', 1));

        NFA actual = NFA.makeSingle('a');
        assertEquals(expected, actual);
    }

    @Test
    void deepClone() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new TreeSet<>();
        State start = new State(0);
        Set<State> finalStates = new TreeSet<>();
        State finalState = new State(1);
        Set<Triplet> moves = new TreeSet<>();
        Triplet move = new Triplet(start, 'a', finalState);

        alphabet.addSymbol('a');
        moves.add(move);
        states.add(start);
        states.add(finalState);
        finalStates.add(finalState);
        NFA nfa = new NFA(alphabet, states, start, finalStates, moves);

        NFA expected = nfa;
        NFA actual = nfa.deepClone();
        assertEquals(expected, actual);

        NFA unexpected = nfa;
        unexpected.addSymbol('b');
        unexpected.addFinalState(new State(2));
        unexpected.addState(new State(2));
        unexpected.addMove(new State(0), 'b', new State(2));
        assertNotEquals(unexpected, actual);
    }

    @Test
    void concatenate() {
        NFA expected = makeNFA(0);
        addSymbols(expected, 'a', 'b');
        addStates(expected, 0, 1, 2, 3);
        addFinalStates(expected, 3);
        addMoves(expected,
                makeMove(0, 'a', 1),
                makeMove(1, NFA.EPSILON, 2),
                makeMove(2, 'b', 3));

        NFA first = makeNFA(0);
        addSymbols(first, 'a');
        addStates(first, 0, 1);
        addFinalStates(first, 1);
        addMoves(first, makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        addSymbols(second, 'b');
        addStates(second, 2, 3);
        addFinalStates(second, 3);
        addMoves(second, makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.concatenate(first, second);
        assertEquals(expected, actual);
    }

    @Test
    void kleeneStar() {
        NFA expected = makeNFA(3);
        addSymbols(expected, 'a');
        addStates(expected, 0, 1, 2, 3);
        addFinalStates(expected, 2);
        addMoves(expected,
                makeMove(0, 'a', 1),
                makeMove(1, NFA.EPSILON, 0),
                makeMove(1, NFA.EPSILON, 2),
                makeMove(3, NFA.EPSILON, 0),
                makeMove(3, NFA.EPSILON, 2));

        NFA actual = makeNFA(0);
        addSymbols(actual, 'a');
        addStates(actual, 0, 1);
        addFinalStates(actual, 1);
        addMoves(actual, makeMove(0, 'a', 1));
        State.setIdCounter(2);

        actual = NFA.kleeneStar(actual);
        assertEquals(expected, actual);
    }

    @Test
    void alternate() {
        NFA expected = makeNFA(4);
        addSymbols(expected, 'a', 'b');
        addStates(expected, 0, 1, 2, 3, 4, 5);
        addFinalStates(expected, 5);
        addMoves(expected,
                makeMove(0, 'a', 1),
                makeMove(1, NFA.EPSILON, 5),
                makeMove(2, 'b', 3),
                makeMove(3, NFA.EPSILON, 5),
                makeMove(4, NFA.EPSILON, 0),
                makeMove(4, NFA.EPSILON, 2));

        NFA first = makeNFA(0);
        addSymbols(first, 'a');
        addStates(first, 0, 1);
        addFinalStates(first, 1);
        addMoves(first, makeMove(0, 'a', 1));

        NFA second = makeNFA(2);
        addSymbols(second, 'b');
        addStates(second, 2, 3);
        addFinalStates(second, 3);
        addMoves(second, makeMove(2, 'b', 3));
        State.setIdCounter(4);

        NFA actual = NFA.alternate(first, second);
        assertEquals(expected, actual);
    }
}