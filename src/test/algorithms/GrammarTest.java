package algorithms;

import org.junit.jupiter.api.Test;

import java.util.*;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class GrammarTest {

    @Test
    void addAndGetNonTerminals() {
        Grammar cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        Set<String> actual = cfg.getNonTerminals();
        Set<String> expected = new TreeSet<>(Arrays.asList("E", "E'", "T", "T'", "F"));
        assertEquals(expected, actual);
    }

    @Test
    void addAndGetTerminals() {
        Grammar cfg = new Grammar("E");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        Set<String> actual = cfg.getTerminals();
        Set<String> expected = new TreeSet<>(Arrays.asList("+", EPSILON, "*", "(", ")", "id"));
        assertEquals(expected, actual);
    }

    @Test
    void addAndGetProductions() {
        Grammar cfg = new Grammar("E");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        List<Production> actual = cfg.getProductions();
        List<Production> expected = new ArrayList<>(Arrays.asList(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        ));
        assertEquals(expected, actual);
    }

    @Test
    void first() {
        // From sample exam 1
        Grammar cfg = new Grammar("S");
        cfg.addNonTerminals("A", "B");
        cfg.addTerminals("a", "b", "c", EPSILON);
        cfg.addProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
        );
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
        cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
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

        Grammar cfg = new Grammar("S");
        cfg.addNonTerminals("A", "B");
        cfg.addTerminals("a", "b", "c", EPSILON);
        cfg.addProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
        );
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

        // From lecture slides
        expected = new FollowMap();
        expected.put("E", new Follow(")", TERMINATOR));
        expected.put("E'", new Follow(")", TERMINATOR));
        expected.put("F", new Follow("+", "*", ")", TERMINATOR));
        expected.put("T", new Follow("+", ")", TERMINATOR));
        expected.put("T'", new Follow("+", ")", TERMINATOR));

        cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
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

        Grammar cfg = new Grammar("S");
        cfg.addNonTerminals("A", "B");
        cfg.addTerminals("a", "b", "c", EPSILON);
        cfg.addProductions(
                new Production("S", "A", "a"),
                new Production("S", "a"),
                new Production("A", "c"),
                new Production("A", "b", "B"),
                new Production("B", "c", "B"),
                new Production("B", EPSILON)
        );

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

        cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
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
    }

    @Test
    void parseSentence() throws Exception {
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

        Grammar cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        String w = "id + id * id " + TERMINATOR;
        LL1ParseOutput actual = cfg.parseSentence(table, w);

        assertEquals(expected, actual);
    }

    @Test
    void removeLeftRecursion() {
        // From lecture slides 1
        Grammar expected = new Grammar("Y");
        expected.addNonTerminals("'Y");
        expected.addTerminals("a", "b", EPSILON);
        expected.addProductions(
                new Production("Y", "b", "Y'"),
                new Production("Y'", "a", "Y'"),
                new Production("Y'", EPSILON)
        );

        Grammar cfg = new Grammar("Y");
        cfg.addTerminals("a", "b");
        cfg.addProductions(
                new Production("Y", "Y", "a"),
                new Production("Y", "b")
        );
        Grammar actual = cfg.removeLeftRecursion();

        assertEquals(cfg, actual);

        // From lecture slides 2
        /*
        Grammar expected = new Grammar("E");
        expected.addNonTerminals("E'", "T", "T'", "F");
        expected.addTerminals("+", EPSILON, "*", "(", ")", "id");
        expected.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );

        Grammar cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "E", "+", "T"),
                new Production("E", "T"),
                new Production("T", "T", "*", "F"),
                new Production("T'", "F"),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        Grammar actual = cfg.removeLeftRecursion();

        assertEquals(expected, actual);
        */
    }

    @Test
    void deepClone() {
        Grammar expected = new Grammar("E");
        expected.addNonTerminals("E'", "T", "T'", "F");
        expected.addTerminals("+", EPSILON, "*", "(", ")", "id");
        expected.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "E", ")"),
                new Production("F", "id")
        );
        Grammar actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);
    }
}
