package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Item.MARKER;

class LR1ParseTable {
    private final ActionTable actionTable;
    private final GotoTable gotoTable;

    LR1ParseTable(ActionTable actionTable, GotoTable gotoTable) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
    }
}

class ActionTable extends TreeMap<Integer, ActionEntry> {
    void set(Integer state, String terminal, Action action) {
        ActionEntry entry = this.get(state);

        if (entry == null) {
            entry = new ActionEntry();
            this.put(state, entry);
        }

        entry.put(terminal, action);
    }

    Action get(Integer state, String terminal) {
        ActionEntry entry = this.get(state);
        if (entry == null) {
            return null;
        }

        Action action = entry.get(terminal);
        if (terminal == null || action == null) {
            return null;
        }

        // In case of conflicts, we just want to return one value
        return action;
    }
}

class ActionEntry extends TreeMap<String, Action> implements Comparable<ActionEntry> {
    @Override
    public int compareTo(@NotNull ActionEntry other) {
        return Comparator
                .comparing(ActionEntry::toString)
                .compare(this, other);
    }
}

class Action implements Comparable<Action> {
    private final Execution execution;
    private final Integer state;

    Action(Execution execution, Integer state) {
        this.execution = execution;
        this.state = state;
    }

    @Override
    public int compareTo(@NotNull Action other) {
        return Comparator
                .comparing(Action::getExecution)
                .thenComparing(Action::getState)
                .compare(this, other);
    }

    public Execution getExecution() {
        return execution;
    }

    public Integer getState() {
        return state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(execution, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action other = (Action) o;
        return execution == other.execution && state.equals(other.state);
    }

    enum Execution {
        SHIFT, REDUCE, ACCEPT
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

        // In case of conflicts, we just want to return one value
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
    private final GotoMap gotoMap;

    LR1Collection(GotoMap gotoMap) {
        super(Items::compareTo);
        this.gotoMap = gotoMap;
    }

    LR1Collection(@NotNull Collection<? extends Items> c, GotoMap gotoMap) {
        super(Items::compareTo);
        this.addAll(c);
        this.gotoMap = gotoMap;
    }

    GotoMap getGotoMap() {
        return gotoMap;
    }

    LR1Collection deepClone() {
        GotoMap map = this.gotoMap.deepClone();
        LR1Collection clone = new LR1Collection(map);
        clone.addAll(this);
        return clone;
    }

    void add(Items from, String symbol, Items to) {
        super.add(to);
        gotoMap.put(to, new Goto(from, symbol));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LR1Collection items = (LR1Collection) o;
        return gotoMap.equals(items.gotoMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gotoMap);
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
        return Objects.hash(lhs, rhs, lookahead);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item other = (Item) o;
        return lookahead.equals(other.lookahead);
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

class Goto implements Comparable<Goto> {
    private final Items from;
    private final String symbol;

    Goto(Items from, String symbol) {
        this.from = from;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(@NotNull Goto other) {
        return Comparator
                .comparing(Goto::getFrom)
                .thenComparing(Goto::getSymbol)
                .compare(this, other);
    }

    Items getFrom() {
        return from;
    }

    String getSymbol() {
        return symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goto other = (Goto) o;
        return from.equals(other.from) && symbol.equals(other.symbol);
    }
}

class GotoMap extends TreeMap<Items, Goto> {
    GotoMap deepClone() {
        GotoMap clone = new GotoMap();
        clone.putAll(this);
        return clone;
    }
}
