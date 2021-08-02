package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Execution.*;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Utility.CHECKMARK;
import static algorithms.Utility.printCollection;

enum Execution implements DOT {
    SHIFT, REDUCE, ACCEPT;

    @Override
    public String toDOT() {
        return switch (this) {
            case SHIFT -> "s";
            case REDUCE -> "r";
            case ACCEPT -> CHECKMARK;
        };
    }
}

class LR1ParseTable implements DOT {
    private final ActionTable actionTable;
    private final GotoTable gotoTable;
    private final Integer startIndex;

    LR1ParseTable(ActionTable actionTable, GotoTable gotoTable, Integer startIndex) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        this.startIndex = startIndex;
    }

    Integer getStartIndex() {
        return startIndex;
    }

    ActionTable getActionTable() {
        return actionTable;
    }

    GotoTable getGotoTable() {
        return gotoTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1ParseTable other = (LR1ParseTable) o;
        return actionTable.equals(other.actionTable) &&
                gotoTable.equals(other.gotoTable) &&
                startIndex.equals(other.startIndex);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        return "{" +
                "\"actionTable\": " + actionTable +
                ", \"gotoTable\": " + gotoTable +
                ", \"startIndex\": " + startIndex +
                '}';
    }

    @Override
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        StringBuilder header = printHeader();

        sb.append("<table>");
        sb.append(header);

        for (Integer productionIndex : this.actionTable.firstKeySet()) {
            StringBuilder actions = printActions(productionIndex);
            StringBuilder productionIndices = printProductionIndices(productionIndex);
            sb.append("<tr>");
            sb.append("<td>" + productionIndex + "</td>");
            sb.append(actions);
            sb.append(productionIndices);
            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private StringBuilder printHeader() {
        StringBuilder header = new StringBuilder();

        String terminals = this
                .actionTable
                .secondKeySet()
                .stream()
                .map(terminal -> "<td>" + terminal + "</td>")
                .collect(Collectors.joining(""));
        int actionHeaderLength = this.actionTable.secondKeySet().size();

        String nonTerminals = this
                .gotoTable
                .secondKeySet()
                .stream()
                .map(nonTerminal -> "<td>" + nonTerminal + "</td>")
                .collect(Collectors.joining(""));
        int gotoHeaderLength = this.gotoTable.secondKeySet().size();

        int headerLength = actionHeaderLength + gotoHeaderLength + 1;

        header.append("<tr><td colspan=\"" + headerLength + "\">LR(1) Parse Tables</td></tr>");

        header.append("<tr>" +
                "<td rowspan=\"2\">States</td>" +
                "<td colspan=\"" + actionHeaderLength + "\">Action</td>" +
                "<td colspan=\"" + gotoHeaderLength + "\">Goto</td>" +
                "</tr>");

        header.append("<tr>" + terminals + nonTerminals + "</tr>");

        return header;
    }

    private StringBuilder printProductionIndices(Integer productionIndex) {
        StringBuilder productionIndices = new StringBuilder();
        for (String nonTerminal : this.gotoTable.secondKeySet()) {
            List<Integer> conflicts = this.gotoTable.getConflicts(productionIndex, nonTerminal);
            String print;
            if (conflicts != null) {
                print = conflicts
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
            } else {
                print = "";
            }
            productionIndices.append("<td align=\"left\">" + print + "</td>");
        }
        return productionIndices;
    }

    private StringBuilder printActions(Integer productionIndex) {
        StringBuilder actions = new StringBuilder();
        for (String terminal : this.actionTable.secondKeySet()) {
            List<Action> conflicts = this.actionTable.getConflicts(productionIndex, terminal);
            String print;
            if (conflicts != null) {
                print = conflicts
                        .stream()
                        .map(Action::toDOT)
                        .collect(Collectors.joining(", "));
            } else {
                print = "";
            }
            actions.append("<td align=\"left\">" + print + "</td>");
        }
        return actions;
    }
}

class ActionTable extends Table<Integer, String, Action> {
    // We don't transition out of an Accept state in the DFA generated from an LR1 collection.
    static final Integer noSuchState = -2;

