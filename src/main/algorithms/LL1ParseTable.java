package algorithms;

import java.util.ArrayList;
import java.util.TreeMap;

class LL1ParseTable extends TreeMap<String, LL1ParseTableRow> {
    void set(String nonTerminal, String terminal, int productionIndex) {
        LL1ParseTableRow row = this.get(nonTerminal);
        LL1ParseTableEntry entry = new LL1ParseTableEntry();
        entry.put(terminal, productionIndex);

        if (row == null) {
            row = new LL1ParseTableRow();
            row.add(entry);
            this.put(nonTerminal, row);
        } else {
            row.add(entry);
        }
    }
}

class LL1ParseTableRow extends ArrayList<LL1ParseTableEntry> {
}

class LL1ParseTableEntry extends TreeMap<String, Integer> {

}