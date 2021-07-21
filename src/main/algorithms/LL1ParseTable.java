package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.TreeMap;

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

        // An empty sequence has no characters.
        if (sequence.size() != 0) {

            String firstSymbol = sequence.get(0);
            Symbols entry = this.get(firstSymbol);

            if (entry == null) {
                // If the entry doesn't already exist, it must be the terminator.
                entry = new Symbols();
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

class LL1ParseTable extends Table<String, String, Indices> {

    void set(String nonTerminal, String terminal, int productionIndex) {
        Indices indices = super.get(nonTerminal, terminal);
        if (indices == null) {
            indices = new Indices();
        }
        indices.add(productionIndex);
        super.set(nonTerminal, terminal, indices);
    }

    Integer getIndex(String nonTerminal, String terminal) {
        // In case of conflicts, we just want to return one value.
        Indices indices = super.get(nonTerminal, terminal);
        return indices != null ? indices.get(0) : null;
    }
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