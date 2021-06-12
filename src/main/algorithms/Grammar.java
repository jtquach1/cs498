package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;

class Grammar {
    static final String EPSILON = Character.toString('\u025B');
    static final String GREEK_EPSILON = Character.toString('\u03B5');
    static final String TERMINATOR = "#";

    Set<String> nonTerminals;
    Set<String> terminals;
    String start;
    List<Production> productions;

    FirstMap firstMap;
    FollowMap followMap;
    LL1ParseTable ll1ParseTable;

    Grammar(Set<String> nonTerminals, Set<String> terminals, String start,
            List<Production> productions) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.start = start;
        this.productions = productions;
        nonTerminals.add(start);

        firstMap = first();
        followMap = follow();
        ll1ParseTable = generateLL1ParseTable();
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

    FollowMap follow() {
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

    private boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    LL1ParseTable generateLL1ParseTable() {
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

    public static void main(String[] args) throws Exception {
        String inputFile = args[0]; // supply file or do redirection?
//        inputFile = "E ::= T E'\n" +
//                "E' ::= + T E'\n" +
//                "E' ::= ε\n" +
//                "T ::= F T'\n" +
//                "T' ::= ε\n" +
//                "F ::= ( E )\n" +
//                "F ::= id\n";

        inputFile = "E ::= E + T\n" +
                "E ::= T\n" +
                "T ::= T * F\n" +
                "T ::= F\n" +
                "F ::= ( E )\n" +
                "F ::= id\n";

        List<List<String[]>> parsedInput = getParsedInput(inputFile);
        Grammar grammar = initializeGrammar(parsedInput);

        // Later: specify the grammar as stdin, sentence as CLI
        // OR have both be files- one for sentence, one for grammar
        String w = "id + id * id";
        if (!grammar.isLL1()) {
            Grammar converted = grammar.removeLeftRecursion();
        } else {
            LL1ParseOutput output = grammar.parseSentence(w);
//            System.out.println(output);
        }

        System.out.println("foo");

    }

    @NotNull
    private static List<List<String[]>> getParsedInput(String grammar) {
        grammar = grammar.replaceAll(GREEK_EPSILON, EPSILON);
        return Arrays
                .stream(grammar.split("\r?\n|\r"))
                .map(production -> Arrays
                        .stream(production.split("::="))
                        .map(side -> side.trim().split(" "))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @NotNull
    private static Grammar initializeGrammar(List<List<String[]>> parsedInput) {
        Set<String> nonTerminals = new TreeSet<>();
        Set<String> terminals = new TreeSet<>();
        String start = parsedInput.get(0).get(0)[0];
        List<Production> productions = new ArrayList<>();

        for (List<String[]> prod : parsedInput) {
            String lhs = prod.get(0)[0];
            nonTerminals.add(lhs);

            String[] rhs = prod.get(1);
            Production p = new Production(lhs, rhs);
            productions.add(p);
        }

        for (Production p : productions) {
            for (String symbol : p.getRhs()) {
                if (!nonTerminals.contains(symbol)) {
                    terminals.add(symbol);
                }
            }
        }

        return new Grammar(nonTerminals, terminals, start, productions);
    }

    LL1ParseOutput parseSentence(String delimitedBySpaces) throws Exception {
        Queue<String> sentence = this.handleSentence(delimitedBySpaces);
        Stack<String> stack = this.initializeStack();
        String symbol = sentence.dequeue();
        LL1ParseOutput output = this.initializeOutput(sentence, stack, symbol);

        while (true) {
            String top = stack.pop();
            Integer index = null;
            if (top.equals(symbol) && symbol.equals(TERMINATOR)) {
                break;
            } else if (isTerminal(top)) {
                if (top.equals(symbol)) {
                    symbol = sentence.dequeue();
                } else if (top.equals(EPSILON)) {
                    /* Replace the previous non-terminal on the stack with nothing.
                    EPSILON should not be a symbol in the sentence; it's only used for parsing. */
                } else {
                    throw new Exception("A " + symbol + " found where a " + top + " was expected");
                }
            } else if (isNonTerminal(top)) {
                index = ll1ParseTable.get(top, symbol);
                if (index != null) {
                    this.applyRuleAndReplaceTop(stack, index);
                } else {
                    throw new Exception("No rule to follow");
                }
            } else {
                throw new Exception("Illegal symbol " + top);
            }
            output.add(new LL1ParseOutputEntry(stack, sentence, index, symbol));
        }
        return output;
    }

    @NotNull
    private LL1ParseOutput initializeOutput(Queue<String> sentence, Stack<String> stack,
                                            String symbol) {
        LL1ParseOutput output = new LL1ParseOutput();
        output.add(new LL1ParseOutputEntry(stack, sentence, null, symbol));
        return output;
    }

    private boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    private void applyRuleAndReplaceTop(Stack<String> stack, Integer index) {
        Production rule = productions.get(index);
        List<String> reverse = new ArrayList<>(rule.getRhs());
        Collections.reverse(reverse);
        reverse.forEach(stack::push);
    }

    @NotNull
    private Queue<String> handleSentence(String w) {
        /* If the sentence doesn't end with the terminator, we will get an
        ArrayIndexOutOfBounds exception otherwise when doing LL1 parsing. */
        if (!w.endsWith(TERMINATOR)) {
            w += " " + TERMINATOR;
        }
        List<String> sentence = Arrays.asList(w.strip().split(" "));
        Queue<String> result = new Queue<>();
        result.addAll(sentence);
        return result;
    }

    @NotNull
    private Stack<String> initializeStack() {
        Stack<String> stack = new Stack<>();
        stack.push(TERMINATOR);
        stack.push(start);
        return stack;
    }

    boolean isLL1() {
        Set<String> nonTerminals = ll1ParseTable.keySet();
        for (String nonTerminal : nonTerminals) {
            LL1ParseTableEntry entry = ll1ParseTable.get(nonTerminal);
            Collection<Indices> values = entry.values();
            for (Indices indices : values) {
                if (indices.size() > 1) {
                    return false;
                }
            }
        }
        return true;
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
        Set<String> nonTerminals = new TreeSet<>(this.nonTerminals);
        Set<String> terminals = new TreeSet<>(this.terminals);
        List<Production> productions = new ArrayList<>(this.productions);
        return new Grammar(nonTerminals, terminals, start, productions);
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
        String firstSymbol = rhs.get(0);
        return firstSymbol.equals(EPSILON);
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
