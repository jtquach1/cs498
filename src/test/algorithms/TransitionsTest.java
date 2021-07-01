package algorithms;

import org.junit.jupiter.api.Test;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.makeItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class TransitionsTest {

    @Test
    void deepClone() {
        Items s0 = makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        Items s1 = makeItems(
                "[E' ::= E " + MARKER + ", " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, +]"
        );

        Transitions expected = new Transitions();
        expected.add(new Goto(s0, "E", s1));

        Transitions actual = expected.deepClone();

        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }
}