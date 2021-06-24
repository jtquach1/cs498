package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Item.MARKER;

class LR1Collection extends TreeSet<Items> {
    
}

class Item extends Production {
    static final String MARKER = Character.toString('\u00B7');
    private final String lhs;
    private final ArrayList<String> rhs;
    private final String lookahead;

    Item(String lookahead, String lhs, String... rhs) {
        super(lhs, rhs);
        this.lhs = lhs;
        this.rhs = new ArrayList<>(Arrays.asList(rhs));
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
        return Objects.equals(lhs, other.lhs) &&
                Objects.equals(rhs, other.rhs) &&
                Objects.equals(lookahead, other.lookahead);
    }

    @Override
    public String toString() {
        String rule = super.toString();
        return "[" + rule + ", " + lookahead + "]";
    }
}

class Items extends TreeSet<Item> {
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
            if (beta == null) {
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

}