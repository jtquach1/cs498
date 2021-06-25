package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
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

        collection = new LR1Collection(Arrays.asList(s0, s1));
    }

    @Test
    void deepClone() {
        LR1Collection expected = collection;

        LR1Collection actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    /*
    @Test
    void populate() {
        FirstMap firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E'", new First("(", "id"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("id", new First("id"));

        Grammar augmentedArithmeticExpression = new Grammar(
                makeNonTerminals("E", "T", "F"),
                makeTerminals("+", "*", "(", ")", "id"),
                "E'",
                makeProductions(
                        "E' ::= E",
                        "E ::= E + T",
                        "E ::= T",
                        "T ::= T * F",
                        "T ::= F",
                        "F ::= ( E )",
                        "F ::= id"
                )
        );
    }
    */
}