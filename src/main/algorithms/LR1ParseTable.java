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

enum Execution {
    SHIFT, REDUCE, ACCEPT
}

class LR1ParseTable {
    private final ActionTable actionTable;
    private final GotoTable gotoTable;
    private final Integer startIndex;

    LR1ParseTable(ActionTable actionTable, GotoTable gotoTable, Integer startIndex) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        this.startIndex = startIndex;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public ActionTable getActionTable() {
        return actionTable;
    }

    public GotoTable getGotoTable() {
        return gotoTable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionTable, gotoTable, startIndex);
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
}

class LR1ParseOutput extends ArrayList<LR1ParseOutputEntry> {
    public LR1ParseOutput() {
    }

    public LR1ParseOutput(@NotNull Collection<? extends LR1ParseOutputEntry> c) {
        super(c);
    }
}

class LR1ParseOutputEntry {
    // Contains states represented as Integers and non-terminals represented as Strings.
    Stack<Pair> stack;
    Queue<String> input;
    Action action;

    LR1ParseOutputEntry(Stack<Pair> stack, Queue<String> input, Action action, String cursor) {
        this.stack = new Stack<>(stack);

        this.input = new Queue<>(input);

        if (cursor != null) {
            this.input.queue(cursor);
        }

        this.action = action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, input, action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1ParseOutputEntry other = (LR1ParseOutputEntry) o;
        return Objects.equals(stack, other.stack) &&
                Objects.equals(input, other.input) &&
                Objects.equals(action, other.action);
    }

    @Override
    public String toString() {
        return "{\"stack\":\"" + stack + "\"," +
                "\"input\":\"" + input + "\"," +
                "\"action\":\"" + action + "\"}\n";
    }
}

class Pair implements Comparable<Pair> {
    // When initializing the LR1 parse stack, s0 has not transitioned from any symbol
    static final String noSuchSymbol = "";

    // Can either be terminal or non-terminal
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
    public int hashCode() {
        return Objects.hash(stateIndex, symbol);
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
        String space = symbol.equals(noSuchSymbol) ? "" : " ";
        return symbol + space + stateIndex;
    }
}

class ActionTable extends TreeMap<Integer, ActionEntry> {
    // Used for the Accept action- don't transition out of an Accept state in the DFA
    static final Integer noSuchState = -1;

    Action get(Integer state, String terminal) {
        ActionEntry entry = this.get(state);
        if (entry == null) {
            return null;
        }

        Action action = entry.get(terminal);
        if (terminal == null || action == null) {
            return null;
        }

        return action;
    }

    void populateWithReduce(Productions productions, Items from, Integer fromIndex) {
        from
                .stream()
                .filter(item -> item.getBeta().isEmpty())
                .forEach(item -> {
                            String lhs = item.getLhs();
                            String[] alpha = item.getAlpha().toArray(new String[0]);
                            String symbol = item.getLookahead();

                            Production production = new Production(lhs, alpha);
                            Integer productionIndex = productions.indexOf(production);
                            Action action = new Action(REDUCE, productionIndex);

                            this.set(fromIndex, symbol, action);
                        }
                );
    }

    void set(Integer state, String terminal, Action action) {
        ActionEntry entry = this.get(state);

        if (entry == null) {
            entry = new ActionEntry();
            this.put(state, entry);
        }

        entry.put(terminal, action);
    }

    void populateWithAccept(Integer fromIndex) {
        Action action = new Action(ACCEPT, noSuchState);
        this.set(fromIndex, TERMINATOR, action);
    }

    void populateWithShift(Integer fromIndex, String terminal, Integer toIndex) {
        Action action = new Action(SHIFT, toIndex);
        this.set(fromIndex, terminal, action);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Integer fromId : this.keySet()) {
            sb.append("\"" + fromId + "\": " + this.get(fromId) + ",");
        }
        sb.append("}");
        return sb.toString();
    }
}

class ActionEntry extends TreeMap<String, Action> implements Comparable<ActionEntry> {
    @Override
    public int compareTo(@NotNull ActionEntry other) {
        return Comparator
                .comparing(ActionEntry::toString)
                .compare(this, other);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String terminal : this.keySet()) {
            sb.append("\"" + terminal + "\": " + this.get(terminal) + ",");
        }
        sb.append("}");
        return sb.toString();
    }
}

class Action implements Comparable<Action> {
    private final Execution execution;

    // Refers to either a collection state index when shifting OR a production index when reducing.
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

    @Override
    public int hashCode() {
        return Objects.hash(execution, index);
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
        return "{ \"execution\": \"" + execution + "\", \"index\": \"" + index + "\"}";
    }
}