    void populateWithReduce(Productions productions, Items from, Integer fromIndex) {
        from
                .stream()
                .filter(item -> item.getBeta().isEmpty())
                .forEach(item -> {
                            String lhs = item.getLhs();
                            String[] alpha = item.getAlpha().toArray(new String[0]);
                            String symbol = item.getLookahead();

                            Production production = new Production(lhs, alpha);
                            Integer index = productions.indexOf(production);
                            Action action = new Action(REDUCE, index);

                            this.set(fromIndex, symbol, action);
                        }
                );
    }

    void populateWithAccept(Integer fromIndex) {
        Action action = new Action(ACCEPT, noSuchState);
        this.set(fromIndex, TERMINATOR, action);
    }

    void populateWithShift(Integer fromIndex, String terminal, Integer toIndex) {
        Action action = new Action(SHIFT, toIndex);
        this.set(fromIndex, terminal, action);
    }
}

class Action implements Comparable<Action>, DOT {
    private final Execution execution;

    // Refers to an LR1 collection state index when shifting OR a production index when reducing.
    private final Integer index;

    Action(Execution execution, Integer index) {
        this.execution = execution;
        this.index = index;
    }

    @Override
    public int compareTo(@NotNull Action other) {
        return Comparator
                .comparing(Action::getExecution)
                .thenComparing(Action::getIndex)
                .compare(this, other);
    }

    Execution getExecution() {
        return execution;
    }

    Integer getIndex() {
        return index;
    }

    boolean executesAccept() {
        return execution.equals(ACCEPT);
    }

    boolean executesReduce() {
        return execution.equals(REDUCE);
    }

    boolean executesShift() {
        return execution.equals(SHIFT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action other = (Action) o;
        return execution == other.execution && index.equals(other.index);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        return "{" +
                "\"execution\":\"" + execution + "\"" +
                ", \"index\":\"" + index + "\"" +
                "}";
    }

    @Override
    public String toDOT() {
        if (execution.equals(ACCEPT)) {
            return execution.toDOT();
        }
        return execution.toDOT() + index;
    }
}

class GotoTable extends Table<Integer, String, Integer> {
}

class LR1ParseOutput extends ArrayList<LR1ParseOutputEntry> implements DOT {
    LR1ParseOutput() {
    }

    LR1ParseOutput(LR1ParseOutputEntry... entries) {
        super(Arrays.asList(entries));
    }

    @Override
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><td colspan=\"3\">LR(1) Parse Output</td></tr>");
        sb.append("<tr>" +
                "<td>Stack</td>" +
                "<td>Input</td>" +
                "<td>Action</td>" +
                "</tr>");
        for (LR1ParseOutputEntry entry : this) {
            sb.append(entry.toDOT());
        }
        sb.append("</table>");
        return sb.toString();
    }
}

class LR1ParseOutputEntry extends OutputEntry<Pair, String, Action> implements DOT {
    // Contains states represented as Integers and non-terminals represented as Strings.
    LR1ParseOutputEntry(Stack<Pair> stack, Queue<String> input, Action output, String cursor) {
        super(stack, input, output, cursor);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        String input = printCollection(this.getInput());

        return "{\"stack\":" + this.getStack() +
                ", \"input\":" + input +
                ", \"action\":" + this.getOutput() +
                "}";
    }

    @Override
    public String toDOT() {
        StringBuilder sb = new StringBuilder();
        String stack = this
                .getStack()
                .stream()
                .map(Pair::toDOT)
                .collect(Collectors.joining(" "));
        String input = String.join(" ", this.getInput());
        String output = this.getOutput().toDOT();
        sb.append("<tr>" +
                "<td align=\"left\">" + stack + "</td>" +
                "<td align=\"left\">" + input + "</td>" +
                "<td>" + output + "</td>" +
                "</tr>");

        return sb.toString();
    }
}

class Pair implements Comparable<Pair>, DOT {
    /* When initializing the LR1 parse stack, the first state has not transitioned from any
    symbol, hence there is no such symbol we push onto the stack */
    static final String noSuchSymbol = "";

    // Can either be terminal or non-terminal.
    private final String symbol;
    private final Integer stateIndex;

