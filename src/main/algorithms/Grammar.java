package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;

class Grammar {
    static final String EPSILON = Character.toString('\u025B');
    static final String GREEK_EPSILON = Character.toString('\u03B5');
    static final String TERMINATOR = "#";

    private final Symbols nonTerminals;
    private final Symbols terminals;
    private final String start;
    private final Productions productions;

    Grammar(Symbols nonTerminals, Symbols terminals, String start, Productions productions) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.start = start;
        this.productions = productions;
        nonTerminals.add(start);
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

        Productions productions = getProductionsFromFile(inputFile);
        Grammar grammar = initializeGrammar(productions);

        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable ll1ParseTable = grammar.generateLL1ParseTable(firstMap, followMap);

        // Later: specify the grammar as stdin, sentence as CLI
        // OR have both be files- one for sentence, one for grammar
        String w = "id + id * id";

        if (!grammar.isLL1(ll1ParseTable)) {
            Grammar converted = grammar.removeLeftRecursion();
            System.out.println(converted);
        } else {
            LL1ParseOutput output = grammar.parseSentence(ll1ParseTable, w);
            System.out.println(output);
        }

    }

    @NotNull
    private static Productions getProductionsFromFile(String grammar) {
        grammar = grammar.replaceAll(GREEK_EPSILON, EPSILON);
        String[] lines = grammar.split("\r?\n|\r");
        Productions productions = new Productions();

        for (String line : lines) {
            String[] sides = line.split("::=");
            String lhs = sides[0].trim();
            String[] rhs = sides[1].trim().split(" ");
            Production production = new Production(lhs, rhs);
            productions.add(production);
        }

        return productions;
    }

    @NotNull
    private static Grammar initializeGrammar(Productions productions) {
        Symbols nonTerminals = new Symbols();
        Symbols terminals = new Symbols();
        String start = productions.get(0).getLhs();

        for (Production production : productions) {
            String lhs = production.getLhs();
            nonTerminals.add(lhs);

            List<String> rhs = production.getRhs();
            for (String symbol : rhs) {
                if (!nonTerminals.contains(symbol)) {
                    terminals.add(symbol);
                }
            }
        }

        return new Grammar(nonTerminals, terminals, start, productions);
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

    private boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    LL1ParseTable generateLL1ParseTable(FirstMap firstMap, FollowMap followMap) {
        LL1ParseTable table = new LL1ParseTable();

        for (String nonTerminal : nonTerminals) {
            Productions subset = getSubsetOfProductions(nonTerminal);

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
    private Productions getSubsetOfProductions(String nonTerminal) {
        return productions
                .stream()
                .filter(p -> p.getLhs().equals(nonTerminal))
                .collect(Collectors.toCollection(Productions::new));
    }

    LL1ParseOutput parseSentence(LL1ParseTable table, String delimitedBySpaces) throws Exception {
        Queue<String> sentence = initializeSentence(delimitedBySpaces);
        Stack<String> stack = initializeStack();
        String symbol = sentence.dequeue();
        LL1ParseOutput output = initializeOutput(sentence, stack, symbol);

        while (true) {
            String top = stack.pop();
            Integer index = null;
            if (isTerminator(symbol, top)) {
                break;
            } else if (isTerminal(top)) {
                symbol = getNextSymbol(sentence, symbol, top);
            } else if (isNonTerminal(top)) {
                index = replaceTopWithRule(table, stack, symbol, top);
            } else {
                throw new Exception("Illegal symbol " + top);
            }
            output.add(new LL1ParseOutputEntry(stack, sentence, index, symbol));
        }
        return output;
    }

    private static boolean isTerminator(String symbol, String top) {
        return top.equals(symbol) && symbol.equals(TERMINATOR);
    }

    private static String getNextSymbol(Queue<String> sentence, String symbol, String top)
            throws Exception {
        if (top.equals(symbol)) {
            symbol = sentence.dequeue();
        } else if (top.equals(EPSILON)) {
            /* Replace the previous non-terminal on the stack with nothing.
            EPSILON should not be a symbol in the sentence; it's only used for parsing. */
        } else {
            throw new Exception("A " + symbol + " found where a " + top + " was expected");
        }
        return symbol;
    }

    @NotNull
    private Integer replaceTopWithRule(LL1ParseTable table, Stack<String> stack,
                                       String symbol, String top) throws Exception {
        Integer index;
        index = table.get(top, symbol);
        if (index != null) {
            Production rule = productions.get(index);
            List<String> reverse = new ArrayList<>(rule.getRhs());
            Collections.reverse(reverse);
            reverse.forEach(stack::push);
        } else {
            throw new Exception("No rule to follow");
        }
        return index;
    }

    @NotNull
    private static LL1ParseOutput initializeOutput(Queue<String> sentence, Stack<String> stack,
                                                   String symbol) {
        LL1ParseOutput output = new LL1ParseOutput();
        output.add(new LL1ParseOutputEntry(stack, sentence, null, symbol));
        return output;
    }

    private boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    @NotNull
    private static Queue<String> initializeSentence(String w) {
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

    boolean isLL1(LL1ParseTable table) {
        for (String nonTerminal : nonTerminals) {
            LL1ParseTableEntry entry = table.get(nonTerminal);
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
        Enumerations enums = enumerateNonTerminals();
        int n = nonTerminals.size();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                cfg.eliminateIndirectLeftRecursion(enums, i, j);
            }
            cfg.eliminateDirectLeftRecursion(enums, i);
        }

        return cfg;
    }

    private void eliminateDirectLeftRecursion(Enumerations enums, int i) {
        Productions alphaForms = getAlphaForms(isLeftRecursiveAlphaForm(enums, i));
        Productions betaForms = getBetaForms(isNonLeftRecursiveBetaForm(enums, i));

        for (Production alphaForm : alphaForms) {
            List<String> rhs = alphaForm.getRhs();
            List<String> alpha = rhs.subList(1, rhs.size());
            String y = alphaForm.getLhs();
            String yPrime = y + "'";
            productions.remove(alphaForm);

            for (Production betaForm : betaForms) {
                List<String> beta = betaForm.getRhs();
                String[] newRhs = getNewRhs(beta, Collections.singleton(yPrime));
                Production newProduction = new Production(y, newRhs);
                productions.add(newProduction);

                newRhs = getNewRhs(alpha, Collections.singleton(yPrime));
                newProduction = new Production(yPrime, newRhs);
                productions.add(newProduction);

                newRhs = new String[]{EPSILON};
                newProduction = new Production(yPrime, newRhs);
                productions.add(newProduction);

                productions.remove(betaForm);
            }
        }
    }

    private static Predicate<Production> isNonLeftRecursiveBetaForm(Enumerations enums, int j) {
        return production -> {
            String expectedY = enums.get(j);
            String actualY = production.getLhs();
            boolean doesNotContainSelf = !production.getRhs().get(0).equals(actualY);
            return expectedY.equals(actualY) && doesNotContainSelf;
        };
    }

    private static Predicate<Production> isLeftRecursiveAlphaForm(Enumerations enums, int i) {
        return isAlphaForm(enums, i, i);
    }

    private void eliminateIndirectLeftRecursion(Enumerations enums, int i, int j) {
        Productions alphaForms = getAlphaForms(isAlphaForm(enums, i, j));
        Productions betaForms = getBetaForms(isBetaForm(enums, j));

        for (Production alphaForm : alphaForms) {
            List<String> rhs = alphaForm.getRhs();
            List<String> alpha = rhs.subList(1, rhs.size());
            String xi = alphaForm.getLhs();
            productions.remove(alphaForm);

            for (Production betaForm : betaForms) {
                List<String> beta = betaForm.getRhs();
                String[] newRhs = getNewRhs(beta, alpha);
                Production betaAlphaForm = new Production(xi, newRhs);
                productions.add(betaAlphaForm);
            }
        }
    }

    @NotNull
    private Productions getBetaForms(Predicate<Production> isBetaForm) {
        return productions
                .stream()
                .filter(isBetaForm)
                .collect(Collectors.toCollection(Productions::new));
    }

    @NotNull
    private Productions getAlphaForms(Predicate<Production> isAlphaForm) {
        return productions
                .stream()
                .filter(isAlphaForm)
                .collect(Collectors.toCollection(Productions::new));
    }

    @NotNull
    private static String[] getNewRhs(Collection<String> first, Collection<String> second) {
        List<String> newRhs = new ArrayList<>();
        newRhs.addAll(first);
        newRhs.addAll(second);
        return newRhs.toArray(new String[0]);
    }

    private static Predicate<Production> isBetaForm(Enumerations enums, int j) {
        return production -> {
            String expectedXj = enums.get(j);
            String actualXj = production.getLhs();
            return expectedXj.equals(actualXj);
        };
    }

    private static Predicate<Production> isAlphaForm(Enumerations enums, int i, int j) {
        return production -> {
            String expectedXi = enums.get(i);
            String actualXi = production.getLhs();
            String expectedXj = enums.get(j);
            String actualXj = production.getRhs().get(0);
            return expectedXi.equals(actualXi) && expectedXj.equals(actualXj);
        };
    }

    private Enumerations enumerateNonTerminals() {
        Enumerations enums = new Enumerations();

        List<String> orderedNonTerminals = productions
                .stream()
                .map(Production::getLhs)
                .collect(Collectors.toList());

        for (String nonTerminal : orderedNonTerminals) {
            if (!enums.contains(nonTerminal)) {
                enums.add(nonTerminal);
            }
        }

        return enums;
    }

    Grammar deepClone() {
        Symbols nonTerminals = new Symbols(this.nonTerminals);
        Symbols terminals = new Symbols(this.terminals);
        Productions productions = new Productions(this.productions);
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

class Enumerations extends ArrayList<String> {
}

class Symbols extends TreeSet<String> {
    public Symbols() {
        super();
    }

    public Symbols(@NotNull Collection<? extends String> symbols) {
        super(symbols);
    }
}


class Productions extends ArrayList<Production> {
    public Productions() {
        super();
    }

    public Productions(@NotNull Collection<? extends Production> productions) {
        super(productions);
    }

    @Override
    public boolean add(Production production) {
        if (!contains(production)) {
            super.add(production);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Production> productions) {
        boolean hasChanged = false;
        for (Production production : productions) {
            hasChanged |= add(production);
        }
        return hasChanged;
    }
}
