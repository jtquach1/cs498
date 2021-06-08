package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static algorithms.Grammar.EPSILON;

class Grammar {
    static final String EPSILON = Character.toString('\u025B');

    Set<String> nonTerminals;
    Set<String> terminals;
    String start;
    Set<Production> productions;

    Grammar(String start) {
        nonTerminals = new TreeSet<>();
        terminals = new TreeSet<>();
        this.start = start;
        productions = new TreeSet<>();
        nonTerminals.add(start);
    }

    public static void main(String[] args) {
        Grammar cfg = new Grammar("E");
        cfg.addNonTerminals("E'", "T", "T'", "F");
        cfg.addTerminals("+", EPSILON, "*", "(", ")", "id");
        cfg.addProductions(
                new Production("E", "T", "E'"),
                new Production("E'", "+", "T", "E'"),
                new Production("E'", EPSILON),
                new Production("T", "F", "T'"),
                new Production("T'", "*", "F", "T'"),
                new Production("T'", EPSILON),
                new Production("F", "(", "id", ")"),
                new Production("F", "id")
        );
        System.out.println(cfg);

        FirstMap firstMap = cfg.first();
        System.out.println(firstMap);
    }

    void addNonTerminals(String... symbols) {
        nonTerminals.addAll(Arrays.asList(symbols));
    }

    void addTerminals(String... symbols) {
        terminals.addAll(Arrays.asList(symbols));
    }

    void addProductions(Production... productions) {
        this.productions.addAll(Arrays.asList(productions));
    }

    FirstMap first() {
        FirstMap map = new FirstMap();
        terminals.forEach(map::initializeFirstSetOfTerminal);
        nonTerminals.forEach(map::initializeFirstSetOfNonTerminal);
        productions
                .stream()
                .filter(Production::beginsWithEpsilon)
                .forEach(map::addEpsilonToFirstSetOfSymbol);
        FirstMap previous = map.deepClone();

        boolean newSymbolsAreBeingAdded = true;
        while (newSymbolsAreBeingAdded) {
            productions.forEach(map::addFirstSetOfSequenceToFirstSetOfSymbol);
            newSymbolsAreBeingAdded = !map.equals(previous);
            previous = map.deepClone();
        }

        return map;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Set<Production> getProductions() {
        return productions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminals, terminals, start, productions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grammar grammar = (Grammar) o;
        return Objects.equals(nonTerminals, grammar.nonTerminals) &&
                Objects.equals(terminals, grammar.terminals) &&
                Objects.equals(start, grammar.start) &&
                Objects.equals(productions, grammar.productions);
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }
}

class Production implements Comparable<Production> {
    private final String lhs;
    private final ArrayList<String> rhs;

    Production(String lhs, ArrayList<String> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    Production(String lhs, String... rhs) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>(Arrays.asList(rhs));
    }

    @Override
    public int compareTo(@NotNull Production other) {
        return Comparator
                .comparing(Production::getLhs)
                .thenComparing(p -> p.getRhs().toString())
                .compare(this, other);
    }

    String getLhs() {
        return lhs;
    }

    ArrayList<String> getRhs() {
        return rhs;
    }

    boolean beginsWithEpsilon() {
        return rhs.get(0).equals(EPSILON);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production other = (Production) o;
        return lhs.equals(other.lhs) && rhs.equals(other.rhs);
    }
}

class First extends TreeSet<String> {
    public First(String... symbols) {
        super();
        this.addAll(Arrays.asList(symbols));
    }
}

class FirstMap extends TreeMap<String, First> {
    FirstMap() {
        super();
    }

    FirstMap deepClone() {
        FirstMap mapClone = new FirstMap();
        for (String symbol : this.keySet()) {
            First old = this.get(symbol);
            First clone = new First();
            clone.addAll(old);
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

    void addEpsilonToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        First set = this.get(lhs);
        set.add(EPSILON);
        this.put(lhs, set);
    }

    void addFirstSetOfSequenceToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        First set = this.get(lhs);
        set.addAll(this.first(p.getRhs()));
        this.put(lhs, set);
    }

    First first(ArrayList<String> rhs) {
        String x1 = rhs.get(0);
        First F = new First();
        F.addAll(this.get(x1));
        int i = 1;
        int n = rhs.size();
        while (F.contains(EPSILON) && i < n) {
            F.remove(EPSILON);
            String xi = rhs.get(i);
            F.addAll(this.get(xi));
            i++;
        }
        return F;
    }

}

class Follow extends TreeSet<String> {
}

class FollowMap extends TreeMap<String, Follow> {
    FollowMap() {
        super();
    }

    FollowMap deepClone() {
        FollowMap mapClone = new FollowMap();
        for (String symbol : this.keySet()) {
            Follow old = this.get(symbol);
            Follow clone = new Follow();
            clone.addAll(old);
            mapClone.put(symbol, clone);
        }
        return mapClone;
    }
}