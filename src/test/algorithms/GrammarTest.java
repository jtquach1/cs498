package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {

    @Test
    void first() {
        // From sample exam 1
        Set<String> nonTerminals = makeNonTerminals("A", "B");
        Set<String> terminals = makeTerminals("a", "b", "c", EPSILON);
        List<Production> productions = makeProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
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

        // From lecture slides
        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
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
    }

    @Test
    void follow() {
        // From sample exam 1
        FollowMap expected = new FollowMap();
        expected.put("S", new Follow(TERMINATOR));
        expected.put("A", new Follow("a"));
        expected.put("B", new Follow("a"));

        Set<String> nonTerminals = makeNonTerminals("A", "B");
        Set<String> terminals = makeTerminals("a", "b", "c", EPSILON);
        List<Production> productions = makeProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "S", productions);
        FollowMap actual = cfg.follow();

        assertEquals(expected, actual);

        // From lecture slides
        expected = new FollowMap();
        expected.put("E", new Follow(")", TERMINATOR));
        expected.put("E'", new Follow(")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", ")", TERMINATOR));
        expected.put("T'", new Follow("+", ")", TERMINATOR));

        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
        actual = cfg.follow();

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

        Set<String> nonTerminals = makeNonTerminals("A", "B");
        Set<String> terminals = makeTerminals("a", "b", "c", EPSILON);
        List<Production> productions = makeProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "S", productions);
        LL1ParseTable actual = cfg.generateLL1ParseTable();
        assertEquals(expected, actual);

        // From lecture slides
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
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
        actual = cfg.generateLL1ParseTable();
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

        Set<String> nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        Set<String> terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        List<Production> productions = makeProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "E", productions);
        String w = "id + id * id " + TERMINATOR;
        LL1ParseOutput actual = cfg.parseSentence(w);

        assertEquals(expected, actual);
    }

    @Test
    void isLL1() {
        // Non LL(1) grammar with direct left recursion
        Set<String> nonTerminals = makeNonTerminals("T", "F");
        Set<String> terminals = makeTerminals("+", "*", "(", ")", "id");
        List<Production> productions = makeProductions(
                new Production("E", "E", "+", "T"),
                new Production("E", "T"),
                new Production("T", "T", "*", "F"),
                new Production("T", "F"),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        Grammar cfg = new Grammar(nonTerminals, terminals, "E", productions);
        assertFalse(cfg.isLL1());

        // Equivalent LL(1) grammar
        nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        productions = makeProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        cfg = new Grammar(nonTerminals, terminals, "E", productions);
        assertTrue(cfg.isLL1());
    }

    @Test
    void removeLeftRecursion() {
        // From lecture slides 1
        Grammar expected = new Grammar(
                makeNonTerminals("Y'"),
                makeTerminals("a", "b", EPSILON),
                "Y",
                makeProductions(
                        new Production("Y", "b", "Y'"),
                        new Production("Y'", "a", "Y'"),
                        new Production("Y'", EPSILON)
                )
        );
        Grammar cfg = new Grammar(
                makeNonTerminals("Y'"),
                makeTerminals("a", "b"),
                "Y",
                makeProductions(
                        new Production("Y", "Y", "a"),
                        new Production("Y", "b")
                )
        );
        Grammar actual = cfg.removeLeftRecursion();

        assertEquals(cfg, actual);
    }

    @Test
    void deepClone() {
        Set<String> nonTerminals = makeNonTerminals("E'", "T", "T'", "F");
        Set<String> terminals = makeTerminals("+", EPSILON, "*", "(", ")", "id");
        List<Production> productions = makeProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        Grammar expected = new Grammar(nonTerminals, terminals, "E", productions);
        Grammar actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }
}
