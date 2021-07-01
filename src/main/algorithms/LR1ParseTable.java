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

    LR1ParseTable(ActionTable actionTable, GotoTable gotoTable) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
    }
}

class ActionTable extends TreeMap<Integer, ActionEntry> {
    static Integer noNextState = -1;

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
        Action action = new Action(ACCEPT, noNextState);
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

    // Refers to either a collection state index or a production index.
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

    public Execution getExecution() {
        return execution;
    }

    public Integer getIndex() {
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
    private final Transitions transitions;

    LR1Collection(Transitions transitions) {
        super(Items::compareTo);
        this.transitions = transitions;
    }

    LR1Collection(@NotNull Collection<? extends Items> c, Transitions transitions) {
        super(Items::compareTo);
        this.addAll(c);
        this.transitions = transitions;
    }

    Transitions getTransitions() {
        return transitions;
    }

    LR1Collection deepClone() {
        Transitions transitions = this.transitions.deepClone();
        LR1Collection clone = new LR1Collection(transitions);
        clone.addAll(this);
        return clone;
    }

    void add(Items from, String symbol, Items to) {
        super.add(to);
        transitions.add(new Goto(from, symbol, to));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LR1Collection items = (LR1Collection) o;
        return transitions.equals(items.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transitions);
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

class Goto implements Comparable<Goto> {
    private final Items from;
    private final String symbol;
    private final Items to;

    Goto(Items from, String symbol, Items to) {
        this.from = from;
        this.symbol = symbol;
        this.to = to;
    }

    @Override
    public int compareTo(@NotNull Goto other) {
        return Comparator
                .comparing(Goto::getFrom)
                .thenComparing(Goto::getSymbol)
                .thenComparing(Goto::getTo)
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
        Goto other = (Goto) o;
        return Objects.equals(from, other.from) &&
                Objects.equals(symbol, other.symbol) &&
                Objects.equals(to, other.to);
    }
}

class Transitions extends TreeSet<Goto> {
    Transitions deepClone() {
        Transitions clone = new Transitions();
        clone.addAll(this);
        return clone;
    }
}
