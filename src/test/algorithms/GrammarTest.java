package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {
    Grammar arithmeticExpression, augmentedArithmeticExpression, arithmeticExpressionRedux,
            firstSampleExamQuestion, leftRecursionExample;

    @BeforeEach
    void setUp() {
        firstSampleExamQuestion = new Grammar(
                makeNonTerminals("A", "B"),
                makeTerminals("a", "b", "c", EPSILON),
                "S",
                makeProductions(
                        "S ::= A a",
                        "S ::= a",
                        "A ::= c",
                        "A ::= b B",
                        "B ::= c B",
                        "B ::= " + EPSILON
                )
        );
        arithmeticExpression = new Grammar(
                makeNonTerminals("T", "F"),
                makeTerminals("+", "*", "(", ")", "id"),
                "E",
                makeProductions(
                        "E ::= E + T",
                        "E ::= T",
                        "T ::= T * F",
                        "T ::= F",
                        "F ::= ( E )",
                        "F ::= id"
                )
        );
        augmentedArithmeticExpression = new Grammar(
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
        arithmeticExpressionRedux = new Grammar(
                makeNonTerminals("E'", "T", "T'", "F"),
                makeTerminals("+", EPSILON, "*", "(", ")", "id"),
                "E",
                makeProductions(
                        "E ::= T E'",
                        "E' ::= + T E'",
                        "E' ::= " + EPSILON,
                        "T ::= F T'",
                        "T' ::= * F T'",
                        "T' ::= " + EPSILON,
                        "F ::= ( E )",
                        "F ::= id"
                )
        );
        leftRecursionExample = new Grammar(
                makeNonTerminals("A"),
                makeTerminals("a", "b", "c", "d"),
                "S",
                makeProductions(
                        "S ::= A a",
                        "S ::= b",
                        "A ::= S c",
                        "A ::= d"
                )
        );
    }

    @Test
    void first() {
        // From sample exam 1
        Grammar cfg = firstSampleExamQuestion;
        FirstMap actual = cfg.first();
        FirstMap expected = new FirstMap();
        expected.put("a", new First("a"));
        expected.put("b", new First("b"));
        expected.put("c", new First("c"));
        expected.put(EPSILON, new First(EPSILON));
        expected.put("S", new First("a", "b", "c"));
        expected.put("A", new First("c", "b"));
        expected.put("B", new First("c", EPSILON));
        assertEquals(expected, actual);

        // From lecture slides, LL(1) grammar
        cfg = arithmeticExpressionRedux;
        actual = cfg.first();
        expected = new FirstMap();
        expected.put("(", new First("("));
        expected.put(")", new First(")"));
        expected.put("*", new First("*"));
        expected.put("+", new First("+"));
        expected.put("E", new First("(", "id"));
        expected.put("E'", new First("+", EPSILON));
        expected.put("F", new First("(", "id"));
        expected.put("T", new First("(", "id"));
        expected.put("T'", new First("*", EPSILON));
        expected.put("id", new First("id"));
        expected.put(EPSILON, new First(EPSILON));
        assertEquals(expected, actual);

        // From lecture slides, non LL(1) grammar
        cfg = arithmeticExpression;
        actual = cfg.first();

        expected = new FirstMap();
        expected.put("(", new First("("));
        expected.put(")", new First(")"));
        expected.put("*", new First("*"));
        expected.put("+", new First("+"));
        expected.put("E", new First("(", "id"));
        expected.put("F", new First("(", "id"));
        expected.put("T", new First("(", "id"));
        expected.put("id", new First("id"));
        assertEquals(expected, actual);
    }

    @Test
    void follow() {
        // From sample exam 1
        FollowMap expected = new FollowMap();
        expected.put("S", new Follow(TERMINATOR));
        expected.put("A", new Follow("a"));
        expected.put("B", new Follow("a"));

        Grammar cfg = firstSampleExamQuestion;

        FirstMap firstMap = new FirstMap();
        firstMap.put("a", new First("a"));
        firstMap.put("b", new First("b"));
        firstMap.put("c", new First("c"));
        firstMap.put(EPSILON, new First(EPSILON));
        firstMap.put("S", new First("a", "b", "c"));
        firstMap.put("A", new First("c", "b"));
        firstMap.put("B", new First("c", EPSILON));

        FollowMap actual = cfg.follow(firstMap);
        assertEquals(expected, actual);

        // From lecture slides, LL(1) grammar
        expected = new FollowMap();
        expected.put("E", new Follow(")", TERMINATOR));
        expected.put("E'", new Follow(")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", ")", TERMINATOR));
        expected.put("T'", new Follow("+", ")", TERMINATOR));

        cfg = arithmeticExpressionRedux;

        firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("E'", new First("+", EPSILON));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("T'", new First("*", EPSILON));
        firstMap.put("id", new First("id"));
        firstMap.put(EPSILON, new First(EPSILON));

        actual = cfg.follow(firstMap);
        assertEquals(expected, actual);

        // From lecture slides, non LL(1) grammar
        expected = new FollowMap();
        expected.put("E", new Follow("+", ")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", "*", ")", TERMINATOR));

        cfg = arithmeticExpression;

        firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("id", new First("id"));

        actual = cfg.follow(firstMap);
        assertEquals(expected, actual);
    }

    @Test
    void generateLL1ParseTable() {
        // From sample exam 1
        LL1ParseTable expected = new LL1ParseTable();
        expected.set("S", "a", 5);
        expected.set("S", "b", 4);
        expected.set("S", "c", 4);
        expected.set("A", "b", 0);
        expected.set("A", "c", 1);
        expected.set("B", "a", 3);
        expected.set("B", "c", 2);

        Grammar cfg = firstSampleExamQuestion;

        FirstMap firstMap = new FirstMap();
        firstMap.put("a", new First("a"));
        firstMap.put("b", new First("b"));
        firstMap.put("c", new First("c"));
        firstMap.put(EPSILON, new First(EPSILON));
        firstMap.put("S", new First("a", "b", "c"));
        firstMap.put("A", new First("c", "b"));
        firstMap.put("B", new First("c", EPSILON));

        FollowMap followMap = new FollowMap();
        followMap.put("S", new Follow(TERMINATOR));
        followMap.put("A", new Follow("a"));
        followMap.put("B", new Follow("a"));

        LL1ParseTable actual = cfg.generateLL1ParseTable(firstMap, followMap);
        assertEquals(expected, actual);

        // From lecture slides, LL(1) grammar
        expected = new LL1ParseTable();
        expected.set("E", "(", 0);
        expected.set("E", "id", 0);
        expected.set("E'", "+", 1);
        expected.set("E'", ")", 2);
        expected.set("E'", TERMINATOR, 2);
        expected.set("T", "(", 5);
        expected.set("T", "id", 5);
        expected.set("T'", "+", 7);
        expected.set("T'", "*", 6);
        expected.set("T'", ")", 7);
        expected.set("T'", TERMINATOR, 7);
        expected.set("F", "(", 3);
        expected.set("F", "id", 4);

        cfg = arithmeticExpressionRedux;

        firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("E'", new First("+", EPSILON));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("T'", new First("*", EPSILON));
        firstMap.put("id", new First("id"));
        firstMap.put(EPSILON, new First(EPSILON));

        followMap = new FollowMap();
        followMap.put("E", new Follow(")", TERMINATOR));
        followMap.put("E'", new Follow(")", TERMINATOR));
        followMap.put("F", new Follow("+", "*", ")", TERMINATOR));
        followMap.put("T", new Follow("+", ")", TERMINATOR));
        followMap.put("T'", new Follow("+", ")", TERMINATOR));

        actual = cfg.generateLL1ParseTable(firstMap, followMap);

        assertEquals(expected, actual);

        // From lecture slides, non LL(1) grammar
        expected = new LL1ParseTable();
        expected.set("E", "(", 0);
        expected.set("E", "(", 1);
        expected.set("E", "id", 0);
        expected.set("E", "id", 1);
        expected.set("T", "(", 4);
        expected.set("T", "(", 5);
        expected.set("T", "id", 4);
        expected.set("T", "id", 5);
        expected.set("F", "(", 2);
        expected.set("F", "id", 3);

        cfg = arithmeticExpression;

        firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("id", new First("id"));

        followMap = new FollowMap();
        followMap.put("E", new Follow("+", ")", TERMINATOR));
        followMap.put("F", new Follow("+", "*", ")", TERMINATOR));
        followMap.put("T", new Follow("+", "*", ")", TERMINATOR));

        actual = cfg.generateLL1ParseTable(firstMap, followMap);

        assertEquals(expected, actual);
    }

    @Test
    void parseSentence() throws Exception {
        /* In contrast to the table from the lecture slides, this test checks for entries
        where EPSILON is on the stack */
        LL1ParseOutput expected = new LL1ParseOutput();
        expected.addAll(Arrays.asList(
                makeEntry(
                        makeStack(TERMINATOR, "E"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        0),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        5),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        4),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        7),
                makeEntry(
                        makeStack(TERMINATOR, "E'"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T", "+"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        1),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        5),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        4),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F", "*"),
                        makeQueue("*", "id", TERMINATOR),
                        6),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", TERMINATOR),
                        4),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue(TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue(TERMINATOR),
                        7),
                makeEntry(
                        makeStack(TERMINATOR, "E'"),
                        makeQueue(TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, EPSILON),
                        makeQueue(TERMINATOR),
                        2),
                makeEntry(
                        makeStack(TERMINATOR),
                        makeQueue(TERMINATOR),
                        null)
        ));

        LL1ParseTable table = new LL1ParseTable();
        table.set("E", "(", 0);
        table.set("E", "id", 0);
        table.set("E'", "+", 1);
        table.set("E'", ")", 2);
        table.set("E'", TERMINATOR, 2);
        table.set("T", "(", 5);
        table.set("T", "id", 5);
        table.set("T'", "+", 7);
        table.set("T'", "*", 6);
        table.set("T'", ")", 7);
        table.set("T'", TERMINATOR, 7);
        table.set("F", "(", 3);
        table.set("F", "id", 4);

        Grammar cfg = arithmeticExpressionRedux;
        String w = "id + id * id " + TERMINATOR;
        LL1ParseOutput actual = cfg.parseSentence(table, w);
        assertEquals(expected, actual);
    }

    @Test
    void isLL1() {
        // Non LL(1) grammar with left recursion
        Grammar cfg = arithmeticExpression;
        LL1ParseTable table = new LL1ParseTable();
        table.set("E", "(", 0);
        table.set("E", "(", 1);
        table.set("E", "id", 0);
        table.set("E", "id", 1);
        table.set("T", "(", 2);
        table.set("T", "(", 3);
        table.set("T", "id", 2);
        table.set("T", "id", 3);
        table.set("F", "(", 4);
        table.set("F", "id", 5);

        assertFalse(cfg.isLL1(table));

        // Equivalent LL(1) grammar
        cfg = arithmeticExpressionRedux;
        table = new LL1ParseTable();
        table.set("E", "(", 0);
        table.set("E", "id", 0);
        table.set("E'", "+", 1);
        table.set("E'", ")", 2);
        table.set("E'", TERMINATOR, 2);
        table.set("T", "(", 3);
        table.set("T", "id", 3);
        table.set("T'", "+", 5);
        table.set("T'", "*", 4);
        table.set("T'", ")", 5);
        table.set("T'", TERMINATOR, 5);
        table.set("F", "(", 6);
        table.set("F", "id", 7);

        assertTrue(cfg.isLL1(table));
    }

    @Test
    void removeLeftRecursion() {
        // Textbook example with direct and indirect left recursion
        Grammar expected = new Grammar(
                makeNonTerminals("A", "A'"),
                makeTerminals("a", "b", "c", "d", EPSILON),
                "S",
                makeProductions(
                        "S ::= A a",
                        "S ::= b",
                        "A ::= b c A'",
                        "A ::= d A'",
                        "A' ::= a c A'",
                        "A' ::= " + EPSILON
                )
        );
        Grammar cfg = leftRecursionExample;
        Grammar actual = cfg.removeLeftRecursion();
        assertEquals(expected, actual);

        // From lecture slides, non LL(1) grammar with left recursion removed
        expected = new Grammar(
                makeNonTerminals("E'", "T", "T'", "F"),
                makeTerminals("+", EPSILON, "*", "(", ")", "id"),
                "E",
                makeProductions(
                        "E ::= T E'",
                        "E' ::= + T E'",
                        "E' ::= " + EPSILON,
                        "T ::= ( E ) T'",
                        "T ::= id T'",
                        "T' ::= * F T'",
                        "T' ::= " + EPSILON,
                        "F ::= ( E )",
                        "F ::= id"
                )
        );
        cfg = arithmeticExpression;
        actual = cfg.removeLeftRecursion();
        assertEquals(expected, actual);
    }

    @Test
    void deepClone() {
        Grammar expected = arithmeticExpressionRedux;
        Grammar actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }

    @Test
    void augment() {
        Grammar expected = augmentedArithmeticExpression;
        Grammar actual = arithmeticExpression.augment();
        assertEquals(expected, actual);
    }

    @Test
    void computeLR1Collection() {
//        FirstMap firstMap = new FirstMap();
//        firstMap.put("(", new First("("));
//        firstMap.put(")", new First(")"));
//        firstMap.put("*", new First("*"));
//        firstMap.put("+", new First("+"));
//        firstMap.put("E'", new First("(", "id"));
//        firstMap.put("E", new First("(", "id"));
//        firstMap.put("F", new First("(", "id"));
//        firstMap.put("T", new First("(", "id"));
//        firstMap.put("id", new First("id"));

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

        Items s2 = makeItems(
                "[E ::= T " + MARKER + ", " + TERMINATOR + "/+]",
                "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
        );

        Items s3 = makeItems("[T ::= F " + MARKER + ", " + TERMINATOR + "/+/*]");

        Items s4 = makeItems(
                "[F ::= ( " + MARKER + " E ), " + TERMINATOR + "/+/*]",
                "[E ::= " + MARKER + " E + T, +/)]",
                "[E ::= " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        Items s5 = makeItems("[F ::= id " + MARKER + ", " + TERMINATOR + "/+/*]");

        Items s6 = makeItems(
                "[E ::= E + " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        Items s7 = makeItems(
                "[T ::= T * " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        Items s8 = makeItems(
                "[F ::= ( E " + MARKER + " ), " + TERMINATOR + "/+/*]",
                "[E ::= E " + MARKER + " + T, +/)]"
        );

        Items s9 = makeItems(
                "[E ::= T " + MARKER + ", +/)]",
                "[T ::= T " + MARKER + " * F, +/*/)]"
        );

        Items s10 = makeItems("[T ::= F " + MARKER + ", +/*/)]");

        Items s11 = makeItems(
                "[F ::= ( " + MARKER + " E ), +/*/)]",
                "[E ::= " + MARKER + " E + T, +/)]",
                "[E ::= " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        Items s12 = makeItems("[F ::= id " + MARKER + ", +/*/)]");

        Items s13 = makeItems(
                "[E ::= E + T " + MARKER + ", " + TERMINATOR + "/+]",
                "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
        );

        Items s14 = makeItems("[T ::= T * F " + MARKER + ", " + TERMINATOR + "/+/*]");

        Items s15 = makeItems("[F ::= ( E ) " + MARKER + ", " + TERMINATOR + "/+/*]");

        Items s16 = makeItems(
                "[E ::= E + " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        Items s17 = makeItems(
                "[T ::= T * " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        Items s18 = makeItems(
                "[F ::= ( E " + MARKER + " ), +/*/)]",
                "[E ::= E " + MARKER + " + T, +/)]"
        );

        Items s19 = makeItems(
                "[E ::= E + T " + MARKER + ", +/)]",
                "[T ::= T " + MARKER + " * F, +/*/)]"
        );

        Items s20 = makeItems("[T ::= T * F " + MARKER + ", +/*/)]");

        Items s21 = makeItems("[F ::= ( E ) " + MARKER + ", +/*/)]");

        GotoMap gotoMap = new GotoMap();
        gotoMap.put(s1, makeGoto(s0, "E"));
        gotoMap.put(s2, makeGoto(s0, "T"));
        gotoMap.put(s3, makeGoto(s0, "F"));
        gotoMap.put(s4, makeGoto(s0, "("));
        gotoMap.put(s5, makeGoto(s0, "id"));

        gotoMap.put(s6, makeGoto(s1, "+"));

        gotoMap.put(s7, makeGoto(s2, "*"));

        gotoMap.put(s8, makeGoto(s4, "E"));
        gotoMap.put(s9, makeGoto(s4, "T"));
        gotoMap.put(s10, makeGoto(s4, "F"));
        gotoMap.put(s11, makeGoto(s4, "("));
        gotoMap.put(s12, makeGoto(s4, "id"));

        gotoMap.put(s13, makeGoto(s6, "T"));
//        gotoMap.put(s3, makeGoto(s6, "F"));
//        gotoMap.put(s4, makeGoto(s6, "("));
//        gotoMap.put(s5, makeGoto(s6, "id"));

        gotoMap.put(s14, makeGoto(s7, "F"));
//        gotoMap.put(s4, makeGoto(s7, "("));
//        gotoMap.put(s5, makeGoto(s7, "id"));

        gotoMap.put(s15, makeGoto(s8, ")"));
        gotoMap.put(s16, makeGoto(s8, "+"));

        gotoMap.put(s17, makeGoto(s9, "*"));

        gotoMap.put(s18, makeGoto(s11, "E"));
//        gotoMap.put(s9, makeGoto(s11, "T"));
//        gotoMap.put(s10, makeGoto(s11, "F"));
//        gotoMap.put(s11, makeGoto(s11, "("));
//        gotoMap.put(s12, makeGoto(s11, "id"));

//        gotoMap.put(s7, makeGoto(s13, "*"));

        gotoMap.put(s19, makeGoto(s16, "T"));
//        gotoMap.put(s10, makeGoto(s16, "F"));
//        gotoMap.put(s11, makeGoto(s16, "("));
//        gotoMap.put(s12, makeGoto(s16, "id"));

        gotoMap.put(s20, makeGoto(s17, "F"));
//        gotoMap.put(s11, makeGoto(s17, "("));
//        gotoMap.put(s12, makeGoto(s17, "id"));

        gotoMap.put(s21, makeGoto(s18, ")"));
//        gotoMap.put(s16, makeGoto(s18, "+"));

//        gotoMap.put(s17, makeGoto(s19, "*"));

        LR1Collection expected = new LR1Collection(
                Arrays.asList(
                        s0, s1, s2, s3, s4, s5, s6, s7,
                        s8, s9, s10, s11, s12, s13, s14,
                        s15, s16, s17, s18, s19, s20, s21
                ),
                gotoMap
        );

        LR1Collection actual = arithmeticExpression.computeLR1Collection();

        assertEquals(expected, actual);
    }
}