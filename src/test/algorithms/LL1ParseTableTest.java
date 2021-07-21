package algorithms;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LL1ParseTableTest {

    @Test
    void set() {
        LL1ParseTable expected = new LL1ParseTable();
        TreeMap<String, Indices> entry = new TreeMap<>();
        Indices indices = new Indices();
        indices.add(1);
        entry.put("a", indices);
        expected.put("S", entry);

        LL1ParseTable actual = new LL1ParseTable();
        actual.set("S", "a", 1);

        assertEquals(expected, actual);
    }

    @Test
    void getIndex() {
        LL1ParseTable table = new LL1ParseTable();
        TreeMap<String, Indices> entry = new TreeMap<>();
        Indices indices = new Indices();
        indices.add(1);
        indices.add(5);
        entry.put("a", indices);
        table.put("S", entry);

        assertEquals(1, table.getIndex("S", "a"));
        assertNull(table.getIndex("A", "a"));
        assertNull(table.getIndex("S", "b"));
    }
}