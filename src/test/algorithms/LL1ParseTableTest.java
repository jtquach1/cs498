package algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LL1ParseTableTest {

    @Test
    void set() {
        LL1ParseTable expected = new LL1ParseTable();
        LL1ParseTableEntry entry = new LL1ParseTableEntry();
        Indices indices = new Indices();
        indices.add(1);
        entry.put("a", indices);
        expected.put("S", entry);

        LL1ParseTable actual = new LL1ParseTable();
        actual.set("S", "a", 1);

        assertEquals(expected, actual);
    }

    @Test
    void get() {
        LL1ParseTable table = new LL1ParseTable();
        LL1ParseTableEntry entry = new LL1ParseTableEntry();
        Indices indices = new Indices();
        indices.add(1);
        indices.add(5);
        entry.put("a", indices);
        table.put("S", entry);

        assertEquals(1, table.get("S", "a"));
        assertNull(table.get("A", "a"));
        assertNull(table.get("S", "b"));
    }
}