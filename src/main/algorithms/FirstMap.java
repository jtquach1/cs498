package algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import static algorithms.Grammar.EPSILON;

class First extends TreeSet<String> {
    public First(String... symbols) {
        super();
        this.addAll(Arrays.asList(symbols));
    }

    First deepClone() {
        First clone = new First();
        clone.addAll(this);
        return clone;
    }
}

class FirstMap extends TreeMap<String, First> {
    FirstMap() {
        super();
    }

    FirstMap deepClone() {
        FirstMap mapClone = new FirstMap();
        for (String symbol : this.keySet()) {
            First old = this.get(symbol);
            First clone = new First();
            clone.addAll(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }

    void initializeFirstSetOfTerminal(String symbol) {
        First set = new First();
        set.add(symbol);
        this.put(symbol, set);
    }

    void initializeFirstSetOfNonTerminal(String symbol) {
        First set = new First();
        this.put(symbol, set);
    }

    void addEpsilonToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        First set = this.get(lhs);
        set.add(EPSILON);
    }

    void addFirstSetOfSequenceToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        First set = this.get(lhs);
        set.addAll(this.first(p.getRhs()));
    }

    First first(List<String> sequence) {
        First F = new First();
        // An empty sequence has no characters
        if (sequence.size() != 0) {
            String firstSymbol = sequence.get(0);
            F.addAll(this.get(firstSymbol));
            int i = 1;
            int n = sequence.size();
            while (F.contains(EPSILON) && i < n) {
                F.remove(EPSILON);
                String symbol = sequence.get(i);
                F.addAll(this.get(symbol));
                i++;
            }
        }
        return F;
    }
}