package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Utility.CHECKMARK;

class FollowMap extends TreeMap<String, Symbols> implements DOT {
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
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td colspan=\"2\">Follow</td></tr>");

        for (String symbol : this.keySet()) {
            sb.append("<tr>");
            sb.append("<td align=\"left\">" + symbol + "</td>");
            sb.append("<td align=\"left\">" + this.get(symbol).toDOT() + "</td>");
            sb.append("</tr>");
        }

        return sb.toString();
    }
}

class FirstMap extends TreeMap<String, Symbols> implements DOT {
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
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td colspan=\"2\">First</td></tr>");

        for (String symbol : this.keySet()) {
            sb.append("<tr>");
            sb.append("<td align=\"left\">" + symbol + "</td>");
            sb.append("<td align=\"left\">" + this.get(symbol).toDOT() + "</td>");
            sb.append("</tr>");
        }

        return sb.toString();
    }
}

class LL1ParseTable extends Table<String, String, Integer> implements DOT {
    @Override
    public String toDOT() {
        String terminals = this
                .secondKeySet()
                .stream()
                .map(terminal -> "<td>" + terminal + "</td>")
                .collect(Collectors.joining(""));
        int headerLength = this.secondKeySet().size() + 1;

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td colspan=\"" + headerLength + "\">LL(1) Parse Table</td></tr>");

        sb.append("<tr>");
        sb.append("<td></td>");
        sb.append(terminals);
        sb.append("</tr>");

        for (String nonTerminal : this.firstKeySet()) {
            StringBuilder indices = new StringBuilder();

            for (String terminal : this.secondKeySet()) {
                List<Integer> conflicts = this.getConflicts(nonTerminal, terminal);
                String print;
                if (conflicts != null) {
                    // Production indices are 1-indexed in the DOT representation of a Grammar.
                    print = conflicts
                            .stream()
                            .map(conflict -> Integer.toString(conflict + 1))
                            .collect(Collectors.joining(", "));
                } else {
                    print = "";
                }
                indices.append("<td align=\"left\">" + print + "</td>");
            }

            sb.append("<tr>");
            sb.append("<td>" + nonTerminal + "</td>");
            sb.append(indices);
            sb.append("</tr>");
        }

        return sb.toString();
    }
}

class LL1ParseOutput extends ArrayList<LL1ParseOutputEntry> implements DOT {
    LL1ParseOutput() {
    }

    LL1ParseOutput(LL1ParseOutputEntry... entries) {
        super(Arrays.asList(entries));
    }

    @Override
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td colspan=\"3\">LL(1) Parse Output</td></tr>");
        sb.append("<tr>" +
                "<td>Stack</td>" +
                "<td>Input</td>" +
                "<td>Output</td>" +
                "</tr>");
        for (LL1ParseOutputEntry entry : this) {
            sb.append(entry.toDOT());
        }
        return sb.toString();
    }
}

class LL1ParseOutputEntry extends OutputEntry<String, String, Integer> implements DOT {
    /* We use a list of integers to see whether a parsed grammar was LL1.
     If the list contains more than one index, then the grammar is ambiguous
     and must have left recursion removed. */
    LL1ParseOutputEntry(Stack<String> stack, Queue<String> input, Integer output, String cursor) {
        super(stack, input, output, cursor);
    }

    @Override
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        String stack = String.join(" ", this.getStack());
        String input = String.join(" ", this.getInput());

        // Production indices are 1-indexed in the DOT representation of a Grammar.
        Integer index = this.getOutput();
        String output;
        if (index == null) {
            output = "";
        } else {
            index += 1;
            output = index.toString();
        }

        if (isFinalEntry()) {
            output = CHECKMARK;
        }

        sb.append("<tr>" +
                "<td align=\"left\">" + stack + "</td>" +
                "<td align=\"left\">" + input + "</td>" +
                "<td>" + output + "</td>" +
                "</tr>");

        return sb.toString();
    }

    private boolean isFinalEntry() {
        return this.getStack().peek().equals(TERMINATOR) &&
                this.getInput().get(0).equals(TERMINATOR) &&
                this.getOutput() == null;
    }
}