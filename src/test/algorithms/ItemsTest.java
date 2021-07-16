package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.makeItems;
import static algorithms.Utility.makeProductions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ItemsTest {
    // From lecture slides, non LL(1) grammar with new start state
    Productions augmentedArithmeticExpressionProductions;
    FirstMap augmentedArithmeticExpressionFirstMap;

    // From Spring 2019 sample exam, problem 4
    Productions sampleExamProblem4Productions;
    FirstMap sampleExamProblem4FirstMap;

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

        augmentedArithmeticExpressionFirstMap = new FirstMap();
        augmentedArithmeticExpressionFirstMap.put("(", new First("("));
        augmentedArithmeticExpressionFirstMap.put(")", new First(")"));
        augmentedArithmeticExpressionFirstMap.put("*", new First("*"));
        augmentedArithmeticExpressionFirstMap.put("+", new First("+"));
        augmentedArithmeticExpressionFirstMap.put("E'", new First("(", "id"));
        augmentedArithmeticExpressionFirstMap.put("E", new First("(", "id"));
        augmentedArithmeticExpressionFirstMap.put("F", new First("(", "id"));
        augmentedArithmeticExpressionFirstMap.put("T", new First("(", "id"));
        augmentedArithmeticExpressionFirstMap.put("id", new First("id"));

        sampleExamProblem4Productions = makeProductions(
                "X ::= Y = Z",
                "X ::= Z",
                "Y ::= + Z",
                "Y ::= a",
                "Z ::= Y"
        );

        sampleExamProblem4FirstMap = new FirstMap();
        sampleExamProblem4FirstMap.put("+", new First("+"));
        sampleExamProblem4FirstMap.put("=", new First("="));
        sampleExamProblem4FirstMap.put("a", new First("a"));
        sampleExamProblem4FirstMap.put("X", new First("+", "a"));
        sampleExamProblem4FirstMap.put("Y", new First("+", "a"));
        sampleExamProblem4FirstMap.put("Z", new First("+", "a"));
    }

    @Test
    void closure() {
        assertEquals(
                makeItems(
                        "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                        "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                        "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                        "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                        "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                        "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                        "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
                ),
                makeItems("[E' ::= " + MARKER + " E, " + TERMINATOR + "]").closure(
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
        );

        assertEquals(
                makeItems(
                        "[X' ::= " + MARKER + " X, " + TERMINATOR + "]",
                        "[X ::= " + MARKER + " Y = Z, " + TERMINATOR + "]",
                        "[X ::= " + MARKER + " Z, " + TERMINATOR + "]",
                        "[Y ::= " + MARKER + " + Z, " + TERMINATOR + "/=]",
                        "[Y ::= " + MARKER + " a, " + TERMINATOR + "/=]",
                        "[Z ::= " + MARKER + " Y, " + TERMINATOR + "]"
                ),
                makeItems("[X' ::= " + MARKER + " X, " + TERMINATOR + "]").closure(
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
        );
    }

    @Test
    void computeGoto() {
        Items s0 = makeItems(
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
                s0.computeGoto(
                        "E",
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
        );

        // goto(s0, T) = s2
        assertEquals(
                makeItems(
                        "[E ::= T " + MARKER + ", " + TERMINATOR + "/+]",
                        "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
                ),
                s0.computeGoto(
                        "T",
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
        );


        // goto(s0, F) = s3
        assertEquals(
                makeItems("[T ::= F " + MARKER + ", " + TERMINATOR + "/+/*]"),
                s0.computeGoto(
                        "F",
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
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
                s0.computeGoto(
                        "(",
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
        );

        // goto(s0, id) = s5
        assertEquals(
                makeItems("[F ::= id " + MARKER + ", " + TERMINATOR + "/+/*]"),
                s0.computeGoto(
                        "id",
                        augmentedArithmeticExpressionFirstMap,
                        augmentedArithmeticExpressionProductions
                )
        );

        s0 = makeItems(
                "[X' ::= " + MARKER + " X, " + TERMINATOR + "]",
                "[X ::= " + MARKER + " Y = Z, " + TERMINATOR + "]",
                "[X ::= " + MARKER + " Z, " + TERMINATOR + "]",
                "[Y ::= " + MARKER + " + Z, " + TERMINATOR + "/=]",
                "[Y ::= " + MARKER + " a, " + TERMINATOR + "/=]",
                "[Z ::= " + MARKER + " Y, " + TERMINATOR + "]"
        );

        // goto(s0, X) = s1
        assertEquals(
                makeItems("[X' ::= X " + MARKER + ", " + TERMINATOR + "]"),
                s0.computeGoto(
                        "X",
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
        );

        // goto(s0, Y) = s2
        assertEquals(
                makeItems(
                        "[X ::= Y " + MARKER + " = Z, " + TERMINATOR + "]",
                        "[Z ::= Y " + MARKER + ", " + TERMINATOR + "]"
                ),
                s0.computeGoto(
                        "Y",
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
        );


        // goto(s0, Z) = s3
        assertEquals(
                makeItems("[X ::= Z " + MARKER + ", " + TERMINATOR + "]"),
                s0.computeGoto(
                        "Z",
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
        );

        // goto(s0, +) = s4
        assertEquals(
                makeItems(
                        "[Y ::= + " + MARKER + " Z, " + TERMINATOR + "/=]",
                        "[Z ::= " + MARKER + " Y, " + TERMINATOR + "/=]",
                        "[Y ::= " + MARKER + " + Z, " + TERMINATOR + "/=]",
                        "[Y ::= " + MARKER + " a , " + TERMINATOR + "/=]"
                ),
                s0.computeGoto(
                        "+",
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
        );

        // goto(s0, a) = s5
        assertEquals(
                makeItems("[Y ::= a " + MARKER + ", " + TERMINATOR + "/=]"),
                s0.computeGoto(
                        "a",
                        sampleExamProblem4FirstMap,
                        sampleExamProblem4Productions
                )
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