package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Set<Production> actual = cfg.getProductions();
        Set<Production> expected = new TreeSet<>(Arrays.asList(
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
}
