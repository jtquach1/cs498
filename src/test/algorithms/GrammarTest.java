package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {

    @Test
    void first() {
        // From sample exam 1
        Symbols nonTerminals = makeNonTerminals("A", "B");
        Symbols terminals = makeTerminals("a", "b", "c", EPSILON);
        Productions productions = makeProductions(
                "S ::= A a",
                "S ::= a",
                "A ::= c",
                "A ::= b B",
                "B ::= c B",
                "B ::= " + EPSILON
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "S", productions);
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
        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
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
        nonTerminals = makeNonTerminals("T", "F");
        terminals = makeTerminals("+", "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= E + T",
                "E ::= T",
                "T ::= T * F",
                "T ::= F",
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
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

        Symbols nonTerminals = makeNonTerminals("A", "B");
        Symbols terminals = makeTerminals("a", "b", "c", EPSILON);
        Productions productions = makeProductions(
                "S ::= A a",
                "S ::= a",
                "A ::= c",
                "A ::= b B",
                "B ::= c B",
                "B ::= " + EPSILON
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "S", productions);

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

        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);

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

        nonTerminals = makeNonTerminals("T", "F");
        terminals = makeTerminals("+", "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= E + T",
                "E ::= T",
                "T ::= T * F",
                "T ::= F",
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);

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
        expected.set("S", "a", 1);
        expected.set("S", "b", 0);
        expected.set("S", "c", 0);
        expected.set("A", "b", 3);
        expected.set("A", "c", 2);
        expected.set("B", "a", 5);
        expected.set("B", "c", 4);

        Symbols nonTerminals = makeNonTerminals("A", "B");
        Symbols terminals = makeTerminals("a", "b", "c", EPSILON);
        Productions productions = makeProductions(
                "S ::= A a",
                "S ::= a",
                "A ::= c",
                "A ::= b B",
                "B ::= c B",
                "B ::= " + EPSILON
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "S", productions);

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
        expected.set("T", "(", 3);
        expected.set("T", "id", 3);
        expected.set("T'", "+", 5);
        expected.set("T'", "*", 4);
        expected.set("T'", ")", 5);
        expected.set("T'", TERMINATOR, 5);
        expected.set("F", "(", 6);
        expected.set("F", "id", 7);

        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);

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
        expected.set("T", "(", 2);
        expected.set("T", "(", 3);
        expected.set("T", "id", 2);
        expected.set("T", "id", 3);
        expected.set("F", "(", 4);
        expected.set("F", "id", 5);

        nonTerminals = makeNonTerminals("T", "F");
        terminals = makeTerminals("+", "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= E + T",
                "E ::= T",
                "T ::= T * F",
                "T ::= F",
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);

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
                        3),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "+", "id", "*", "id", TERMINATOR),
                        7),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue("+", "id", "*", "id", TERMINATOR),
                        5),
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
                        3),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", "*", "id", TERMINATOR),
                        7),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue("*", "id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F", "*"),
                        makeQueue("*", "id", TERMINATOR),
                        4),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "F"),
                        makeQueue("id", TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'", "id"),
                        makeQueue("id", TERMINATOR),
                        7),
                makeEntry(
                        makeStack(TERMINATOR, "E'", "T'"),
                        makeQueue(TERMINATOR),
                        null),
                makeEntry(
                        makeStack(TERMINATOR, "E'", EPSILON),
                        makeQueue(TERMINATOR),
                        5),
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
        table.set("T", "(", 3);
        table.set("T", "id", 3);
        table.set("T'", "+", 5);
        table.set("T'", "*", 4);
        table.set("T'", ")", 5);
        table.set("T'", TERMINATOR, 5);
        table.set("F", "(", 6);
        table.set("F", "id", 7);

        Symbols nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        Symbols terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        Productions productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "E", productions);

        String w = "id + id * id " + TERMINATOR;
        LL1ParseOutput actual = cfg.parseSentence(table, w);
        assertEquals(expected, actual);
    }

    @Test
    void isLL1() {
        // Non LL(1) grammar with direct left recursion
        Symbols nonTerminals = makeNonTerminals("T", "F");
        Symbols terminals = makeTerminals("+", "*", "(", ")", "id");
        Productions productions = makeProductions(
                "E ::= E + T",
                "E ::= T",
                "T ::= T * F",
                "T ::= F",
                "F ::= ( E )",
                "F ::= id"
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "E", productions);
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
        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
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
        // Textbook example
        Grammar expected = new Grammar(
                makeNonTerminals("A"),
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
        Grammar cfg = new Grammar(
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
        Grammar actual = cfg.removeLeftRecursion();
        assertEquals(expected, actual);
    }

    @Test
    void deepClone() {
        Symbols nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        Symbols terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        Productions productions = makeProductions(
                "E ::= T E'",
                "E' ::= + T E'",
                "E' ::= " + EPSILON,
                "T ::= F T'",
                "T' ::= * F T'",
                "T' ::= " + EPSILON,
                "F ::= ( E )",
                "F ::= id"
        );
        Grammar expected = new Grammar(nonTerminals, terminals, "E", productions);
        Grammar actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }
}