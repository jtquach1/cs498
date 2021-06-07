package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    TreeMap<String, TreeSet<String>> first(String symbol) {
        TreeMap<String, TreeSet<String>> map = new TreeMap<>();

        return map;
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
}
