package algorithms;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<String> actual = p.getRhs();
        List<String> expected = new ArrayList<>(Arrays.asList("T", "E'"));
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