package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static algorithms.FSA.EPSILON;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NFATest {

    @BeforeEach
    void setUp() {
        State.setIdCounter(0);
    }

    @Test
    void regexToNFABasic() {
        NFA expected = makeNFA(
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
        NFA actual = NFA.regexToNFA("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFABasic2() {
        NFA expected = makeNFA(
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
        NFA actual = NFA.regexToNFA("a(a|b)*b");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAEmpty() {
        NFA expected = makeNFA(
                makeAlphabet(),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(makeMove(0, EPSILON, 1))
        );
        NFA actual = NFA.regexToNFA("");
        assertEquals(expected, actual);
    }

    @Test
    void regexToNFAIdentifiers() {
        NFA expected = makeNFA(
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
        NFA actual = NFA.regexToNFA("($|_|s|S)(s|S|d)*");
        assertEquals(expected, actual);
    }

    @Test
    void makeSingle() {
        NFA expected = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(makeMove(0, 'a', 1))
        );

        NFA actual = NFA.makeSingle('a');
        assertEquals(expected, actual);
    }

    @Test
    void concatenate() {
        NFA expected = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3),
                new State(0),
                makeStates(3),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 2),
                        makeMove(2, 'b', 3))
        );

        NFA first = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(makeMove(0, 'a', 1))
        );

        NFA second = makeNFA(
                makeAlphabet('b'),
                makeStates(2, 3),
                new State(2),
                makeStates(3),
                makeMoves(makeMove(2, 'b', 3))
        );
        State.setIdCounter(4);

        NFA actual = NFA.concatenate(first, second);
        assertEquals(expected, actual);
    }

    @Test
    void kleeneStar() {
        NFA expected = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1, 2, 3),
                new State(2),
                makeStates(3),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 0),
                        makeMove(1, EPSILON, 3),
                        makeMove(2, EPSILON, 0),
                        makeMove(2, EPSILON, 3))
        );
        NFA actual = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(makeMove(0, 'a', 1))
        );

        State.setIdCounter(2);

        actual = NFA.kleeneStar(actual);
        assertEquals(expected, actual);
    }

    @Test
    void alternate() {
        NFA expected = makeNFA(
                makeAlphabet('a', 'b'),
                makeStates(0, 1, 2, 3, 4, 5),
                new State(4),
                makeStates(5),
                makeMoves(
                        makeMove(0, 'a', 1),
                        makeMove(1, EPSILON, 5),
                        makeMove(2, 'b', 3),
                        makeMove(3, EPSILON, 5),
                        makeMove(4, EPSILON, 0),
                        makeMove(4, EPSILON, 2))
        );

        NFA first = makeNFA(
                makeAlphabet('a'),
                makeStates(0, 1),
                new State(0),
                makeStates(1),
                makeMoves(makeMove(0, 'a', 1))
        );

        NFA second = makeNFA(
                makeAlphabet('b'),
                makeStates(2, 3),
                new State(2),
                makeStates(3),
                makeMoves(makeMove(2, 'b', 3))
        );

        State.setIdCounter(4);

        NFA actual = NFA.alternate(first, second);
        assertEquals(expected, actual);
    }

    @Test
    void infixToPostfix() {
        String expected = "ab|a*.b.";
        String actual = Regex.infixToPostfix("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void markWithConcatenation() {
        String expected = "(a|b).a*.b";
        String actual = Regex.markWithConcatenation("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void toDOT() {
        String expected = "digraph finite_state_machine {\n" +
                "\trankdir=LR;\n" +
                "\tsize=\"8,5\";\n" +
                "\n" +
                "\tnode [shape = doublecircle];\n" +
                "\t11 ;\n" +
                "\n" +
                "\tnode [shape = circle];\n" +
                "\n" +
                "\t0 -> 1 [label = \"a\"];\n" +
                "\t1 -> 5 [label = \"ɛ\"];\n" +
                "\t2 -> 3 [label = \"b\"];\n" +
                "\t3 -> 5 [label = \"ɛ\"];\n" +
                "\t4 -> 0 [label = \"ɛ\"];\n" +
                "\t4 -> 2 [label = \"ɛ\"];\n" +
                "\t5 -> 8 [label = \"ɛ\"];\n" +
                "\t6 -> 7 [label = \"a\"];\n" +
                "\t7 -> 6 [label = \"ɛ\"];\n" +
                "\t7 -> 9 [label = \"ɛ\"];\n" +
                "\t8 -> 6 [label = \"ɛ\"];\n" +
                "\t8 -> 9 [label = \"ɛ\"];\n" +
                "\t9 -> 10 [label = \"ɛ\"];\n" +
                "\t10 -> 11 [label = \"b\"];\n" +
                "\n" +
                "\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> 4;\n" +
                "}\n";

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
                        makeMove(10, 'b', 11))
        );
        String actual = nfa.toDOT();

        assertEquals(expected, actual);
    }
}