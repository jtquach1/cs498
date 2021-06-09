package algorithms;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import static algorithms.Grammar.TERMINATOR;

class LL1ParseTable extends TreeMap<String, LL1ParseTableEntry> {
    private final TreeMap<String, Integer> terminalToIndex;
    int size;

    LL1ParseTable(Set<String> terminals) {
//        terminals.add(TERMINATOR);
        terminalToIndex = new TreeMap<>();
        int i = 0;
        for (String terminal : terminals) {
            terminalToIndex.put(terminal, i);
            i++;
        }
        size = terminalToIndex.keySet().size();
    }

    void set(String nonTerminal, String terminal, int productionIndex) {
        LL1ParseTableEntry entry = this.get(nonTerminal);
        if (entry == null) {
            entry = new LL1ParseTableEntry(size);
            for (int i = 0; i < size; i++) {
                entry.add(i, null);
            }
            this.put(nonTerminal, entry);
        }
        Integer terminalIndex = terminalToIndex.get(terminal);
        entry.set(terminalIndex, productionIndex);
        this.put(nonTerminal, entry);
    }
}

class LL1ParseTableEntry extends ArrayList<Integer> {
    LL1ParseTableEntry(int size) {
        super(size);
    }
}