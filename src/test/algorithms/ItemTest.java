package algorithms;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    @Test
    void getAlpha() {
        Sequence expected = new Sequence();
        Item item = new Item(TERMINATOR, "E'", MARKER, "E");
        Sequence actual = item.getAlpha();
        assertEquals(expected, actual);
    }

    @Test
    void getBeta() {
        Sequence expected = new Sequence(Collections.singletonList("E"));
        Item item = new Item(TERMINATOR, "E'", MARKER, "E");
        Sequence actual = item.getBeta();
        assertEquals(expected, actual);
    }
}