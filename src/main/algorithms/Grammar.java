package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
        String inputFile = args[0]; // supply file or do redirection?
        List<List<String[]>> parsedInput = getParsedInput(inputFile);
        Grammar grammar = initializeGrammar(parsedInput);
        populateGrammarWithProductions(parsedInput, grammar);
        populateGrammarWithNonTerminals(grammar);

        FirstMap firstMap = grammar.first();
        System.out.println(firstMap);
    }

    @NotNull
    private static List<List<String[]>> getParsedInput(String grammar) {
        String g = "E ::= T E'\n" +
                "E' ::= + T E'\n" +
                "E' ::= ε\n" +
                "T ::= F T'\n" +
                "T' ::= ε\n" +
                "F ::= ( E )\n" +
                "F ::= id\n";

        List<List<String[]>> parsedInput =
                Arrays
                        .stream(g.split("\r?\n|\r"))
                        .map(prod -> Arrays
                                .stream(prod.split("::="))
                                .map(side -> side.trim().split(" "))
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());
        return parsedInput;
    }

    @NotNull
    private static Grammar initializeGrammar(List<List<String[]>> parsedInput) {
        String start = parsedInput.get(0).get(0)[0];
        return new Grammar(start);
    }

    private static void populateGrammarWithNonTerminals(Grammar cfg) {
        Set<String> nonTerminals = cfg.getNonTerminals();
        for (Production p : cfg.getProductions()) {
            List<String> rhs = p.getRhs();
            List<String> terminals = rhs
                    .stream()
                    .filter(symbol -> !nonTerminals.contains(symbol))
                    .collect(Collectors.toList());
            terminals.forEach(cfg::addTerminals);
        }
    }

    void addTerminals(String... symbols) {
        terminals.addAll(Arrays.asList(symbols));
    }

    public Set<Production> getProductions() {
        return productions;
    }

    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    private static void populateGrammarWithProductions(List<List<String[]>> parsedInput,
                                                       Grammar cfg) {
        for (List<String[]> prod : parsedInput) {
            String lhs = prod.get(0)[0];
            String[] rhs = prod.get(1);

            cfg.addNonTerminals(lhs);
            Production p = new Production(lhs, rhs);
            cfg.addProductions(p);
        }
    }

    void addNonTerminals(String... symbols) {
        nonTerminals.addAll(Arrays.asList(symbols));
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

    First first(ArrayList<String> sequence) {
        String x1 = sequence.get(0);
        First F = new First();
        F.addAll(this.get(x1));
        int i = 1;
        int n = sequence.size();
        while (F.contains(EPSILON) && i < n) {
            F.remove(EPSILON);
            String xi = sequence.get(i);
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