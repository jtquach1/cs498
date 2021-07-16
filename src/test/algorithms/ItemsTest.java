package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.makeItems;
import static algorithms.Utility.makeProductions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ItemsTest {
    Productions augmentedArithmeticExpressionProductions;

    @BeforeEach
    void setUp() {
        augmentedArithmeticExpressionProductions = makeProductions(
                "E' ::= E",
                "E ::= E + T",
                "E ::= T",
                "T ::= T * F",
                "T ::= F",
                "F ::= ( E )",
                "F ::= id"
        );
    }

    @Test
    void closure() {
        // From lecture slides
        Items expected = makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        Item kernel = new Item(TERMINATOR, "E'", MARKER, "E");
        Items set = new Items(Collections.singletonList(kernel));
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
        Items actual = set.closure(firstMap, augmentedArithmeticExpressionProductions);

        assertEquals(expected, actual);
    }

    @Test
    void computeGoto() {
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

        // s0
        Items state = makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        // goto(s0, E) = s1
        assertEquals(
                makeItems(
                        "[E' ::= E " + MARKER + ", " + TERMINATOR + "]",
                        "[E ::= E " + MARKER + " + T, " + TERMINATOR + "/+]"
                ),
                state.computeGoto("E", firstMap, augmentedArithmeticExpressionProductions)
        );

        // goto(s0, T) = s2
        assertEquals(
                makeItems(
                        "[E ::= T " + MARKER + ", " + TERMINATOR + "/+]",
                        "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
                ),
                state.computeGoto("T", firstMap, augmentedArithmeticExpressionProductions)
        );


        // goto(s0, F) = s3
        assertEquals(
                makeItems("[T ::= F " + MARKER + ", " + TERMINATOR + "/+/*]"),
                state.computeGoto("F", firstMap, augmentedArithmeticExpressionProductions)
        );

        // goto(s0, () = s4
        assertEquals(
                makeItems(
                        "[F ::= ( " + MARKER + " E ), " + TERMINATOR + "/+/*]",
                        "[E ::= " + MARKER + " E + T, +/)]",
                        "[E ::= " + MARKER + " T, +/)]",
                        "[T ::= " + MARKER + " T * F, +/*/)]",
                        "[T ::= " + MARKER + " F, +/*/)]",
                        "[F ::= " + MARKER + " ( E ), +/*/)]",
                        "[F ::= " + MARKER + " id, +/*/)]"
                ),
                state.computeGoto("(", firstMap, augmentedArithmeticExpressionProductions)
        );

        // goto(s0, id) = s5
        assertEquals(
                makeItems("[F ::= id " + MARKER + ", " + TERMINATOR + "/+/*]"),
                state.computeGoto("id", firstMap, augmentedArithmeticExpressionProductions)
        );
    }

    @Test
    void deepClone() {
        Items expected = new Items(Arrays.asList(
                new Item(TERMINATOR, "E'", MARKER, "E"),
                new Item(TERMINATOR, "E'", MARKER, "E", "+", "T"),
                new Item("+", "E'", MARKER, "E", "+", "T")
        ));
        Items actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }
}