    Pair(String symbol, Integer stateIndex) {
        this.symbol = symbol;
        this.stateIndex = stateIndex;
    }

    @Override
    public int compareTo(@NotNull Pair other) {
        return Comparator
                .comparing(Pair::getStateIndex)
                .thenComparing(Pair::getSymbol)
                .compare(this, other);
    }

    Integer getStateIndex() {
        return stateIndex;
    }

    String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair other = (Pair) o;
        return Objects.equals(stateIndex, other.stateIndex) && Objects.equals(symbol, other.symbol);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        String existingSymbol = symbol.equals(noSuchSymbol) ? "" : "\"symbol\": \"" + symbol +
                "\",";
        return "{" + existingSymbol + "\"stateIndex\": " + stateIndex + "}";
    }

    @Override
    public String toDOT() {
        String space = symbol.equals(noSuchSymbol) ? "" : " ";
        return symbol + space + stateIndex;
    }
}

class LR1Collection extends ListWithUniques<Items> implements DOT {
    private final Transitions transitions;

    /* We store a reference to the starting item set because its index is unstable when the
    collection is being populated. After population, we use its index to create the
    LR1ParseTable. Indices are easier to read than the constituent item sets, and we do not need
    the item sets during parsing. */
    private final Items start;

    LR1Collection(Items start, Transitions transitions) {
        super(Items::compareTo);
        this.add(start);
        this.transitions = transitions;
        this.start = start;
    }

    Transitions getTransitions() {
        return transitions;
    }

    LR1Collection deepClone() {
        Transitions transitions = this.transitions.deepClone();
        LR1Collection clone = new LR1Collection(start, transitions);
        clone.addAll(this);
        return clone;
    }

    void add(Transition transition) {
        super.add(transition.getTo());
        transitions.add(transition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LR1Collection other = (LR1Collection) o;
        return transitions.equals(other.transitions) && start.equals(other.start);
    }

    public boolean contains(Transition transition) {
        return transitions.contains(transition) && super.contains(transition.getTo());
    }

    Items getStart() {
        return start;
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        String collection = super.toString();

        return "{" +
                "\"collection\": " + collection +
                ", \"transitions\":" + transitions +
                ", \"start\":" + start +
                "}";
    }

    @Override
    public String toDOT() {
        TreeSet<String> symbols = transitions
                .stream()
                .map(Transition::getSymbol)
                .collect(Collectors.toCollection(TreeSet::new));

        return "<table>" +
                printHeader(symbols) +
                printRows(symbols) +
                "</table>";
    }

    private StringBuilder printRows(TreeSet<String> symbols) {
        StringBuilder rows = new StringBuilder();
        Table<Integer, String, Integer> transitionsTable = getTransitionsTable();

        for (int i = 0; i < this.size(); i++) {
            Items items = this.get(i);
            rows.append("<tr>");
            rows.append("<td>" + i + "</td>");
            rows.append("<td align=\"left\">" + items.toDOT() + "</td>");

            for (String symbol : symbols) {
                List<Integer> conflicts = transitionsTable.getConflicts(i, symbol);
                String print;
                if (conflicts != null) {
                    print = conflicts
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                } else {
                    print = "";
                }
                rows.append("<td align=\"left\">" + print + "</td>");
            }

            rows.append("</tr>");
        }
        return rows;
    }

    @NotNull
    private Table<Integer, String, Integer> getTransitionsTable() {
        Table<Integer, String, Integer> transitionsTable = new Table<>();
        for (Transition transition : transitions) {
            Items from = transition.getFrom();
            String symbol = transition.getSymbol();
            Items to = transition.getTo();
            transitionsTable.set(this.indexOf(from), symbol, this.indexOf(to));
        }
        return transitionsTable;
    }

    private StringBuilder printHeader(TreeSet<String> symbols) {
        StringBuilder header = new StringBuilder();
        String symbolsColumns = symbols
                .stream()
                .map(symbol -> "<td>" + symbol + "</td>")
                .collect(Collectors.joining(""));

        header.append("<tr>" +
                "<td colspan=\"" + (symbols.size() + 2) + "\">LR(1) Canonical Collection</td>" +
                "</tr>");

        header.append("<tr>" +
                "<td rowspan=\"2\">States</td>" +
                "<td rowspan=\"2\">Item sets</td>" +
                "<td colspan=\"" + symbols.size() + "\">Transitions</td>" +
                "</tr>");

        header.append("<tr>" + symbolsColumns + "</tr>");

        return header;
    }
}

class Item extends Production {
    static final String MARKER = Character.toString('\u00B7');
    private final String lookahead;

