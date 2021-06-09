package algorithms;

import java.util.TreeMap;

class LL1ParseTable extends TreeMap<String, LL1ParseTableEntry> {

    void set(String nonTerminal, String terminal, int productionIndex) {
        LL1ParseTableEntry entry = this.get(nonTerminal);
        if (entry == null) {
            entry = new LL1ParseTableEntry();
            this.put(nonTerminal, entry);
        }
        entry.put(terminal, productionIndex);
    }
}

class LL1ParseTableEntry extends TreeMap<String, Integer> {
}