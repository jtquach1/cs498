package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.ActionTable.noSuchState;
import static algorithms.Execution.*;
import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Pair.noSuchSymbol;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {
    Grammar arithmeticExpression, augmentedArithmeticExpression, arithmeticExpressionRedux,
            firstSampleExamQuestion, leftRecursionExample;

    Items s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18,
            s19, s20, s21;

    Transitions transitions;
    ActionTable actionTable;
    GotoTable gotoTable;
    LR1Collection collection;
    LR1ParseTable table;

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

        s2 = makeItems(
                "[E ::= T " + MARKER + ", " + TERMINATOR + "/+]",
                "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
        );

        s3 = makeItems("[T ::= F " + MARKER + ", " + TERMINATOR + "/+/*]");

        s4 = makeItems(
                "[F ::= ( " + MARKER + " E ), " + TERMINATOR + "/+/*]",
                "[E ::= " + MARKER + " E + T, +/)]",
                "[E ::= " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        s5 = makeItems("[F ::= id " + MARKER + ", " + TERMINATOR + "/+/*]");

        s6 = makeItems(
                "[E ::= E + " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        s7 = makeItems(
                "[T ::= T * " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        s8 = makeItems(
                "[F ::= ( E " + MARKER + " ), " + TERMINATOR + "/+/*]",
                "[E ::= E " + MARKER + " + T, +/)]"
        );

        s9 = makeItems(
                "[E ::= T " + MARKER + ", +/)]",
                "[T ::= T " + MARKER + " * F, +/*/)]"
        );

        s10 = makeItems("[T ::= F " + MARKER + ", +/*/)]");

        s11 = makeItems(
                "[F ::= ( " + MARKER + " E ), +/*/)]",
                "[E ::= " + MARKER + " E + T, +/)]",
                "[E ::= " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        s12 = makeItems("[F ::= id " + MARKER + ", +/*/)]");

        s13 = makeItems(
                "[E ::= E + T " + MARKER + ", " + TERMINATOR + "/+]",
                "[T ::= T " + MARKER + " * F, " + TERMINATOR + "/+/*]"
        );

        s14 = makeItems("[T ::= T * F " + MARKER + ", " + TERMINATOR + "/+/*]");

        s15 = makeItems("[F ::= ( E ) " + MARKER + ", " + TERMINATOR + "/+/*]");

        s16 = makeItems(
                "[E ::= E + " + MARKER + " T, +/)]",
                "[T ::= " + MARKER + " T * F, +/*/)]",
                "[T ::= " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        s17 = makeItems(
                "[T ::= T * " + MARKER + " F, +/*/)]",
                "[F ::= " + MARKER + " ( E ), +/*/)]",
                "[F ::= " + MARKER + " id, +/*/)]"
        );

        s18 = makeItems(
                "[F ::= ( E " + MARKER + " ), +/*/)]",
                "[E ::= E " + MARKER + " + T, +/)]"
        );

        s19 = makeItems(
                "[E ::= E + T " + MARKER + ", +/)]",
                "[T ::= T " + MARKER + " * F, +/*/)]"
        );

        s20 = makeItems("[T ::= T * F " + MARKER + ", +/*/)]");

        s21 = makeItems("[F ::= ( E ) " + MARKER + ", +/*/)]");

        transitions = new Transitions();
        transitions.add(makeTransition(s0, "E", s1));
        transitions.add(makeTransition(s0, "T", s2));
        transitions.add(makeTransition(s0, "F", s3));
        transitions.add(makeTransition(s0, "(", s4));
        transitions.add(makeTransition(s0, "id", s5));

        transitions.add(makeTransition(s1, "+", s6));

        transitions.add(makeTransition(s2, "*", s7));

        transitions.add(makeTransition(s4, "E", s8));
        transitions.add(makeTransition(s4, "T", s9));
        transitions.add(makeTransition(s4, "F", s10));
        transitions.add(makeTransition(s4, "(", s11));
        transitions.add(makeTransition(s4, "id", s12));

        transitions.add(makeTransition(s6, "T", s13));
        transitions.add(makeTransition(s6, "F", s3));
        transitions.add(makeTransition(s6, "(", s4));
        transitions.add(makeTransition(s6, "id", s5));

        transitions.add(makeTransition(s7, "F", s14));
        transitions.add(makeTransition(s7, "(", s4));
        transitions.add(makeTransition(s7, "id", s5));

        transitions.add(makeTransition(s8, ")", s15));
        transitions.add(makeTransition(s8, "+", s16));

        transitions.add(makeTransition(s9, "*", s17));

        transitions.add(makeTransition(s11, "E", s18));
        transitions.add(makeTransition(s11, "T", s9));
        transitions.add(makeTransition(s11, "F", s10));
        transitions.add(makeTransition(s11, "(", s11));
        transitions.add(makeTransition(s11, "id", s12));

        transitions.add(makeTransition(s13, "*", s7));

        transitions.add(makeTransition(s16, "T", s19));
        transitions.add(makeTransition(s16, "F", s10));
        transitions.add(makeTransition(s16, "(", s11));
        transitions.add(makeTransition(s16, "id", s12));

        transitions.add(makeTransition(s17, "F", s20));
        transitions.add(makeTransition(s17, "(", s11));
        transitions.add(makeTransition(s17, "id", s12));

        transitions.add(makeTransition(s18, ")", s21));
        transitions.add(makeTransition(s18, "+", s16));

        transitions.add(makeTransition(s19, "*", s17));

        actionTable = new ActionTable();
        actionTable.set(9, "(", makeAction(SHIFT, 10));
        actionTable.set(9, "id", makeAction(SHIFT, 14));

        actionTable.set(4, "+", makeAction(SHIFT, 2));
        actionTable.set(4, TERMINATOR, makeAction(ACCEPT, noSuchState));

        actionTable.set(7, "+", makeAction(REDUCE, 1));
        actionTable.set(7, "*", makeAction(SHIFT, 16));
        actionTable.set(7, TERMINATOR, makeAction(REDUCE, 1));

        actionTable.set(18, "+", makeAction(REDUCE, 4));
        actionTable.set(18, "*", makeAction(REDUCE, 4));
        actionTable.set(18, TERMINATOR, makeAction(REDUCE, 4));

        actionTable.set(10, "(", makeAction(SHIFT, 11));
        actionTable.set(10, "id", makeAction(SHIFT, 15));

        actionTable.set(14, "+", makeAction(REDUCE, 3));
        actionTable.set(14, "*", makeAction(REDUCE, 3));
        actionTable.set(14, TERMINATOR, makeAction(REDUCE, 3));

        actionTable.set(2, "(", makeAction(SHIFT, 10));
        actionTable.set(2, "id", makeAction(SHIFT, 14));

        actionTable.set(16, "(", makeAction(SHIFT, 10));
        actionTable.set(16, "id", makeAction(SHIFT, 14));

        actionTable.set(5, "+", makeAction(SHIFT, 3));
        actionTable.set(5, ")", makeAction(SHIFT, 12));

        actionTable.set(8, "+", makeAction(REDUCE, 1));
        actionTable.set(8, "*", makeAction(SHIFT, 17));
        actionTable.set(8, ")", makeAction(REDUCE, 1));

        actionTable.set(19, "+", makeAction(REDUCE, 4));
        actionTable.set(19, "*", makeAction(REDUCE, 4));
        actionTable.set(19, ")", makeAction(REDUCE, 4));

        actionTable.set(11, "(", makeAction(SHIFT, 11));
        actionTable.set(11, "id", makeAction(SHIFT, 15));

        actionTable.set(15, "+", makeAction(REDUCE, 3));
        actionTable.set(15, "*", makeAction(REDUCE, 3));
        actionTable.set(15, ")", makeAction(REDUCE, 3));

        actionTable.set(0, "+", makeAction(REDUCE, 0));
        actionTable.set(0, "*", makeAction(SHIFT, 16));
        actionTable.set(0, TERMINATOR, makeAction(REDUCE, 0));

        actionTable.set(20, "+", makeAction(REDUCE, 5));
        actionTable.set(20, "*", makeAction(REDUCE, 5));
        actionTable.set(20, TERMINATOR, makeAction(REDUCE, 5));

        actionTable.set(12, "+", makeAction(REDUCE, 2));
        actionTable.set(12, "*", makeAction(REDUCE, 2));
        actionTable.set(12, TERMINATOR, makeAction(REDUCE, 2));

        actionTable.set(3, "(", makeAction(SHIFT, 11));
        actionTable.set(3, "id", makeAction(SHIFT, 15));

        actionTable.set(17, "(", makeAction(SHIFT, 11));
        actionTable.set(17, "id", makeAction(SHIFT, 15));

        actionTable.set(6, "+", makeAction(SHIFT, 3));
        actionTable.set(6, ")", makeAction(SHIFT, 13));

        actionTable.set(1, "+", makeAction(REDUCE, 0));
        actionTable.set(1, "*", makeAction(SHIFT, 17));
        actionTable.set(1, ")", makeAction(REDUCE, 0));

        actionTable.set(21, "+", makeAction(REDUCE, 5));
        actionTable.set(21, "*", makeAction(REDUCE, 5));
        actionTable.set(21, ")", makeAction(REDUCE, 5));

        actionTable.set(13, "+", makeAction(REDUCE, 2));
        actionTable.set(13, "*", makeAction(REDUCE, 2));
        actionTable.set(13, ")", makeAction(REDUCE, 2));

        gotoTable = new GotoTable();
        gotoTable.set(9, "E", 4);
        gotoTable.set(9, "T", 7);
        gotoTable.set(9, "F", 18);

        gotoTable.set(10, "E", 5);
        gotoTable.set(10, "T", 8);
        gotoTable.set(10, "F", 19);

        gotoTable.set(2, "T", 0);
        gotoTable.set(2, "F", 18);

        gotoTable.set(16, "F", 20);

        gotoTable.set(11, "E", 6);
        gotoTable.set(11, "T", 8);
        gotoTable.set(11, "F", 19);

        gotoTable.set(3, "T", 1);
        gotoTable.set(3, "F", 19);

        gotoTable.set(17, "F", 21);

        collection = new LR1Collection(
                Arrays.asList(
                        s0, s1, s2, s3, s4, s5, s6, s7,
                        s8, s9, s10, s11, s12, s13, s14,
                        s15, s16, s17, s18, s19, s20, s21
                ),
                transitions,
                s0
        );

        table = new LR1ParseTable(actionTable, gotoTable, 9);
    }

    @Test
    void first() {
        // From sample exam 1
        FirstMap actual = firstSampleExamQuestion.first();
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
        actual = arithmeticExpressionRedux.first();
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
        actual = arithmeticExpression.first();

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

        FirstMap firstMap = new FirstMap();
        firstMap.put("a", new First("a"));
        firstMap.put("b", new First("b"));
        firstMap.put("c", new First("c"));
        firstMap.put(EPSILON, new First(EPSILON));
        firstMap.put("S", new First("a", "b", "c"));
        firstMap.put("A", new First("c", "b"));
        firstMap.put("B", new First("c", EPSILON));

        FollowMap actual = firstSampleExamQuestion.follow(firstMap);
        assertEquals(expected, actual);

        // From lecture slides, LL(1) grammar
        expected = new FollowMap();
        expected.put("E", new Follow(")", TERMINATOR));
        expected.put("E'", new Follow(")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", ")", TERMINATOR));
        expected.put("T'", new Follow("+", ")", TERMINATOR));

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

        actual = arithmeticExpressionRedux.follow(firstMap);
        assertEquals(expected, actual);

        // From lecture slides, non LL(1) grammar
        expected = new FollowMap();
        expected.put("E", new Follow("+", ")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", "*", ")", TERMINATOR));

        firstMap = new FirstMap();
        firstMap.put("(", new First("("));
        firstMap.put(")", new First(")"));
        firstMap.put("*", new First("*"));
        firstMap.put("+", new First("+"));
        firstMap.put("E", new First("(", "id"));
        firstMap.put("F", new First("(", "id"));
        firstMap.put("T", new First("(", "id"));
        firstMap.put("id", new First("id"));

        actual = arithmeticExpression.follow(firstMap);
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

        LL1ParseTable actual = firstSampleExamQuestion.generateLL1ParseTable(firstMap, followMap);
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

        actual = arithmeticExpressionRedux.generateLL1ParseTable(firstMap, followMap);

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

        actual = arithmeticExpression.generateLL1ParseTable(firstMap, followMap);

        assertEquals(expected, actual);
    }

    @Test
    void parseSentenceLL1() throws Exception {
        /* In contrast to the table from the lecture slides, this test checks for entries
        where EPSILON is on the stack */
        LL1ParseOutput expected = new LL1ParseOutput();
        expected.addAll(Arrays.asList(
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        0),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        5),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        4),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        7),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T", "+"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        1),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        5),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        4),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("*", "id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F", "*"),
                        makeQueue("*", "id", TERMINATOR),
                        6),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", TERMINATOR),
                        4),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue(TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue(TERMINATOR),
                        7),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, "E'"),
                        makeQueue(TERMINATOR),
                        null),
                makeLL1ParseOutputEntry(
                        makeStack(TERMINATOR, EPSILON),
                        makeQueue(TERMINATOR),
                        2),
                makeLL1ParseOutputEntry(
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

        String w = "id + id * id " + TERMINATOR;
        LL1ParseOutput actual = arithmeticExpressionRedux.parseSentence(table, w);
        assertEquals(expected, actual);
    }

    @Test
    void isLL1() {
        // Non LL(1) grammar with left recursion
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

        assertFalse(arithmeticExpression.isLL1(table));

        // Equivalent LL(1) grammar
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

        assertTrue(arithmeticExpressionRedux.isLL1(table));
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
        Grammar actual = leftRecursionExample.removeLeftRecursion();
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
        actual = arithmeticExpression.removeLeftRecursion();
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
        assertEquals(augmentedArithmeticExpression, arithmeticExpression.augment());
    }

    @Test
    void computeLR1Collection() {
        assertEquals(collection, arithmeticExpression.computeLR1Collection());
    }

    @Test
    void generateLR1ParseTable() {
        assertEquals(table, arithmeticExpression.generateLR1ParseTable(collection));
    }

    @Test
    void constructActionTable() {
        assertEquals(actionTable, arithmeticExpression.constructActionTable(collection));
    }

    @Test
    void constructGotoTable() {
        assertEquals(gotoTable, arithmeticExpression.constructGotoTable(collection));
    }

    @Test
    void parseSentenceLR1() throws Exception {
        LR1ParseOutput expected = new LR1ParseOutput();
        expected.addAll(Arrays.asList(
                makeLR1ParseOutputEntry(
                        makeStack(makePair(noSuchSymbol, 9)),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        makeAction(SHIFT, 14)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("id", 14)),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        makeAction(REDUCE, 3)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("F", 18)),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        makeAction(REDUCE, 4)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("T", 7)),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        makeAction(REDUCE, 1)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4)),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        makeAction(SHIFT, 2)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2)),
                        makeQueue("id", "*", "id", TERMINATOR),
                        makeAction(SHIFT, 14)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("id", 14)),
                        makeQueue("*", "id", TERMINATOR),
                        makeAction(REDUCE, 3)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("F", 18)),
                        makeQueue("*", "id", TERMINATOR),
                        makeAction(REDUCE, 4)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("T", 0)),
                        makeQueue("*", "id", TERMINATOR),
                        makeAction(SHIFT, 16)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("T", 0),
                                makePair("*", 16)),
                        makeQueue("id", TERMINATOR),
                        makeAction(SHIFT, 14)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("T", 0),
                                makePair("*", 16),
                                makePair("id", 14)),
                        makeQueue(TERMINATOR),
                        makeAction(REDUCE, 3)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("T", 0),
                                makePair("*", 16),
                                makePair("F", 20)),
                        makeQueue(TERMINATOR),
                        makeAction(REDUCE, 5)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4),
                                makePair("+", 2),
                                makePair("T", 0)),
                        makeQueue(TERMINATOR),
                        makeAction(REDUCE, 0)),
                makeLR1ParseOutputEntry(
                        makeStack(
                                makePair(noSuchSymbol, 9),
                                makePair("E", 4)),
                        makeQueue(TERMINATOR),
                        makeAction(ACCEPT, noSuchState))
                )
        );

        LR1ParseTable table = new LR1ParseTable(actionTable, gotoTable, 9);
        String w = "id + id * id " + TERMINATOR;
        LR1ParseOutput actual = arithmeticExpression.parseSentence(table, w);

        assertEquals(expected, actual);
    }
}