    Item(String lookahead, String lhs, String... rhs) {
        super(lhs, rhs);
        this.lookahead = lookahead;
    }

    Sequence getAlpha() {
        int untilMarker = rhs.indexOf(MARKER);
        return rhs.subList(0, untilMarker);
    }

    Sequence getBeta() {
        int afterMarker = rhs.indexOf(MARKER) + 1;
        int toEnd = rhs.size();
        return rhs.subList(afterMarker, toEnd);
    }

    public int compareTo(@NotNull Item other) {
        return Comparator
                .comparing(Item::getLhs)
                .thenComparing(Item::getRhs)
                .thenComparing(Item::getLookahead)
                .compare(this, other);
    }

    String getLookahead() {
        return lookahead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item other = (Item) o;
        return Objects.equals(lookahead, other.lookahead);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        String symbols = printCollection(rhs);

        return "{" +
                "\"lhs\":\"" + lhs + "\", " +
                "\"rhs\":" + symbols + ", " +
                "\"lookahead\":\"" + lookahead + "\"" +
                "}";
    }
}

class Items extends TreeSet<Item> implements Comparable<Items>, DOT {
    Items(@NotNull Collection<? extends Item> items) {
        super(items);
    }

    Items computeGoto(String symbol, FirstMap firstMap, Productions productions) {
        Items r = this
                .stream()
                .filter(aboutToParseSymbol(symbol))
                .map(getItemAfterParsingSymbol())
                .collect(Collectors.toCollection(Items::new));
        return r.closure(firstMap, productions);
    }

    Items() {
        super(Item::compareTo);
    }

    @NotNull
    private Function<Item, Item> getItemAfterParsingSymbol() {
        return item -> {
            String lookahead = item.getLookahead();
            String lhs = item.getLhs();
            String[] rhs = getNewRhs(item);
            return new Item(lookahead, lhs, rhs);
        };
    }

    @NotNull
    private static String[] getNewRhs(Item item) {
        Sequence beta = item.getBeta();
        String nonTerminal = beta.get(0);
        Sequence alpha = item.getAlpha();
        Sequence subBeta = beta.subList(1, beta.size());

        Sequence newRhs = new Sequence(alpha);
        newRhs.add(nonTerminal);
        newRhs.add(MARKER);
        newRhs.addAll(subBeta);
        return newRhs.toArray(new String[0]);
    }

    @NotNull
    private Predicate<Item> aboutToParseSymbol(String symbol) {
        return item -> {
            Sequence beta = item.getBeta();
            if (beta == null || beta.isEmpty()) {
                return false;
            }
            return beta.get(0).equals(symbol);
        };
    }

    Items closure(FirstMap firstMap, Productions productions) {
        Items closure = this.deepClone();
        boolean newItemsAreBeingAdded = true;
        Items previous;

        while (newItemsAreBeingAdded) {
            previous = closure.deepClone();
            productions.forEach(addCorrespondingItems(firstMap, closure));
            newItemsAreBeingAdded = !closure.equals(previous);
        }
        return closure;
    }

    Items deepClone() {
        Items clone = new Items();
        clone.addAll(this);
        return clone;
    }

    @NotNull
    private static Consumer<Production> addCorrespondingItems(FirstMap firstMap, Items closure) {
        return rule -> {
            Items partiallyParsedForms = getPartiallyParsedForms(closure, rule);

            partiallyParsedForms.forEach(item -> {
                        Sequence betaLookahead = getBetaLookahead(item);
                        Symbols first = firstMap.first(betaLookahead);
                        first.forEach(addCorrespondingItem(closure, rule));
                    }
            );
        };
    }

