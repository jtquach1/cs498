package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static algorithms.Grammar.EPSILON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class FirstMapTest {

    @Test
    void deepClone() {
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
        FirstMap actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);

        for (String symbol : expected.keySet()) {
            assertEquals(expected.get(symbol), actual.get(symbol));
            assertNotSame(expected.get(symbol), actual.get(symbol));
        }
    }

    @Test
    void initializeFirstSetOfTerminal() {
        FirstMap expected = new FirstMap();
        expected.put("(", new First("("));
        FirstMap actual = new FirstMap();
        actual.initializeFirstSetOfTerminal("(");
        assertEquals(expected, actual);
    }


    @Test
    void initializeFirstSetOfNonTerminal() {
        FirstMap expected = new FirstMap();
        expected.put("E", new First());
        FirstMap actual = new FirstMap();
        actual.initializeFirstSetOfNonTerminal("E");
        assertEquals(expected, actual);
    }

    @Test
    void addEpsilonToFirstSetOfNonTerminal() {
        FirstMap expected = new FirstMap();
        expected.put("E", new First(EPSILON));

        Production p = new Production("E", "T", "E'");
        FirstMap actual = new FirstMap();
        actual.put("E", new First());
        actual.addEpsilonToFirstSetOfNonTerminal(p);

        assertEquals(expected, actual);
    }

    @Test
    void addFirstSetOfSequenceToFirstSetOfSymbol() {
        // When rhs doesn't begin with EPSILON
        FirstMap expected = new FirstMap();
        expected.put("(", new First("("));
        expected.put(")", new First(")"));
        expected.put("*", new First("*"));
        expected.put("+", new First("+"));
        expected.put("E", new First());
        expected.put("E'", new First("+", EPSILON));
        expected.put("F", new First("(", "id"));
        expected.put("T", new First("(", "id"));
        expected.put("T'", new First("*", EPSILON));
        expected.put("id", new First("id"));
        expected.put(EPSILON, new First(EPSILON));

        FirstMap actual = new FirstMap();
        actual.put("(", new First("("));
        actual.put(")", new First(")"));
        actual.put("*", new First("*"));
        actual.put("+", new First("+"));
        actual.put("E", new First());
        actual.put("E'", new First("+", EPSILON));
        actual.put("F", new First("(", "id"));
        actual.put("T", new First("(", "id"));
        actual.put("T'", new First(EPSILON));
        actual.put("id", new First("id"));
        actual.put(EPSILON, new First(EPSILON));
        actual.addFirstSetOfSequenceToFirstSetOfSymbol(new Production("T'", "*", "F", "T"));

        assertEquals(expected, actual);

        // When rhs does begin with EPSILON
        expected = new FirstMap();
        expected.put("(", new First("("));
        expected.put(")", new First(")"));
        expected.put("*", new First("*"));
        expected.put("+", new First("+"));
        expected.put("E", new First());
        expected.put("E'", new First("+", EPSILON));
        expected.put("F", new First());
        expected.put("T", new First());
        expected.put("T'", new First(EPSILON));
        expected.put("id", new First("id"));
        expected.put(EPSILON, new First(EPSILON));

        actual = new FirstMap();
        actual.put("(", new First("("));
        actual.put(")", new First(")"));
        actual.put("*", new First("*"));
        actual.put("+", new First("+"));
        actual.put("E", new First());
        actual.put("E'", new First("+", EPSILON));
        actual.put("F", new First());
        actual.put("T", new First());
        actual.put("T'", new First(EPSILON));
        actual.put("id", new First("id"));
        actual.put(EPSILON, new First(EPSILON));
        actual.addFirstSetOfSequenceToFirstSetOfSymbol(new Production("E'", EPSILON));

        assertEquals(expected, actual);
    }

    @Test
    void first() {
        // When rhs doesn't begin with EPSILON
        FirstMap map = new FirstMap();
        map.put("(", new First("("));
        map.put(")", new First(")"));
        map.put("*", new First("*"));
        map.put("+", new First("+"));
        map.put("E", new First());
        map.put("E'", new First("+", EPSILON));
        map.put("F", new First("(", "id"));
        map.put("T", new First("(", "id"));
        map.put("T'", new First(EPSILON));
        map.put("id", new First("id"));
        map.put(EPSILON, new First(EPSILON));

        First actual = map.first(new Sequence(Arrays.asList("*", "F", "T'")));
        First expected = new First("*");
        assertEquals(expected, actual);

        // When rhs does begin with EPSILON
        map = new FirstMap();
        map.put("(", new First("("));
        map.put(")", new First(")"));
        map.put("*", new First("*"));
        map.put("+", new First("+"));
        map.put("E", new First());
        map.put("E'", new First("+", EPSILON));
        map.put("F", new First());
        map.put("T", new First());
        map.put("T'", new First(EPSILON));
        map.put("id", new First("id"));
        map.put(EPSILON, new First(EPSILON));

        actual = map.first(new Sequence(Collections.singletonList(EPSILON)));
        expected = new First(EPSILON);
        assertEquals(expected, actual);
    }
}