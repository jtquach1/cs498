package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;

class FollowMap extends TreeMap<String, Symbols> {
    FollowMap() {
    }

    FollowMap deepClone() {
        FollowMap mapClone = new FollowMap();
        for (String symbol : this.keySet()) {
            Symbols old = this.get(symbol);
            Symbols clone = new Symbols(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }

    void initializeFollowSetOfNonTerminal(String symbol) {
        Symbols set = new Symbols();
        this.put(symbol, set);
    }

    @Override
    public String toString() {
        String entries = this
                .keySet()
                .stream()
                .map(key -> {
                    String values = this.get(key)
                            .stream()
                            .map(value -> "\"" + value + "\"")
                            .collect(Collectors.joining(","));

                    return "\"" + key + "\":" + "[" + values + "]";
                })
                .collect(Collectors.joining(","));

        return "{" + entries + "}";
    }
}

class FirstMap extends TreeMap<String, Symbols> {
    FirstMap() {
    }

    FirstMap deepClone() {
        FirstMap mapClone = new FirstMap();
        for (String symbol : this.keySet()) {
            Symbols old = this.get(symbol);
            Symbols clone = new Symbols(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }

    void initializeFirstSetOfTerminal(String symbol) {
        Symbols set = new Symbols();
        set.add(symbol);
        this.put(symbol, set);
    }

    void initializeFirstSetOfNonTerminal(String symbol) {
        Symbols set = new Symbols();
        this.put(symbol, set);
    }

    void addEpsilonToFirstSetOfNonTerminal(Production p) {
        String lhs = p.getLhs();
        Symbols set = this.get(lhs);
        set.add(EPSILON);
    }

    void addFirstSetOfSequenceToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        Symbols set = this.get(lhs);
        Sequence rhs = p.getRhs();
        set.addAll(this.first(rhs));
    }

    Symbols first(Sequence sequence) {
        Symbols F = new Symbols();
        int i = 0;

        // An empty sequence has no characters.
        if (sequence.size() != 0) {

            String symbol = sequence.get(i++);
            Symbols entry = this.get(symbol);

            if (entry == null) {
                // If the entry doesn't already exist, it must be the terminator.
                entry = new Symbols();
                entry.add(symbol);
            }

            F.addAll(entry);
            int n = sequence.size();

            while (F.contains(EPSILON) && i < n) {
                F.remove(EPSILON);
                symbol = sequence.get(i);
                entry = this.get(symbol);

                if (entry == null) {
                    entry = new Symbols();
                    entry.add(symbol);
                }

                F.addAll(entry);
                i++;
            }
        }

        return F;
    }

    @Override
    public String toString() {
        String entries = this
                .keySet()
                .stream()
                .map(key -> {
                    String values = this.get(key)
                            .stream()
                            .map(value -> "\"" + value + "\"")
                            .collect(Collectors.joining(","));

                    return "\"" + key + "\":" + "[" + values + "]";
                })
                .collect(Collectors.joining(","));

        return "{" + entries + "}";
    }
}

class LL1ParseTable extends Table<String, String, Integer> {
}

class LL1ParseOutput extends ArrayList<LL1ParseOutputEntry> {
    LL1ParseOutput() {
    }

    LL1ParseOutput(LL1ParseOutputEntry... entries) {
        super(Arrays.asList(entries));
    }
}

class LL1ParseOutputEntry extends OutputEntry<String, String, Integer> {
    /* We use a list of integers to see whether a parsed grammar was LL1.
     If the list contains more than one index, then the grammar is ambiguous
     and must have left recursion removed. */
    LL1ParseOutputEntry(Stack<String> stack, Queue<String> input, Integer output, String cursor) {
        super(stack, input, output, cursor);
    }
}