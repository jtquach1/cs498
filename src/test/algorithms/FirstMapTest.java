package algorithms;

import org.junit.jupiter.api.Test;

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
        expected.put("(", new First());
        FirstMap actual = new FirstMap();
        actual.initializeFirstSetOfNonTerminal("(");
        assertEquals(expected, actual);
    }

    @Test
    void addEpsilonToFirstSetOfSymbol() {
        FirstMap expected = new FirstMap();
        expected.put("E", new First(EPSILON));

        Production p = new Production("E", "T", "E'");
        FirstMap actual = new FirstMap();
        actual.put("E", new First());
        actual.addEpsilonToFirstSetOfSymbol(p);

        assertEquals(expected, actual);
    }

    @Test
    void addFirstSetOfSequenceToFirstSetOfSymbol() {
    }

    @Test
    void first() {
    }
}