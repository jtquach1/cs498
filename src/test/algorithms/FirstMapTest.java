package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static algorithms.Grammar.EPSILON;
import static algorithms.Utility.makeFirst;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class FirstMapTest {

    @Test
    void deepClone() {
        FirstMap expected = new FirstMap();
        expected.put("(", makeFirst("("));
        expected.put(")", makeFirst(")"));
        expected.put("*", makeFirst("*"));
        expected.put("+", makeFirst("+"));
        expected.put("E", makeFirst("(", "id"));
        expected.put("E'", makeFirst("+", EPSILON));
        expected.put("F", makeFirst("(", "id"));
        expected.put("T", makeFirst("(", "id"));
        expected.put("T'", makeFirst("*", EPSILON));
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
        expected.put("(", makeFirst("("));
        FirstMap actual = new FirstMap();
        actual.initializeFirstSetOfTerminal("(");
        assertEquals(expected, actual);
    }


    @Test
    void initializeFirstSetOfNonTerminal() {
        FirstMap expected = new FirstMap();
        expected.put("E", makeFirst());
        FirstMap actual = new FirstMap();
        actual.initializeFirstSetOfNonTerminal("E");
        assertEquals(expected, actual);
    }

    @Test
    void addEpsilonToFirstSetOfNonTerminal() {
        FirstMap expected = new FirstMap();
        expected.put("E", makeFirst(EPSILON));

        Production p = new Production("E", "T", "E'");
        FirstMap actual = new FirstMap();
        actual.put("E", makeFirst());
        actual.addEpsilonToFirstSetOfNonTerminal(p);

        assertEquals(expected, actual);
    }

    @Test
    void addFirstSetOfSequenceToFirstSetOfSymbol() {
        // When rhs doesn't begin with EPSILON
        FirstMap expected = new FirstMap();
        expected.put("(", makeFirst("("));
        expected.put(")", makeFirst(")"));
        expected.put("*", makeFirst("*"));
        expected.put("+", makeFirst("+"));
        expected.put("E", makeFirst());
        expected.put("E'", makeFirst("+", EPSILON));
        expected.put("F", makeFirst("(", "id"));
        expected.put("T", makeFirst("(", "id"));
        expected.put("T'", makeFirst("*", EPSILON));
        expected.put("id", makeFirst("id"));
        expected.put(EPSILON, makeFirst(EPSILON));

        FirstMap actual = new FirstMap();
        actual.put("(", makeFirst("("));
        actual.put(")", makeFirst(")"));
        actual.put("*", makeFirst("*"));
        actual.put("+", makeFirst("+"));
        actual.put("E", makeFirst());
        actual.put("E'", makeFirst("+", EPSILON));
        actual.put("F", makeFirst("(", "id"));
        actual.put("T", makeFirst("(", "id"));
        actual.put("T'", makeFirst(EPSILON));
        actual.put("id", makeFirst("id"));
        actual.put(EPSILON, makeFirst(EPSILON));
        actual.addFirstSetOfSequenceToFirstSetOfSymbol(new Production("T'", "*", "F", "T"));

        assertEquals(expected, actual);

        // When rhs does begin with EPSILON
        expected = new FirstMap();
        expected.put("(", makeFirst("("));
        expected.put(")", makeFirst(")"));
        expected.put("*", makeFirst("*"));
        expected.put("+", makeFirst("+"));
        expected.put("E", makeFirst());
        expected.put("E'", makeFirst("+", EPSILON));
        expected.put("F", makeFirst());
        expected.put("T", makeFirst());
        expected.put("T'", makeFirst(EPSILON));
        expected.put("id", makeFirst("id"));
        expected.put(EPSILON, makeFirst(EPSILON));

        actual = new FirstMap();
        actual.put("(", makeFirst("("));
        actual.put(")", makeFirst(")"));
        actual.put("*", makeFirst("*"));
        actual.put("+", makeFirst("+"));
        actual.put("E", makeFirst());
        actual.put("E'", makeFirst("+", EPSILON));
        actual.put("F", makeFirst());
        actual.put("T", makeFirst());
        actual.put("T'", makeFirst(EPSILON));
        actual.put("id", makeFirst("id"));
        actual.put(EPSILON, makeFirst(EPSILON));
        actual.addFirstSetOfSequenceToFirstSetOfSymbol(new Production("E'", EPSILON));

        assertEquals(expected, actual);
    }

    @Test
    void first() {
        // When rhs doesn't begin with EPSILON
        FirstMap map = new FirstMap();
        map.put("(", makeFirst("("));
        map.put(")", makeFirst(")"));
        map.put("*", makeFirst("*"));
        map.put("+", makeFirst("+"));
        map.put("E", makeFirst());
        map.put("E'", makeFirst("+", EPSILON));
        map.put("F", makeFirst("(", "id"));
        map.put("T", makeFirst("(", "id"));
        map.put("T'", makeFirst(EPSILON));
        map.put("id", makeFirst("id"));
        map.put(EPSILON, makeFirst(EPSILON));

        Symbols actual = map.first(new Sequence(Arrays.asList("*", "F", "T'")));
        Symbols expected = makeFirst("*");
        assertEquals(expected, actual);

        // When rhs does begin with EPSILON
        map = new FirstMap();
        map.put("(", makeFirst("("));
        map.put(")", makeFirst(")"));
        map.put("*", makeFirst("*"));
        map.put("+", makeFirst("+"));
        map.put("E", makeFirst());
        map.put("E'", makeFirst("+", EPSILON));
        map.put("F", makeFirst());
        map.put("T", makeFirst());
        map.put("T'", makeFirst(EPSILON));
        map.put("id", makeFirst("id"));
        map.put(EPSILON, makeFirst(EPSILON));

        actual = map.first(new Sequence(Collections.singletonList(EPSILON)));
        expected = makeFirst(EPSILON);
        assertEquals(expected, actual);
    }
}