class GotoTable extends TreeMap<Integer, GotoEntry> {
    void set(Integer from, String nonTerminal, Integer to) {
        GotoEntry entry = this.get(from);

        if (entry == null) {
            entry = new GotoEntry();
            this.put(from, entry);
        }

        entry.put(nonTerminal, to);
    }

    Integer get(Integer from, String nonTerminal) {
        GotoEntry entry = this.get(from);
        if (entry == null) {
            return null;
        }

        Integer to = entry.get(nonTerminal);
        if (nonTerminal == null || to == null) {
            return null;
        }

        return to;
    }
}

class GotoEntry extends TreeMap<String, Integer> implements Comparable<GotoEntry> {
    @Override
    public int compareTo(@NotNull GotoEntry other) {
        return Comparator
                .comparing(GotoEntry::toString)
                .compare(this, other);
    }
}

class LR1Collection extends ListWithUniques<Items> {
    private final Transitions transitions;
    private final Items start;

    LR1Collection(Items start, Transitions transitions) {
        super(Items::compareTo);
        this.add(start);
        this.transitions = transitions;
        this.start = start;
    }

    LR1Collection(@NotNull Collection<? extends Items> c, Transitions transitions, Items start) {
        super(Items::compareTo);
        this.addAll(c);
        this.add(start);
        this.transitions = transitions;
        this.start = start;
    }

    Transitions getTransitions() {
        return transitions;
    }

    LR1Collection deepClone() {
        Transitions transitions = this.transitions.deepClone();
        return new LR1Collection(this, transitions, start);
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

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transitions);
    }

    public boolean contains(Transition transition) {
        return transitions.contains(transition) && super.contains(transition.getTo());
    }

    Items getStart() {
        return start;
    }
}

class Item extends Production {
    static final String MARKER = Character.toString('\u00B7');
    private final String lookahead;

    Item(String lookahead, String lhs, String... rhs) {
        super(lhs, rhs);
        this.lookahead = lookahead;
    }

    List<String> getAlpha() {
        int untilMarker = rhs.indexOf(MARKER);
        return rhs.subList(0, untilMarker);
    }

    List<String> getBeta() {
        int afterMarker = rhs.indexOf(MARKER) + 1;
        int toEnd = rhs.size();
        return rhs.subList(afterMarker, toEnd);
    }

    public int compareTo(@NotNull Item other) {
        return Comparator
                .comparing(Item::getLhs)
                .thenComparing(item -> item.getRhs().toString())
                .thenComparing(Item::getLookahead)
                .compare(this, other);
    }

    String getLookahead() {
        return lookahead;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lookahead);
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
        String rule = super.toString();
        return "[" + rule + ", " + lookahead + "]";
    }
}

class Items extends TreeSet<Item> implements Comparable<Items> {
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
        List<String> beta = item.getBeta();
        String nonTerminal = beta.get(0);
        List<String> alpha = item.getAlpha();
        List<String> subBeta = beta.subList(1, beta.size());

        List<String> newRhs = new ArrayList<>(alpha);
        newRhs.add(nonTerminal);
        newRhs.add(MARKER);
        newRhs.addAll(subBeta);
        return newRhs.toArray(new String[0]);
    }

    @NotNull
    private Predicate<Item> aboutToParseSymbol(String symbol) {
        return item -> {
            List<String> beta = item.getBeta();
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
                        List<String> betaLookahead = getBetaLookahead(item);
                        First first = firstMap.first(betaLookahead);
                        first.forEach(addCorrespondingItem(closure, rule));
                    }
            );
        };
    }

    @NotNull
    private static Consumer<String> addCorrespondingItem(Items closure, Production rule) {
        String lhs = rule.getLhs();
        List<String> rhs = rule.getRhs();

        return token -> {
            Item newItem = getItemAfterParsingSymbol(token, lhs, rhs);
            closure.add(newItem);
        };
    }

    @NotNull
    private static Item getItemAfterParsingSymbol(String token, String lhs, List<String> rhs) {
        List<String> gamma = new ArrayList<>();
        gamma.add(MARKER);
        gamma.addAll(rhs);
        return new Item(token, lhs, gamma.toArray(new String[0]));
    }

    @NotNull
    private static List<String> getBetaLookahead(Item item) {
        List<String> beta = item.getBeta();
        List<String> subBeta = beta.subList(1, beta.size());
        String lookahead = item.getLookahead();
        List<String> sequence = new ArrayList<>(subBeta);
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
            List<String> beta = item.getBeta();
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
    public int hashCode() {
        return Objects.hash(from, symbol, to);
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
}

class Transitions extends TreeSet<Transition> {
    Transitions deepClone() {
        Transitions clone = new Transitions();
        clone.addAll(this);
        return clone;
    }
}
