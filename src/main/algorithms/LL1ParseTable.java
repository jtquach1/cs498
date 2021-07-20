package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static algorithms.Grammar.EPSILON;

class Follow extends TreeSet<String> {
    Follow(String... symbols) {
        this.addAll(Arrays.asList(symbols));
    }

    Follow(@NotNull Collection<? extends String> sets) {
        super(sets);
    }
}

class FollowMap extends TreeMap<String, Follow> {
    FollowMap() {
    }

    FollowMap deepClone() {
        FollowMap mapClone = new FollowMap();
        for (String symbol : this.keySet()) {
            Follow old = this.get(symbol);
            Follow clone = new Follow(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }

    void initializeFollowSetOfNonTerminal(String symbol) {
        Follow set = new Follow();
        this.put(symbol, set);
    }
}

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

class LL1ParseTable extends TreeMap<String, LL1ParseTableEntry> {

    void set(String nonTerminal, String terminal, int productionIndex) {
        LL1ParseTableEntry entry = this.get(nonTerminal);
        Indices indices;
        if (entry == null) {
            entry = new LL1ParseTableEntry();
            this.put(nonTerminal, entry);
        }
        indices = entry.get(terminal);
        if (indices == null) {
            indices = new Indices();
        }

        indices.add(productionIndex);
        entry.put(terminal, indices);
    }

    Integer get(String nonTerminal, String symbol) {
        LL1ParseTableEntry entry = this.get(nonTerminal);
        if (entry == null) {
            return null;
        }

        Indices indices = entry.get(symbol);
        if (symbol == null || indices == null) {
            return null;
        }

        // In case of conflicts, we just want to return one value.
        return indices.get(0);
    }
}

class LL1ParseTableEntry extends TreeMap<String, Indices> {
}

class Indices extends ArrayList<Integer> {
    /* We use a list of integers to see whether a parsed grammar was LL1.
     If the list contains more than one index, then the grammar is ambiguous
     and must have left recursion removed. */
}

class LL1ParseOutput extends ArrayList<LL1ParseOutputEntry> {
    public LL1ParseOutput() {
    }

    public LL1ParseOutput(@NotNull Collection<? extends LL1ParseOutputEntry> c) {
        super(c);
    }
}

class LL1ParseOutputEntry {
    Stack<String> stack;
    Queue<String> input;
    Integer output;

    LL1ParseOutputEntry(Stack<String> stack, Queue<String> input, Integer output, String cursor) {
        /* We need to deep clone the parameters because their references are
        constantly changing in the LL1 parsing algorithm. */

        this.stack = new Stack<>(stack);

        // Lecture slides show the cursor as part of the input.
        this.input = new Queue<>(input);

        if (cursor != null) {
            this.input.queue(cursor);
        }

        this.output = output;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, input, output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LL1ParseOutputEntry other = (LL1ParseOutputEntry) o;
        return Objects.equals(stack, other.stack) &&
                Objects.equals(input, other.input) &&
                Objects.equals(output, other.output);
    }

    @Override
    public String toString() {
        return "{\"stack\":\"" + stack + "\"," +
                "\"input\":\"" + input + "\"," +
                "\"output\":\"" + output + "\"}\n";
    }
}