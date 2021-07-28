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
        return "{" +
                "\"actionTable\": " + actionTable +
                ", \"gotoTable\": " + gotoTable +
                ", \"startIndex\": " + startIndex +
                '}';
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

class Action implements Comparable<Action> {
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
        return "{ \"execution\":\"" + execution + "\", \"index\":\"" + index + "\"}";
    }
}

class GotoTable extends Table<Integer, String, Integer> {
}

class LR1ParseOutput extends ArrayList<LR1ParseOutputEntry> {
    LR1ParseOutput() {
    }

    LR1ParseOutput(LR1ParseOutputEntry... entries) {
        super(Arrays.asList(entries));
    }
}

class LR1ParseOutputEntry extends OutputEntry<Pair, String, Action> {
    // Contains states represented as Integers and non-terminals represented as Strings.
    LR1ParseOutputEntry(Stack<Pair> stack, Queue<String> input, Action output, String cursor) {
        super(stack, input, output, cursor);
    }
}

class Pair implements Comparable<Pair> {
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
        String space = symbol.equals(noSuchSymbol) ? "" : " ";
        return symbol + space + stateIndex;
    }
}

class LR1Collection extends ListWithUniques<Items> {
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
        String collection = super.toString();

        return "{" +
                "\"collection\": " + collection +
                ", \"transitions\":" + transitions +
                ", \"start\":" + start +
                '}';
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
        String symbols = rhs
                .stream()
                .map(symbol -> "\"" + symbol + "\"")
                .collect(Collectors.joining(", "));

        return "{" +
                "\"lhs\":\"" + lhs + "\", " +
                "\"rhs\":[" + symbols + "], " +
                "\"lookahead\":\"" + lookahead + "\"" +
                "}";
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
        return "{" +
                "\"from\":" + from +
                ", \"symbol\": \"" + symbol + "\"" +
                ", \"to\":" + to +
                '}';
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
