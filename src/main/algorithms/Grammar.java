package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;

class Grammar {
    static final String EPSILON = Character.toString('\u025B');
    static final String TERMINATOR = "#";

    Set<String> nonTerminals;
    Set<String> terminals;
    String start;
    List<Production> productions;

    Grammar(String start) {
        nonTerminals = new TreeSet<>();
        terminals = new TreeSet<>();
        this.start = start;
        productions = new ArrayList<>();
        nonTerminals.add(start);
    }

    public static void main(String[] args) throws Exception {
        String inputFile = args[0]; // supply file or do redirection?
        List<List<String[]>> parsedInput = getParsedInput(inputFile);
        Grammar cfg = initializeGrammar(parsedInput);
        populateGrammarWithProductions(parsedInput, cfg);
        populateGrammarWithNonTerminals(cfg);

        FirstMap firstMap = cfg.first();
        FollowMap followMap = cfg.follow(firstMap);
        LL1ParseTable ll1ParseTable = cfg.generateLL1ParseTable(firstMap, followMap);

//        System.out.println(firstMap);
//        System.out.println(followMap);
//        System.out.println(ll1ParseTable);

//        System.out.println(cfg.removeLeftRecursion());

        String w = "id + id * id";
        cfg.parseSentence(ll1ParseTable, w);

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

    public List<Production> getProductions() {
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

    FollowMap follow(FirstMap firstMap) {
        FollowMap followMap = new FollowMap();
        nonTerminals.forEach(followMap::initializeFollowSetOfNonTerminal);
        followMap.get(start).add(TERMINATOR);
        FollowMap previous = followMap.deepClone();

        boolean newSymbolsAreBeingAdded = true;
        while (newSymbolsAreBeingAdded) {
            for (Production p : productions) {
                String lhs = p.getLhs();
                List<String> rhs = p.getRhs();
                int n = rhs.size();
                for (int i = 0; i < n; i++) {
                    String symbol = rhs.get(i);
                    if (!isNonTerminal(symbol)) {
                        continue;
                    }
                    Follow followOfSymbol = followMap.get(symbol);
                    List<String> subsequence = rhs.subList(i + 1, n);
                    First firstOfSubsequence = firstMap.first(subsequence);
                    followOfSymbol.addAll(firstOfSubsequence);
                    followOfSymbol.remove(EPSILON);
                    if (i == n - 1 || firstOfSubsequence.contains(EPSILON)) {
                        followOfSymbol.addAll(followMap.get(lhs));
                    }
                }
            }
            newSymbolsAreBeingAdded = !followMap.equals(previous);
            previous = followMap.deepClone();
        }
        return followMap;
    }

    private boolean isNonTerminal(String top) {
        return nonTerminals.contains(top);
    }

    LL1ParseTable generateLL1ParseTable(FirstMap firstMap, FollowMap followMap) {
        LL1ParseTable table = new LL1ParseTable();

        for (String nonTerminal : nonTerminals) {
            List<Production> subset = getSubsetOfProductions(nonTerminal);

            for (Production p : subset) {
                int productionIndex = productions.indexOf(p);
                First firstOfRhs = firstMap.first(p.getRhs());

                for (String firstTerminal : firstOfRhs) {
                    if (!firstTerminal.equals(EPSILON)) {
                        table.set(nonTerminal, firstTerminal, productionIndex);
                    }
                    if (firstOfRhs.contains(EPSILON)) {
                        Follow followOfNonTerminal = followMap.get(nonTerminal);

                        for (String followTerminal : followOfNonTerminal) {
                            table.set(nonTerminal, followTerminal, productionIndex);
                        }
                    }
                }
            }
        }
        return table;
    }

    @NotNull
    private List<Production> getSubsetOfProductions(String nonTerminal) {
        return productions
                .stream()
                .filter(p -> p.getLhs().equals(nonTerminal))
                .collect(Collectors.toList());
    }

    void parseSentence(LL1ParseTable table, String w) throws Exception {
        List<String> sentence = this.handleSentence(w);
        Stack<String> stack = this.initializeStack();
        String symbol = sentence.get(0);
        int i = 1;

        while (true) {
            String top = stack.pop();
            if (top.equals(symbol) && symbol.equals(TERMINATOR)) {
                break;
            } else if (isTerminal(top)) {
                if (top.equals(symbol)) {
                    symbol = sentence.get(i++);
                } else if (top.equals(EPSILON)) {
                    /* Replace the previous non-terminal on the stack with nothing.
                    EPSILON should not be a symbol in the sentence; it's only used for parsing. */
                } else {
                    throw new Exception("A " + symbol + " found where a " + top + " was expected");
                }
            } else if (isNonTerminal(top)) {
                Integer index = table.get(top, symbol);
                if (index != null) {
                    this.applyRuleAndReplaceTop(stack, index);
                } else {
                    throw new Exception("No rule to follow");
                }
            } else {
                throw new Exception("Illegal symbol " + top);
            }
        }
    }

    private boolean isTerminal(String top) {
        return terminals.contains(top);
    }

    private void applyRuleAndReplaceTop(Stack<String> stack, Integer index) {
        Production rule = productions.get(index);
        List<String> reverse = new ArrayList<>(rule.getRhs());
        Collections.reverse(reverse);
        reverse.forEach(stack::push);
    }

    @NotNull
    private List<String> handleSentence(String w) throws Exception {
        // We will get an ArrayIndexOutOfBounds exception otherwise.
        if (!w.endsWith(TERMINATOR)) {
            throw new Exception("Sentence `" + w + "` does not end with " + TERMINATOR);
        }
        List<String> sentence = Arrays.asList(w.strip().split(" "));
        return sentence;
    }

    @NotNull
    private Stack<String> initializeStack() {
        Stack<String> stack = new Stack<>();
        stack.push(TERMINATOR);
        stack.push(start);
        return stack;
    }

    Grammar removeLeftRecursion() {
        Grammar cfg = this.deepClone();
        TreeMap<String, Integer> enumerations = new TreeMap<>();
        arbitrarilyEnumerateNonTerminals(enumerations);

        for (int i = 0; i < nonTerminals.size(); i++) {
            for (int j = 0; j < i; j++) {
                List<Production> leftMostIsNonTerminal = new ArrayList<>();
            }
        }

        return cfg;
    }

    private void arbitrarilyEnumerateNonTerminals(TreeMap<String, Integer> enumerations) {
        int i = 0;
        for (String nonTerminal : nonTerminals) {
            enumerations.put(nonTerminal, i);
            i++;
        }
    }

    Grammar deepClone() {
        Grammar clone = new Grammar(start);
        clone.nonTerminals.addAll(nonTerminals);
        clone.terminals.addAll(terminals);
        clone.productions.addAll(productions);
        return clone;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lhs + " ::= ");
        for (int i = 0; i < rhs.size(); i++) {
            String symbol = rhs.get(i);
            if (i == rhs.size() - 1) {
                sb.append(symbol);
            } else {
                sb.append(symbol + " ");
            }
        }
        return sb.toString();
    }
}

class Productions extends ArrayList<Production> {
}

class First extends TreeSet<String> {
    public First(String... symbols) {
        super();
        this.addAll(Arrays.asList(symbols));
    }

    First deepClone() {
        First clone = new First();
        clone.addAll(this);
        return clone;
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
    }

    void addFirstSetOfSequenceToFirstSetOfSymbol(Production p) {
        String lhs = p.getLhs();
        First set = this.get(lhs);
        set.addAll(this.first(p.getRhs()));
    }

    First first(List<String> sequence) {
        First F = new First();
        // An empty sequence has no characters
        if (sequence.size() != 0) {
            String firstSymbol = sequence.get(0);
            F.addAll(this.get(firstSymbol));
            int i = 1;
            int n = sequence.size();
            while (F.contains(EPSILON) && i < n) {
                F.remove(EPSILON);
                String symbol = sequence.get(i);
                F.addAll(this.get(symbol));
                i++;
            }
        }
        return F;
    }

}

class Follow extends TreeSet<String> {
    public Follow(String... symbols) {
        super();
        this.addAll(Arrays.asList(symbols));
    }
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

    void initializeFollowSetOfNonTerminal(String symbol) {
        Follow set = new Follow();
        this.put(symbol, set);
    }
}
