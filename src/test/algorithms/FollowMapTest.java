package algorithms;

import org.junit.jupiter.api.Test;

import static algorithms.Grammar.TERMINATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class FollowMapTest {

    @Test
    void deepClone() {
        FollowMap expected = new FollowMap();
        expected.put("E", new Symbols(")", TERMINATOR));
        expected.put("E'", new Symbols(")", TERMINATOR));
        expected.put("F", new Symbols("+", "*", ")", TERMINATOR));
        expected.put("T", new Symbols("+", ")", TERMINATOR));
        expected.put("T'", new Symbols("+", ")", TERMINATOR));
        FollowMap actual = expected.deepClone();
        assertEquals(expected, actual);
        assertNotSame(expected, actual);

        for (String symbol : expected.keySet()) {
            assertEquals(expected.get(symbol), actual.get(symbol));
            assertNotSame(expected.get(symbol), actual.get(symbol));
        }
    }

    @Test
    void initializeFollowSetOfNonTerminal() {
        FollowMap expected = new FollowMap();
        expected.put("E", new Symbols());
        FollowMap actual = new FollowMap();
        actual.initializeFollowSetOfNonTerminal("E");
        assertEquals(expected, actual);
    }
}