package algorithms;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void getAlpha() {
        List<String> expected = new ArrayList<>();
        Item item = new Item(TERMINATOR, "E'", MARKER, "E");
        List<String> actual = item.getAlpha();
        assertEquals(expected, actual);
    }

    @Test
    void getBeta() {
        List<String> expected = new ArrayList<>(Collections.singletonList("E"));
        Item item = new Item(TERMINATOR, "E'", MARKER, "E");
        List<String> actual = item.getBeta();
        assertEquals(expected, actual);
    }
}