    @NotNull
    private static Consumer<String> addCorrespondingItem(Items closure, Production rule) {
        String lhs = rule.getLhs();
        Sequence rhs = rule.getRhs();

        return token -> {
            Item newItem = getItemAfterParsingSymbol(token, lhs, rhs);
            closure.add(newItem);
        };
    }

    @NotNull
    private static Item getItemAfterParsingSymbol(String token, String lhs, Sequence rhs) {
        Sequence gamma = new Sequence();
        gamma.add(MARKER);
        gamma.addAll(rhs);
        return new Item(token, lhs, gamma.toArray(new String[0]));
    }

    @NotNull
    private static Sequence getBetaLookahead(Item item) {
        Sequence beta = item.getBeta();
        Sequence subBeta = beta.subList(1, beta.size());
        String lookahead = item.getLookahead();
        Sequence sequence = new Sequence(subBeta);
        sequence.add(lookahead);
        return sequence;
    }

    @NotNull
    private static Items getPartiallyParsedForms(Items closure, Production rule) {
        String lhs = rule.getLhs();
        return closure
                .stream()
                .filter(hasNonTerminalToParse(lhs))
                .collect(Collectors.toCollection(Items::new));
    }

    private static Predicate<Item> hasNonTerminalToParse(String lhs) {
        return item -> {
            Sequence beta = item.getBeta();
            if (!beta.isEmpty()) {
                return beta.get(0).equals(lhs);
            }
            return false;
        };
    }

    @Override
    public int compareTo(@NotNull Items other) {
        return Comparator
                .comparing(Items::toString)
                .compare(this, other);
    }

    @Override
    public String toDOT() {
        TreeMap<Production, List<String>> abbreviatedItems = getAbbreviatedItems();

        String items = abbreviatedItems
                .keySet()
                .stream()
                .map(key -> {
                    String production = key.getLhs() + " ::= " + String.join(" ", key.getRhs());
                    String lookaheads = String.join("/", abbreviatedItems.get(key));
                    return "[" + production + ", " + lookaheads + "]";
                })
                .collect(Collectors.joining(", "));

        return "{" + items + "}";
    }

    @NotNull
    private TreeMap<Production, List<String>> getAbbreviatedItems() {
        TreeMap<Production, List<String>> abbreviatedItems = new TreeMap<>();
        for (Item item : this) {
            Production key = new Production(item.getLhs(), item.getRhs().toArray(new String[0]));
            List<String> lookaheads = abbreviatedItems.get(key);
            String lookahead = item.getLookahead();

            if (lookaheads != null) {
                lookaheads.add(lookahead);
            } else {
                lookaheads = new Sequence(Collections.singleton(lookahead));
                abbreviatedItems.put(key, lookaheads);
            }
        }
        return abbreviatedItems;
    }
}

class Transition implements Comparable<Transition> {
    private final Items from;
    private final String symbol;
    private final Items to;

    Transition(Items from, String symbol, Items to) {
        this.from = from;
        this.symbol = symbol;
        this.to = to;
    }

    @Override
    public int compareTo(@NotNull Transition other) {
        return Comparator
                .comparing(Transition::getFrom)
                .thenComparing(Transition::getSymbol)
                .thenComparing(Transition::getTo)
                .compare(this, other);
    }

    Items getFrom() {
        return from;
    }

    String getSymbol() {
        return symbol;
    }

    Items getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition other = (Transition) o;
        return Objects.equals(from, other.from) &&
                Objects.equals(symbol, other.symbol) &&
                Objects.equals(to, other.to);
    }

    @Override
    public String toString() {
        return toJSON();
    }

    private String toJSON() {
        return "{" +
                "\"from\":" + from +
                ", \"symbol\": \"" + symbol + "\"" +
                ", \"to\":" + to +
                "}";
    }
}

class Transitions extends TreeSet<Transition> {
    public Transitions(Transition... transitions) {
        super(List.of(transitions));
    }

    Transitions deepClone() {
        Transitions clone = new Transitions();
        clone.addAll(this);
        return clone;
    }
}
