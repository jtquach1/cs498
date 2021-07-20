package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Grammar.EPSILON;
import static org.junit.jupiter.api.Assertions.*;

class ProductionTest {

    @Test
    void getLhs() {
        Production p = new Production("E", "T", "E'");
        String actual = p.getLhs();
        String expected = "E";
        assertEquals(expected, actual);
    }

    @Test
    void getRhs() {
        Production p = new Production("E", "T", "E'");
        Sequence actual = p.getRhs();
        Sequence expected = new Sequence(Arrays.asList("T", "E'"));
        assertEquals(expected, actual);
    }

    @Test
    void beginsWithEpsilon() {
        Production p = new Production("E", "T", "E'");
        assertFalse(p.beginsWithEpsilon());

        p = new Production("E", EPSILON, "E'");
        assertTrue(p.beginsWithEpsilon());
    }
}