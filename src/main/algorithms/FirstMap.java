package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static algorithms.Grammar.EPSILON;

class First extends TreeSet<String> {
    First(String... symbols) {
        this.addAll(Arrays.asList(symbols));
    }

    First(@NotNull Collection<? extends String> sets) {
        super(sets);
    }
}

class FirstMap extends TreeMap<String, First> {
    FirstMap() {
    }

    FirstMap deepClone() {
        FirstMap mapClone = new FirstMap();
        for (String symbol : this.keySet()) {
            First old = this.get(symbol);
            First clone = new First(old);
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

    void addEpsilonToFirstSetOfNonTerminal(Production p) {
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
            First entry = this.get(firstSymbol);

            if (entry == null) {
                // If the entry doesn't already exist, it must be the terminator
                entry = new First();
                entry.add(firstSymbol);
            }

            F.addAll(entry);
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