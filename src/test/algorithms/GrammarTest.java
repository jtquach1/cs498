package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static algorithms.Grammar.EPSILON;
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
                new Production("F", "(", "id", ")"),
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
                new Production("F", "(", "id", ")"),
                new Production("F", "id")
        ));
        assertEquals(expected, actual);
    }

    @Test
    void first() {
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
                new Production("F", "(", "id", ")"),
                new Production("F", "id")
        );
        FirstMap actual = cfg.first();
        FirstMap expected = new FirstMap();
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
}
