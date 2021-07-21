package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.makeItems;
import static algorithms.Utility.makeTransition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class LR1CollectionTest {
    Items s0, s1;
    LR1Collection collection;

    @BeforeEach
    void setUp() {
        s0 = makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        s1 = makeItems(
                "[E' ::= E " + MARKER + ", " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, +]"
        );

        collection = new LR1Collection(s0, new Transitions());
    }

    @Test
    void deepClone() {
        LR1Collection expected = collection;
        LR1Collection actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    void add() {
        Transitions transitions = new Transitions();
        transitions.add(makeTransition(s0, "E", s1));
        LR1Collection expected = new LR1Collection(s0, transitions);
        expected.addAll(Arrays.asList(s0, s1));

        LR1Collection actual = collection;
        Transition transition = new Transition(s0, "E", s1);
        actual.add(transition);
        assertEquals(expected, actual);
    }

    @Test
    void getStart() {
        assertEquals(s0, collection.getStart());
    }
}