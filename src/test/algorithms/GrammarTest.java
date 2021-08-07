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
    static Grammar sampleExamProblem3;               // From Spring 2019 sample exam
    static Grammar arithmeticExpressionRedux;        // From lecture slides, is LL(1)
    static Grammar arithmeticExpression;             // From lecture slides, not LL(1)
    static Grammar augmentedArithmeticExpression;    // From lecture slides, not LL(1)
    static Grammar sampleExamProblem4;               // From Spring 2019 sample exam
    static Grammar directAndIndirectLeftRecursion;   // Textbook example
    static Grammar ambiguousArithmeticExpression;    // From lecture slides, neither LL(1) nor LR(1)
    static Grammar danglingElseProblem;              // From lecture slides, not LR(1)
    static Grammar tinyAmbiguousExpression;          // Neither LL(1) nor LR(1)

    static FirstMap sampleExamProblem3FirstMap;
    static FirstMap arithmeticExpressionReduxFirstMap;
    static FirstMap arithmeticExpressionFirstMap;
    static FirstMap ambiguousArithmeticExpressionFirstMap;
    static FirstMap danglingElseProblemFirstMap;
    static FirstMap tinyAmbiguousExpressionFirstMap;
    static FollowMap sampleExamProblem3FollowMap;
    static FollowMap arithmeticExpressionReduxFollowMap;
    static FollowMap arithmeticExpressionFollowMap;
    static FollowMap ambiguousArithmeticExpressionFollowMap;
    static FollowMap danglingElseProblemFollowMap;
    static FollowMap tinyAmbiguousExpressionFollowMap;
    static LL1ParseTable sampleExamProblem3LL1ParseTable;
    static LL1ParseTable arithmeticExpressionReduxLL1ParseTable;
    static LL1ParseTable arithmeticExpressionLL1ParseTable;
    static LL1ParseTable ambiguousArithmeticExpressionLL1ParseTable;
    static LL1ParseTable danglingElseProblemLL1ParseTable;
    static LL1ParseTable tinyAmbiguousExpressionLL1ParseTable;

    static Items s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17,
            s18, s19, s20, s21;         // For arithmetic expression
    static Items a0, a1, a2, a3, a4;    // For tiny ambiguous expression

    static Transitions arithmeticExpressionTransitions;
    static Transitions tinyAmbiguousExpressionTransitions;
    static LR1Collection arithmeticExpressionCollection;
    static LR1Collection tinyAmbiguousExpressionLR1Collection;
    static ActionTable sampleExamProblem4ActionTable;
    static ActionTable arithmeticExpressionActionTable;
    static ActionTable tinyAmbiguousExpressionActionTable;
    static GotoTable sampleExamProblem4GotoTable;
    static GotoTable arithmeticExpressionGotoTable;
    static GotoTable tinyAmbiguousExpressionGotoTable;
    static LR1ParseTable sampleExamProblem4LR1ParseTable;
    static LR1ParseTable arithmeticExpressionLR1ParseTable;
    static LR1ParseTable tinyAmbiguousExpressionLR1ParseTable;

    @BeforeEach
    void setUp() {
        sampleExamProblem3 = new Grammar(
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

        sampleExamProblem4 = new Grammar(
                makeNonTerminals("X", "Y", "Z"),
                makeTerminals("+", "a", "="),
                "X",
                makeProductions(
                        "X ::= Y = Z",
                        "X ::= Z",
                        "Y ::= + Z",
                        "Y ::= a",
                        "Z ::= Y"
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

        directAndIndirectLeftRecursion = new Grammar(
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

        ambiguousArithmeticExpression = new Grammar(
                makeNonTerminals(),
                makeTerminals("+", "*", "(", ")", "id"),
                "E",
                makeProductions(
                        "E ::= E + E",
                        "E ::= E * E",
                        "E ::= ( E )",
                        "E ::= id"
                )
        );

        danglingElseProblem = new Grammar(
                makeNonTerminals("E"),
                makeTerminals("if", "(", ")", "else", "s", "e"),
                "S",
                makeProductions(
                        "S ::= if ( E ) S",
                        "S ::= if ( E ) S else S",
                        "S ::= s",
                        "E ::= e"
                )
        );

        tinyAmbiguousExpression = new Grammar(
                makeNonTerminals(),
                makeTerminals("id"),
                "E",
                makeProductions(
                        "E ::= E E",
                        "E ::= E id",
                        "E ::= id"
                )
        );

        sampleExamProblem3FirstMap = new FirstMap();
        sampleExamProblem3FirstMap.put("a", makeFirst("a"));
        sampleExamProblem3FirstMap.put("b", makeFirst("b"));
        sampleExamProblem3FirstMap.put("c", makeFirst("c"));
        sampleExamProblem3FirstMap.put(EPSILON, makeFirst(EPSILON));
        sampleExamProblem3FirstMap.put("S", makeFirst("a", "b", "c"));
        sampleExamProblem3FirstMap.put("A", makeFirst("c", "b"));
        sampleExamProblem3FirstMap.put("B", makeFirst("c", EPSILON));

        arithmeticExpressionReduxFirstMap = new FirstMap();
        arithmeticExpressionReduxFirstMap.put("(", makeFirst("("));
        arithmeticExpressionReduxFirstMap.put(")", makeFirst(")"));
        arithmeticExpressionReduxFirstMap.put("*", makeFirst("*"));
        arithmeticExpressionReduxFirstMap.put("+", makeFirst("+"));
        arithmeticExpressionReduxFirstMap.put("E", makeFirst("(", "id"));
        arithmeticExpressionReduxFirstMap.put("E'", makeFirst("+", EPSILON));
        arithmeticExpressionReduxFirstMap.put("F", makeFirst("(", "id"));
        arithmeticExpressionReduxFirstMap.put("T", makeFirst("(", "id"));
        arithmeticExpressionReduxFirstMap.put("T'", makeFirst("*", EPSILON));
        arithmeticExpressionReduxFirstMap.put("id", makeFirst("id"));
        arithmeticExpressionReduxFirstMap.put(EPSILON, makeFirst(EPSILON));

        arithmeticExpressionFirstMap = new FirstMap();
        arithmeticExpressionFirstMap.put("(", makeFirst("("));
        arithmeticExpressionFirstMap.put(")", makeFirst(")"));
        arithmeticExpressionFirstMap.put("*", makeFirst("*"));
        arithmeticExpressionFirstMap.put("+", makeFirst("+"));
        arithmeticExpressionFirstMap.put("E", makeFirst("(", "id"));
        arithmeticExpressionFirstMap.put("F", makeFirst("(", "id"));
        arithmeticExpressionFirstMap.put("T", makeFirst("(", "id"));
        arithmeticExpressionFirstMap.put("id", makeFirst("id"));

        ambiguousArithmeticExpressionFirstMap = new FirstMap();
        ambiguousArithmeticExpressionFirstMap.put("(", makeFirst("("));
        ambiguousArithmeticExpressionFirstMap.put(")", makeFirst(")"));
        ambiguousArithmeticExpressionFirstMap.put("*", makeFirst("*"));
        ambiguousArithmeticExpressionFirstMap.put("+", makeFirst("+"));
        ambiguousArithmeticExpressionFirstMap.put("E", makeFirst("(", "id"));
        ambiguousArithmeticExpressionFirstMap.put("id", makeFirst("id"));

        danglingElseProblemFirstMap = new FirstMap();
        danglingElseProblemFirstMap.put("(", makeFirst("("));
        danglingElseProblemFirstMap.put(")", makeFirst(")"));
        danglingElseProblemFirstMap.put("E", makeFirst("e"));
        danglingElseProblemFirstMap.put("S", makeFirst("if", "s"));
        danglingElseProblemFirstMap.put("e", makeFirst("e"));
        danglingElseProblemFirstMap.put("else", makeFirst("else"));
        danglingElseProblemFirstMap.put("if", makeFirst("if"));
        danglingElseProblemFirstMap.put("s", makeFirst("s"));

        tinyAmbiguousExpressionFirstMap = new FirstMap();
        tinyAmbiguousExpressionFirstMap.put("E", makeFirst("id"));
        tinyAmbiguousExpressionFirstMap.put("id", makeFirst("id"));

        sampleExamProblem3FollowMap = new FollowMap();
        sampleExamProblem3FollowMap.put("S", makeFollow(TERMINATOR));
        sampleExamProblem3FollowMap.put("A", makeFollow("a"));
        sampleExamProblem3FollowMap.put("B", makeFollow("a"));

        arithmeticExpressionReduxFollowMap = new FollowMap();
        arithmeticExpressionReduxFollowMap.put("E", makeFollow(")", TERMINATOR));
        arithmeticExpressionReduxFollowMap.put("E'", makeFollow(")", TERMINATOR));
        arithmeticExpressionReduxFollowMap.put("F", makeFollow("+", "*", ")", TERMINATOR));
        arithmeticExpressionReduxFollowMap.put("T", makeFollow("+", ")", TERMINATOR));
        arithmeticExpressionReduxFollowMap.put("T'", makeFollow("+", ")", TERMINATOR));

        arithmeticExpressionFollowMap = new FollowMap();
        arithmeticExpressionFollowMap.put("E", makeFollow("+", ")", TERMINATOR));
        arithmeticExpressionFollowMap.put("F", makeFollow("+", "*", ")", TERMINATOR));
        arithmeticExpressionFollowMap.put("T", makeFollow("+", "*", ")", TERMINATOR));

        ambiguousArithmeticExpressionFollowMap = new FollowMap();
        ambiguousArithmeticExpressionFollowMap.put("E", makeFollow(TERMINATOR, ")", "*", "+"));

        danglingElseProblemFollowMap = new FollowMap();
        danglingElseProblemFollowMap.put("E", makeFollow(")"));
        danglingElseProblemFollowMap.put("S", makeFollow(TERMINATOR, "else"));

        tinyAmbiguousExpressionFollowMap = new FollowMap();
        tinyAmbiguousExpressionFollowMap.put("E", makeFollow(TERMINATOR, "id"));

        sampleExamProblem3LL1ParseTable = new LL1ParseTable();
        sampleExamProblem3LL1ParseTable.set("S", "a", 5);
        sampleExamProblem3LL1ParseTable.set("S", "b", 4);
        sampleExamProblem3LL1ParseTable.set("S", "c", 4);
        sampleExamProblem3LL1ParseTable.set("A", "b", 0);
        sampleExamProblem3LL1ParseTable.set("A", "c", 1);
        sampleExamProblem3LL1ParseTable.set("B", "a", 3);
        sampleExamProblem3LL1ParseTable.set("B", "c", 2);

        arithmeticExpressionReduxLL1ParseTable = new LL1ParseTable();
        arithmeticExpressionReduxLL1ParseTable.set("E", "(", 0);
        arithmeticExpressionReduxLL1ParseTable.set("E", "id", 0);
        arithmeticExpressionReduxLL1ParseTable.set("E'", "+", 1);
        arithmeticExpressionReduxLL1ParseTable.set("E'", ")", 2);
        arithmeticExpressionReduxLL1ParseTable.set("E'", TERMINATOR, 2);
        arithmeticExpressionReduxLL1ParseTable.set("T", "(", 5);
        arithmeticExpressionReduxLL1ParseTable.set("T", "id", 5);
        arithmeticExpressionReduxLL1ParseTable.set("T'", "+", 7);
        arithmeticExpressionReduxLL1ParseTable.set("T'", "*", 6);
        arithmeticExpressionReduxLL1ParseTable.set("T'", ")", 7);
        arithmeticExpressionReduxLL1ParseTable.set("T'", TERMINATOR, 7);
        arithmeticExpressionReduxLL1ParseTable.set("F", "(", 3);
        arithmeticExpressionReduxLL1ParseTable.set("F", "id", 4);

        arithmeticExpressionLL1ParseTable = new LL1ParseTable();
        arithmeticExpressionLL1ParseTable.set("E", "(", 0);
        arithmeticExpressionLL1ParseTable.set("E", "(", 1);
        arithmeticExpressionLL1ParseTable.set("E", "id", 0);
        arithmeticExpressionLL1ParseTable.set("E", "id", 1);
        arithmeticExpressionLL1ParseTable.set("T", "(", 4);
        arithmeticExpressionLL1ParseTable.set("T", "(", 5);
        arithmeticExpressionLL1ParseTable.set("T", "id", 4);
        arithmeticExpressionLL1ParseTable.set("T", "id", 5);
        arithmeticExpressionLL1ParseTable.set("F", "(", 2);
        arithmeticExpressionLL1ParseTable.set("F", "id", 3);

        ambiguousArithmeticExpressionLL1ParseTable = new LL1ParseTable();
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "(", 0);
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "(", 1);
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "(", 2);
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "id", 1);
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "id", 2);
        ambiguousArithmeticExpressionLL1ParseTable.set("E", "id", 3);

        danglingElseProblemLL1ParseTable = new LL1ParseTable();
        danglingElseProblemLL1ParseTable.set("E", "e", 0);
        danglingElseProblemLL1ParseTable.set("S", "if", 1);
        danglingElseProblemLL1ParseTable.set("S", "if", 2);
        danglingElseProblemLL1ParseTable.set("S", "s", 3);

        tinyAmbiguousExpressionLL1ParseTable = new LL1ParseTable();
        tinyAmbiguousExpressionLL1ParseTable.set("E", "id", 0);
        tinyAmbiguousExpressionLL1ParseTable.set("E", "id", 1);
        tinyAmbiguousExpressionLL1ParseTable.set("E", "id", 2);


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

        a0 = makeItems(
                "[E ::= " + MARKER + " E E, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " E id, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " id, " + TERMINATOR + "/id]",
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]"
        );

        a1 = makeItems(
                "[E ::= E " + MARKER + " E, " + TERMINATOR + "/id]",
                "[E ::= E " + MARKER + " id, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " E E, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " E id, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " id, " + TERMINATOR + "/id]",
                "[E' ::= E " + MARKER + ", " + TERMINATOR + "]"
        );

        a2 = makeItems("[E ::= id " + MARKER + ", " + TERMINATOR + "/id]");

        a3 = makeItems(
                "[E ::= E E " + MARKER + ", " + TERMINATOR + "/id]",
                "[E ::= E " + MARKER + " E, " + TERMINATOR + "/id]",
                "[E ::= E " + MARKER + " id, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " E E, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " E id, " + TERMINATOR + "/id]",
                "[E ::= " + MARKER + " id, " + TERMINATOR + "/id]"
        );

        a4 = makeItems(
                "[E ::= E id ·, #/id]",
                "[E ::= id ·, #/id]"
        );

        arithmeticExpressionTransitions = new Transitions(
                makeTransition(s0, "E", s1),
                makeTransition(s0, "T", s2),
                makeTransition(s0, "F", s3),
                makeTransition(s0, "(", s4),
                makeTransition(s0, "id", s5),
                makeTransition(s1, "+", s6),
                makeTransition(s2, "*", s7),
                makeTransition(s4, "E", s8),
                makeTransition(s4, "T", s9),
                makeTransition(s4, "F", s10),
                makeTransition(s4, "(", s11),
                makeTransition(s4, "id", s12),
                makeTransition(s6, "T", s13),
                makeTransition(s6, "F", s3),
                makeTransition(s6, "(", s4),
                makeTransition(s6, "id", s5),
                makeTransition(s7, "F", s14),
                makeTransition(s7, "(", s4),
                makeTransition(s7, "id", s5),
                makeTransition(s8, ")", s15),
                makeTransition(s8, "+", s16),
                makeTransition(s9, "*", s17),
                makeTransition(s11, "E", s18),
                makeTransition(s11, "T", s9),
                makeTransition(s11, "F", s10),
                makeTransition(s11, "(", s11),
                makeTransition(s11, "id", s12),
                makeTransition(s13, "*", s7),
                makeTransition(s16, "T", s19),
                makeTransition(s16, "F", s10),
                makeTransition(s16, "(", s11),
                makeTransition(s16, "id", s12),
                makeTransition(s17, "F", s20),
                makeTransition(s17, "(", s11),
                makeTransition(s17, "id", s12),
                makeTransition(s18, ")", s21),
                makeTransition(s18, "+", s16),
                makeTransition(s19, "*", s17)
        );

        tinyAmbiguousExpressionTransitions = new Transitions(
                makeTransition(a3, "E", a3),
                makeTransition(a3, "id", a4),
                makeTransition(a1, "E", a3),
                makeTransition(a1, "id", a4),
                makeTransition(a0, "E", a1),
                makeTransition(a0, "id", a2)
        );

        arithmeticExpressionCollection = new LR1Collection(s0, arithmeticExpressionTransitions);
        arithmeticExpressionCollection.addAll(
                Arrays.asList(
                        s0, s1, s2, s3, s4, s5, s6, s7,
                        s8, s9, s10, s11, s12, s13, s14,
                        s15, s16, s17, s18, s19, s20, s21
                )
        );

        tinyAmbiguousExpressionLR1Collection = new LR1Collection(a0,
                tinyAmbiguousExpressionTransitions);
        tinyAmbiguousExpressionLR1Collection.addAll(Arrays.asList(a0, a1, a2, a3, a4));

        // Use indices from sample exam answers rather than try to recompute everything
        sampleExamProblem4ActionTable = new ActionTable();
        sampleExamProblem4ActionTable.set(0, "+", makeAction(SHIFT, 4));
        sampleExamProblem4ActionTable.set(0, "a", makeAction(SHIFT, 5));
        sampleExamProblem4ActionTable.set(1, TERMINATOR, makeAction(ACCEPT, noSuchState));
        sampleExamProblem4ActionTable.set(2, "=", makeAction(SHIFT, 6));
        sampleExamProblem4ActionTable.set(2, TERMINATOR, makeAction(REDUCE, 4));
        sampleExamProblem4ActionTable.set(3, TERMINATOR, makeAction(REDUCE, 1));
        sampleExamProblem4ActionTable.set(4, "+", makeAction(SHIFT, 4));
        sampleExamProblem4ActionTable.set(4, "a", makeAction(SHIFT, 5));
        sampleExamProblem4ActionTable.set(5, "=", makeAction(REDUCE, 3));
        sampleExamProblem4ActionTable.set(5, TERMINATOR, makeAction(REDUCE, 3));
        sampleExamProblem4ActionTable.set(6, "+", makeAction(SHIFT, 11));
        sampleExamProblem4ActionTable.set(6, "a", makeAction(SHIFT, 12));
        sampleExamProblem4ActionTable.set(7, "=", makeAction(REDUCE, 4));
        sampleExamProblem4ActionTable.set(7, TERMINATOR, makeAction(REDUCE, 4));
        sampleExamProblem4ActionTable.set(8, "=", makeAction(REDUCE, 2));
        sampleExamProblem4ActionTable.set(8, TERMINATOR, makeAction(REDUCE, 2));
        sampleExamProblem4ActionTable.set(9, TERMINATOR, makeAction(REDUCE, 4));
        sampleExamProblem4ActionTable.set(10, TERMINATOR, makeAction(REDUCE, 0));
        sampleExamProblem4ActionTable.set(11, "+", makeAction(SHIFT, 11));
        sampleExamProblem4ActionTable.set(11, "a", makeAction(SHIFT, 12));
        sampleExamProblem4ActionTable.set(12, TERMINATOR, makeAction(REDUCE, 3));
        sampleExamProblem4ActionTable.set(13, TERMINATOR, makeAction(REDUCE, 2));

        arithmeticExpressionActionTable = new ActionTable();
        arithmeticExpressionActionTable.set(9, "(", makeAction(SHIFT, 10));
        arithmeticExpressionActionTable.set(9, "id", makeAction(SHIFT, 14));
        arithmeticExpressionActionTable.set(4, "+", makeAction(SHIFT, 2));
        arithmeticExpressionActionTable.set(4, TERMINATOR, makeAction(ACCEPT, noSuchState));
        arithmeticExpressionActionTable.set(7, "+", makeAction(REDUCE, 1));
        arithmeticExpressionActionTable.set(7, "*", makeAction(SHIFT, 16));
        arithmeticExpressionActionTable.set(7, TERMINATOR, makeAction(REDUCE, 1));
        arithmeticExpressionActionTable.set(18, "+", makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(18, "*", makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(18, TERMINATOR, makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(10, "(", makeAction(SHIFT, 11));
        arithmeticExpressionActionTable.set(10, "id", makeAction(SHIFT, 15));
        arithmeticExpressionActionTable.set(14, "+", makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(14, "*", makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(14, TERMINATOR, makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(2, "(", makeAction(SHIFT, 10));
        arithmeticExpressionActionTable.set(2, "id", makeAction(SHIFT, 14));
        arithmeticExpressionActionTable.set(16, "(", makeAction(SHIFT, 10));
        arithmeticExpressionActionTable.set(16, "id", makeAction(SHIFT, 14));
        arithmeticExpressionActionTable.set(5, "+", makeAction(SHIFT, 3));
        arithmeticExpressionActionTable.set(5, ")", makeAction(SHIFT, 12));
        arithmeticExpressionActionTable.set(8, "+", makeAction(REDUCE, 1));
        arithmeticExpressionActionTable.set(8, "*", makeAction(SHIFT, 17));
        arithmeticExpressionActionTable.set(8, ")", makeAction(REDUCE, 1));
        arithmeticExpressionActionTable.set(19, "+", makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(19, "*", makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(19, ")", makeAction(REDUCE, 4));
        arithmeticExpressionActionTable.set(11, "(", makeAction(SHIFT, 11));
        arithmeticExpressionActionTable.set(11, "id", makeAction(SHIFT, 15));
        arithmeticExpressionActionTable.set(15, "+", makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(15, "*", makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(15, ")", makeAction(REDUCE, 3));
        arithmeticExpressionActionTable.set(0, "+", makeAction(REDUCE, 0));
        arithmeticExpressionActionTable.set(0, "*", makeAction(SHIFT, 16));
        arithmeticExpressionActionTable.set(0, TERMINATOR, makeAction(REDUCE, 0));
        arithmeticExpressionActionTable.set(20, "+", makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(20, "*", makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(20, TERMINATOR, makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(12, "+", makeAction(REDUCE, 2));
        arithmeticExpressionActionTable.set(12, "*", makeAction(REDUCE, 2));
        arithmeticExpressionActionTable.set(12, TERMINATOR, makeAction(REDUCE, 2));
        arithmeticExpressionActionTable.set(3, "(", makeAction(SHIFT, 11));
        arithmeticExpressionActionTable.set(3, "id", makeAction(SHIFT, 15));
        arithmeticExpressionActionTable.set(17, "(", makeAction(SHIFT, 11));
        arithmeticExpressionActionTable.set(17, "id", makeAction(SHIFT, 15));
        arithmeticExpressionActionTable.set(6, "+", makeAction(SHIFT, 3));
        arithmeticExpressionActionTable.set(6, ")", makeAction(SHIFT, 13));
        arithmeticExpressionActionTable.set(1, "+", makeAction(REDUCE, 0));
        arithmeticExpressionActionTable.set(1, "*", makeAction(SHIFT, 17));
        arithmeticExpressionActionTable.set(1, ")", makeAction(REDUCE, 0));
        arithmeticExpressionActionTable.set(21, "+", makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(21, "*", makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(21, ")", makeAction(REDUCE, 5));
        arithmeticExpressionActionTable.set(13, "+", makeAction(REDUCE, 2));
        arithmeticExpressionActionTable.set(13, "*", makeAction(REDUCE, 2));
        arithmeticExpressionActionTable.set(13, ")", makeAction(REDUCE, 2));

        tinyAmbiguousExpressionActionTable = new ActionTable();
        tinyAmbiguousExpressionActionTable.set(0, TERMINATOR, makeAction(REDUCE, 0));
        tinyAmbiguousExpressionActionTable.set(0, "id", makeAction(SHIFT, 1));
        tinyAmbiguousExpressionActionTable.set(0, "id", makeAction(REDUCE, 0));
        tinyAmbiguousExpressionActionTable.set(1, TERMINATOR, makeAction(REDUCE, 1));
        tinyAmbiguousExpressionActionTable.set(1, TERMINATOR, makeAction(REDUCE, 2));
        tinyAmbiguousExpressionActionTable.set(1, "id", makeAction(REDUCE, 1));
        tinyAmbiguousExpressionActionTable.set(1, "id", makeAction(REDUCE, 2));
        tinyAmbiguousExpressionActionTable.set(2, TERMINATOR, makeAction(ACCEPT, noSuchState));
        tinyAmbiguousExpressionActionTable.set(2, "id", makeAction(SHIFT, 1));
        tinyAmbiguousExpressionActionTable.set(3, TERMINATOR, makeAction(REDUCE, 2));
        tinyAmbiguousExpressionActionTable.set(3, "id", makeAction(REDUCE, 2));
        tinyAmbiguousExpressionActionTable.set(4, "id", makeAction(SHIFT, 3));

        sampleExamProblem4GotoTable = new GotoTable();
        sampleExamProblem4GotoTable.set(0, "X", 1);
        sampleExamProblem4GotoTable.set(0, "Y", 2);
        sampleExamProblem4GotoTable.set(0, "Z", 3);
        sampleExamProblem4GotoTable.set(4, "Y", 7);
        sampleExamProblem4GotoTable.set(4, "Z", 8);
        sampleExamProblem4GotoTable.set(6, "Y", 9);
        sampleExamProblem4GotoTable.set(6, "Z", 10);
        sampleExamProblem4GotoTable.set(11, "Y", 9);
        sampleExamProblem4GotoTable.set(11, "Z", 13);

        arithmeticExpressionGotoTable = new GotoTable();
        arithmeticExpressionGotoTable.set(9, "E", 4);
        arithmeticExpressionGotoTable.set(9, "T", 7);
        arithmeticExpressionGotoTable.set(9, "F", 18);
        arithmeticExpressionGotoTable.set(10, "E", 5);
        arithmeticExpressionGotoTable.set(10, "T", 8);
        arithmeticExpressionGotoTable.set(10, "F", 19);
        arithmeticExpressionGotoTable.set(2, "T", 0);
        arithmeticExpressionGotoTable.set(2, "F", 18);
        arithmeticExpressionGotoTable.set(16, "F", 20);
        arithmeticExpressionGotoTable.set(11, "E", 6);
        arithmeticExpressionGotoTable.set(11, "T", 8);
        arithmeticExpressionGotoTable.set(11, "F", 19);
        arithmeticExpressionGotoTable.set(3, "T", 1);
        arithmeticExpressionGotoTable.set(3, "F", 19);
        arithmeticExpressionGotoTable.set(17, "F", 21);

        tinyAmbiguousExpressionGotoTable = new GotoTable();
        tinyAmbiguousExpressionGotoTable.set(0, "E", 0);
        tinyAmbiguousExpressionGotoTable.set(2, "E", 0);
        tinyAmbiguousExpressionGotoTable.set(4, "E", 2);

        sampleExamProblem4LR1ParseTable = new LR1ParseTable(
                sampleExamProblem4ActionTable,
                sampleExamProblem4GotoTable,
                0
        );

        arithmeticExpressionLR1ParseTable = new LR1ParseTable(
                arithmeticExpressionActionTable,
                arithmeticExpressionGotoTable,
                9
        );

        tinyAmbiguousExpressionLR1ParseTable = new LR1ParseTable(
                tinyAmbiguousExpressionActionTable,
                tinyAmbiguousExpressionGotoTable,
                4
        );
    }

    @Test
    void first() {
        assertEquals(
                sampleExamProblem3FirstMap,
                sampleExamProblem3.first()
        );

        assertEquals(
                arithmeticExpressionReduxFirstMap,
                arithmeticExpressionRedux.first()
        );

        assertEquals(
                arithmeticExpressionFirstMap,
                arithmeticExpression.first()
        );

        assertEquals(
                ambiguousArithmeticExpressionFirstMap,
                ambiguousArithmeticExpression.first()
        );

        assertEquals(
                danglingElseProblemFirstMap,
                danglingElseProblem.first()
        );

        assertEquals(
                tinyAmbiguousExpressionFirstMap,
                tinyAmbiguousExpression.first()
        );
    }

    @Test
    void follow() {
        assertEquals(sampleExamProblem3FollowMap,
                sampleExamProblem3.follow(sampleExamProblem3FirstMap));

        assertEquals(
                arithmeticExpressionReduxFollowMap,
                arithmeticExpressionRedux.follow(arithmeticExpressionReduxFirstMap)
        );

        assertEquals(
                arithmeticExpressionFollowMap,
                arithmeticExpression.follow(arithmeticExpressionFirstMap)
        );

        assertEquals(
                ambiguousArithmeticExpressionFollowMap,
                ambiguousArithmeticExpression.follow(ambiguousArithmeticExpressionFirstMap)
        );

        assertEquals(
                danglingElseProblemFollowMap,
                danglingElseProblem.follow(danglingElseProblemFirstMap)
        );

        assertEquals(
                tinyAmbiguousExpressionFollowMap,
                tinyAmbiguousExpression.follow(tinyAmbiguousExpressionFirstMap)
        );
    }

    @Test
    void generateLL1ParseTable() {
        assertEquals(
                sampleExamProblem3LL1ParseTable,
                sampleExamProblem3.generateLL1ParseTable(
                        sampleExamProblem3FirstMap,
                        sampleExamProblem3FollowMap
                )
        );

        assertEquals(
                arithmeticExpressionReduxLL1ParseTable,
                arithmeticExpressionRedux.generateLL1ParseTable(
                        arithmeticExpressionReduxFirstMap,
                        arithmeticExpressionReduxFollowMap
                )
        );

        assertEquals(
                arithmeticExpressionLL1ParseTable,
                arithmeticExpression.generateLL1ParseTable(
                        arithmeticExpressionFirstMap,
                        arithmeticExpressionFollowMap
                )
        );

        assertEquals(
                ambiguousArithmeticExpressionLL1ParseTable,
                ambiguousArithmeticExpression.generateLL1ParseTable(
                        ambiguousArithmeticExpressionFirstMap,
                        ambiguousArithmeticExpressionFollowMap
                )
        );

        assertEquals(
                danglingElseProblemLL1ParseTable,
                danglingElseProblem.generateLL1ParseTable(
                        danglingElseProblemFirstMap,
                        danglingElseProblemFollowMap
                )
        );

        assertEquals(
                tinyAmbiguousExpressionLL1ParseTable,
                tinyAmbiguousExpression.generateLL1ParseTable(
                        tinyAmbiguousExpressionFirstMap,
                        tinyAmbiguousExpressionFollowMap
                )
        );
    }

    @Test
    void parseSentenceLL1() throws Exception {
        /* In contrast to the table from the lecture slides, this test checks for entries
        where EPSILON is on the stack */

        assertEquals(
                new LL1ParseOutput(
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "S"),
                                makeQueue("b", "c", "c", "c", "a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "A"),
                                makeQueue("b", "c", "c", "c", "a", TERMINATOR),
                                4
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B", "b"),
                                makeQueue("b", "c", "c", "c", "a", TERMINATOR),
                                0
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B"),
                                makeQueue("c", "c", "c", "a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B", "c"),
                                makeQueue("c", "c", "c", "a", TERMINATOR),
                                2
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B"),
                                makeQueue("c", "c", "a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B", "c"),
                                makeQueue("c", "c", "a", TERMINATOR),
                                2
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B"),
                                makeQueue("c", "a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B", "c"),
                                makeQueue("c", "a", TERMINATOR),
                                2
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "B"),
                                makeQueue("a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a", "ɛ"),
                                makeQueue("a", TERMINATOR),
                                3
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR, "a"),
                                makeQueue("a", TERMINATOR),
                                null
                        ),
                        makeLL1ParseOutputEntry(
                                makeStack(TERMINATOR),
                                makeQueue(TERMINATOR),
                                null
                        )
                ),
                sampleExamProblem3.parseSentence(
                        sampleExamProblem3LL1ParseTable,
                        "b c c c a " + TERMINATOR
                )
        );

        assertEquals(
                new LL1ParseOutput(
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
                ),
                arithmeticExpressionRedux.parseSentence(
                        arithmeticExpressionReduxLL1ParseTable,
                        "id + id * id " + TERMINATOR
                )
        );


    }

    @Test
    void isLL1() {
        assertFalse(arithmeticExpression.isLL1(arithmeticExpressionLL1ParseTable));
        assertTrue(arithmeticExpressionRedux.isLL1(arithmeticExpressionReduxLL1ParseTable));
        assertTrue(sampleExamProblem3.isLL1(sampleExamProblem3LL1ParseTable));
        assertFalse(ambiguousArithmeticExpression.isLL1(ambiguousArithmeticExpressionLL1ParseTable));
        assertFalse(danglingElseProblem.isLL1(danglingElseProblemLL1ParseTable));
        assertFalse(tinyAmbiguousExpression.isLL1(tinyAmbiguousExpressionLL1ParseTable));
    }

    @Test
    void removeLeftRecursion() {
        assertEquals(
                new Grammar(
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
                ),
                directAndIndirectLeftRecursion.removeLeftRecursion()
        );

        assertEquals(
                new Grammar(
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
                ),
                arithmeticExpression.removeLeftRecursion()
        );
    }

    @Test
    void augment() {
        assertEquals(augmentedArithmeticExpression, arithmeticExpression.augment());
    }

    @Test
    void computeLR1Collection() {
        assertEquals(
                arithmeticExpressionCollection,
                arithmeticExpression.computeLR1Collection()
        );

        assertEquals(
                tinyAmbiguousExpressionLR1Collection,
                tinyAmbiguousExpression.computeLR1Collection()
        );
    }

    @Test
    void generateLR1ParseTable() {
        assertEquals(
                arithmeticExpressionLR1ParseTable,
                arithmeticExpression.generateLR1ParseTable(arithmeticExpressionCollection)
        );

        assertEquals(
                tinyAmbiguousExpressionLR1ParseTable,
                tinyAmbiguousExpression.generateLR1ParseTable(tinyAmbiguousExpressionLR1Collection)
        );
    }

    @Test
    void parseSentenceLR1() throws Exception {
        assertEquals(
                new LR1ParseOutput(
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
                ),
                arithmeticExpression.parseSentence(
                        arithmeticExpressionLR1ParseTable,
                        "id + id * id " + TERMINATOR
                )
        );

        assertEquals(
                new LR1ParseOutput(
                        makeLR1ParseOutputEntry(
                                makeStack(makePair(noSuchSymbol, 0)),
                                makeQueue("a", "=", "+", "a", TERMINATOR),
                                makeAction(SHIFT, 5)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("a", 5)
                                ),
                                makeQueue("=", "+", "a", TERMINATOR),
                                makeAction(REDUCE, 3)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2)
                                ),
                                makeQueue("=", "+", "a", TERMINATOR),
                                makeAction(SHIFT, 6)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6)
                                ),
                                makeQueue("+", "a", TERMINATOR),
                                makeAction(SHIFT, 11)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("+", 11)
                                ),
                                makeQueue("a", TERMINATOR),
                                makeAction(SHIFT, 12)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("+", 11),
                                        makePair("a", 12)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(REDUCE, 3)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("+", 11),
                                        makePair("Y", 9)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(REDUCE, 4)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("+", 11),
                                        makePair("Z", 13)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(REDUCE, 2)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("Y", 9)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(REDUCE, 4)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("Y", 2),
                                        makePair("=", 6),
                                        makePair("Z", 10)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(REDUCE, 0)
                        ),
                        makeLR1ParseOutputEntry(
                                makeStack(
                                        makePair(noSuchSymbol, 0),
                                        makePair("X", 1)
                                ),
                                makeQueue(TERMINATOR),
                                makeAction(ACCEPT, noSuchState)
                        )
                ),
                sampleExamProblem4.parseSentence(
                        sampleExamProblem4LR1ParseTable,
                        "a = + a " + TERMINATOR
                )
        );
    }

    @Test
    void isLR1() {
        assertTrue(arithmeticExpression.isLR1(arithmeticExpressionLR1ParseTable));
        assertTrue(sampleExamProblem4.isLR1(sampleExamProblem4LR1ParseTable));
        assertFalse(tinyAmbiguousExpression.isLR1(tinyAmbiguousExpressionLR1ParseTable));
    }

    @Test
    void containsEpsilonProductions() {
        assertTrue(arithmeticExpressionRedux.containsEpsilonProductions());
        assertFalse(arithmeticExpression.containsEpsilonProductions());
    }
}