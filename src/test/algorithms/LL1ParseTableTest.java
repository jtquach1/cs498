package algorithms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LL1ParseTableTest {

    @Test
    void set() {
        LL1ParseTable expected = new LL1ParseTable();
        LL1ParseTableEntry entry = new LL1ParseTableEntry();
        entry.put("a", 1);
        expected.put("S", entry);

        LL1ParseTable actual = new LL1ParseTable();
        actual.set("S", "a", 1);

        assertEquals(expected, actual);
    }
}