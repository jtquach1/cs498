package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.makeGoto;
import static algorithms.Utility.makeItems;
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

        collection = new LR1Collection(Collections.singletonList(s0), new Transitions());
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
        transitions.add(makeGoto(s0, "E", s1));
        LR1Collection expected = new LR1Collection(Arrays.asList(s0, s1), transitions);

        LR1Collection actual = collection;
        actual.add(s0, "E", s1);
        assertEquals(expected, actual);
    }
}