package algorithms;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;

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

        // In case of conflicts, we just want to return one value
        Integer productionIndex = indices.get(0);
        return productionIndex;
    }
}

class LL1ParseTableEntry extends TreeMap<String, Indices> {
    /* We use a list of integers to see whether the parsed grammar was LL1.
     If the list contains more than one index, then the grammar is ambiguous
     and must have left recursion removed. */
}

class Indices extends ArrayList<Integer> {

}

class LL1ParseOutput extends ArrayList<LL1ParseOutputEntry> {
}

class LL1ParseOutputEntry {
    Stack<String> stack;
    Queue<String> input;
    Integer output;

    LL1ParseOutputEntry(Stack<String> stack, Queue<String> input, Integer output, String cursor) {
        /* We need to deep clone the parameters because their references are
        constantly changing in the LL1 parsing algorithm. */

        this.stack = new Stack<>();
        this.stack.addAll(stack);

        // Lecture slides show the cursor as part of the input
        this.input = new Queue<>();
        this.input.addAll(input);
        this.input.queue(cursor);

        this.output = output;
    }

    // For unit tests
    LL1ParseOutputEntry(Stack<String> stack, Queue<String> input, Integer output) {
        this.stack = new Stack<>();
        this.stack.addAll(stack);

        this.input = new Queue<>();
        this.input.addAll(input);

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
        LL1ParseOutputEntry that = (LL1ParseOutputEntry) o;
        return Objects.equals(stack, that.stack) &&
                Objects.equals(input, that.input) &&
                Objects.equals(output, that.output);
    }

    @Override
    public String toString() {
        return "{\"stack\":\"" + stack + "\"," +
                "\"input\":\"" + input + "\"," +
                "\"output\":\"" + output + "\"}\n";
    }
}