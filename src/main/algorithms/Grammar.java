package algorithms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;
import static algorithms.Item.MARKER;
import static algorithms.Utility.getProductionFromLine;

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

        Grammar grammar = initializeGrammar(inputFile);

        FirstMap firstMap = grammar.first();
        FollowMap followMap = grammar.follow(firstMap);
        LL1ParseTable ll1ParseTable = grammar.generateLL1ParseTable(firstMap, followMap);

        // Later: specify the grammar as stdin, sentence as CLI
        // OR have both be files- one for sentence, one for grammar
        String w = "id + id * id";

        if (!grammar.isLL1(ll1ParseTable)) {
            grammar = grammar.removeLeftRecursion();

            firstMap = grammar.first();
            followMap = grammar.follow(firstMap);
            ll1ParseTable = grammar.generateLL1ParseTable(firstMap, followMap);
        }

        LL1ParseOutput output = grammar.parseSentence(ll1ParseTable, w);
        System.out.println(output);
    }

    @NotNull
    private static Grammar initializeGrammar(String grammar) {
        grammar = grammar.replaceAll(GREEK_EPSILON, EPSILON);
        String[] lines = grammar.split("\r?\n|\r");

        Symbols nonTerminals = new Symbols();
        Symbols terminals = new Symbols();
        String start = null;
        Productions productions = new Productions();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Production production = getProductionFromLine(line);
            productions.add(production);
            nonTerminals.add(production.getLhs());

            if (i == 0) {
                start = production.getLhs();
            }
        }

        productions.forEach(production -> production
                .getRhs()
                .stream()
                .filter(symbol -> !nonTerminals.contains(symbol))
                .forEach(terminals::add));

        return new Grammar(nonTerminals, terminals, start, productions);
    }

    FirstMap first() {
        FirstMap map = new FirstMap();
        terminals.forEach(map::initializeFirstSetOfTerminal);
        nonTerminals.forEach(map::initializeFirstSetOfNonTerminal);
        productions
                .stream()
                .filter(Production::beginsWithEpsilon)
                .forEach(map::addEpsilonToFirstSetOfNonTerminal);
        FirstMap previous;

        boolean newSymbolsAreBeingAdded = true;
        while (newSymbolsAreBeingAdded) {
            previous = map.deepClone();
            productions.forEach(map::addFirstSetOfSequenceToFirstSetOfSymbol);
            newSymbolsAreBeingAdded = !map.equals(previous);
        }

        return map;
    }

    FollowMap follow(FirstMap firstMap) {
        FollowMap followMap = initializeFollowMap();
        FollowMap previous;

        boolean newSymbolsAreBeingAdded = true;
        while (newSymbolsAreBeingAdded) {
            previous = followMap.deepClone();
            productions.forEach(populateFollowMap(firstMap, followMap));
            newSymbolsAreBeingAdded = !followMap.equals(previous);
        }
        return followMap;
    }

    @NotNull
    private Consumer<Production> populateFollowMap(FirstMap firstMap, FollowMap followMap) {
        return production -> {
            String lhs = production.getLhs();
            List<String> rhs = production.getRhs();
            int n = rhs.size();

            for (int i = 0; i < n; i++) {
                Follow followOfSymbol = getFollowOfSymbol(followMap, rhs, i);
                if (followOfSymbol == null) continue;

                List<String> subsequence = rhs.subList(i + 1, n);
                First firstOfSubsequence = firstMap.first(subsequence);
                followOfSymbol.addAll(firstOfSubsequence);
                followOfSymbol.remove(EPSILON);

                if (i == n - 1 || firstOfSubsequence.contains(EPSILON)) {
                    followOfSymbol.addAll(followMap.get(lhs));
                }
            }
        };
    }

    @Nullable
    private Follow getFollowOfSymbol(FollowMap followMap, List<String> rhs, int i) {
        String symbol = rhs.get(i);

        // Terminals do not have follow sets.
        if (!isNonTerminal(symbol)) {
            return null;
        }

        return followMap.get(symbol);
    }

    private boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    @NotNull
    private FollowMap initializeFollowMap() {
        FollowMap followMap = new FollowMap();
        nonTerminals.forEach(followMap::initializeFollowSetOfNonTerminal);
        followMap.get(start).add(TERMINATOR);
        return followMap;
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
        int n = enums.size();

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
            // Remove Y ::= Y α
            productions.remove(alphaForm);

            List<String> rhs = alphaForm.getRhs();
            List<String> alpha = rhs.subList(1, rhs.size());
            String y = alphaForm.getLhs();
            String yPrime = y + "'";

            for (Production betaForm : betaForms) {
                List<String> beta = betaForm.getRhs();

                boolean removedRecursion = addBetaForm(y, yPrime, beta);
                removedRecursion |= addAlphaForm(alpha, yPrime);
                removedRecursion |= addEpsilonForm(yPrime);

                if (removedRecursion) {
                    nonTerminals.add(yPrime);
                    terminals.add(EPSILON);
                }

                // Remove Y ::= β
                productions.remove(betaForm);
            }
        }
    }

    private boolean addBetaForm(String y, String yPrime, List<String> beta) {
        // Add Y ::= β Y'
        String[] newRhs = getNewRhs(beta, Collections.singleton(yPrime));
        Production newProduction = new Production(y, newRhs);
        return productions.add(newProduction);
    }

    private boolean addAlphaForm(List<String> alpha, String yPrime) {
        // Add Y' ::= α Y'
        String[] rhs = getNewRhs(alpha, Collections.singleton(yPrime));
        Production production = new Production(yPrime, rhs);
        return productions.add(production);
    }

    private boolean addEpsilonForm(String yPrime) {
        // Add Y' ::= ε
        String[] rhs = new String[]{EPSILON};
        Production production = new Production(yPrime, rhs);
        return productions.add(production);
    }

    private static Predicate<Production> isNonLeftRecursiveBetaForm(Enumerations enums, int j) {
        // Does the production have the form `Y ::= β` where β does not contain Y?
        return production -> {
            String expectedY = enums.get(j);
            String actualY = production.getLhs();
            boolean doesNotContainY = !production.getRhs().get(0).equals(actualY);
            return expectedY.equals(actualY) && doesNotContainY;
        };
    }

    private static Predicate<Production> isLeftRecursiveAlphaForm(Enumerations enums, int i) {
        // Does the production have the form `Y ::= Y α`?
        return production -> {
            String expectedY = enums.get(i);
            String actualY = production.getLhs();
            String firstSymbolOfRhs = production.getRhs().get(0);
            return expectedY.equals(actualY) && expectedY.equals(firstSymbolOfRhs);
        };
    }

    private void eliminateIndirectLeftRecursion(Enumerations enums, int i, int j) {
        Productions alphaForms = getAlphaForms(isAlphaForm(enums, i, j));
        Productions betaForms = getBetaForms(isBetaForm(enums, j));

        for (Production alphaForm : alphaForms) {
            // Remove Xi ::= Xj α
            productions.remove(alphaForm);

            List<String> rhs = alphaForm.getRhs();
            List<String> alpha = rhs.subList(1, rhs.size());
            String xi = alphaForm.getLhs();

            for (Production betaForm : betaForms) {
                List<String> beta = betaForm.getRhs();
                String[] newRhs = getNewRhs(beta, alpha);
                Production betaAlphaForm = new Production(xi, newRhs);

                // Add Xi ::= β α
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
        // Does the production have the form `Xj ::= β` where β can be directly left recursive?
        return production -> {
            String expectedXj = enums.get(j);
            String actualXj = production.getLhs();
            return expectedXj.equals(actualXj);
        };
    }

    private static Predicate<Production> isAlphaForm(Enumerations enums, int i, int j) {
        // Does the production have the form `Xi ::= Xj α`?
        return production -> {
            String expectedXi = enums.get(i);
            String actualXi = production.getLhs();
            String expectedXj = enums.get(j);
            String actualXj = production.getRhs().get(0);
            return expectedXi.equals(actualXi) && expectedXj.equals(actualXj);
        };
    }

    private Enumerations enumerateNonTerminals() {
        Enumerations enumerations = new Enumerations();
        ListWithUniques<String> withoutStart = new ListWithUniques<>(nonTerminals,
                String::compareTo);
        withoutStart.remove(start);

        /* If we don't begin with the start symbol, the converted grammar will
        have completely different productions. */
        enumerations.add(start);
        enumerations.addAll(withoutStart);
        return enumerations;
    }

    Grammar deepClone() {
        Symbols nonTerminals = new Symbols(this.nonTerminals);
        Symbols terminals = new Symbols(this.terminals);
        Productions productions = new Productions(this.productions);
        return new Grammar(nonTerminals, terminals, start, productions);
    }

    LR1Collection computeLR1Collection() {
        Grammar augmented = this.augment();
        FirstMap firstMap = augmented.first();
        Items s0 = augmented.getFirstState(firstMap);
        LR1Collection collection = new LR1Collection(Collections.singletonList(s0));

        boolean newStatesAreBeingAdded = true;
        LR1Collection previous;
        Collection<Items> states;

        while (newStatesAreBeingAdded) {
            previous = collection.deepClone();
            states = previous.values();

            states.forEach(collection.populate(firstMap, augmented));
            newStatesAreBeingAdded = !collection.equals(previous);
        }

        return collection;
    }

    private Items getFirstState(FirstMap firstMap) {
        Item kernel = getKernel();
        Items s0 = new Items();
        s0.add(kernel);
        return s0.closure(firstMap, productions);
    }

    @NotNull
    private Item getKernel() {
        Production startRule = productions
                .stream()
                .filter(p -> p.getLhs().equals(start))
                .toArray(Production[]::new)[0];

        String lhs = startRule.getLhs();

        List<String> rhs = new ArrayList<>();
        rhs.add(MARKER);
        rhs.addAll(startRule.getRhs());

        return new Item(TERMINATOR, lhs, rhs.toArray(new String[0]));
    }

    Grammar augment() {
        Symbols nonTerminals = new Symbols(this.nonTerminals);
        Symbols terminals = new Symbols(this.terminals);
        Productions productions = new Productions(this.productions);

        String newStart = start + "'";
        productions.add(new Production(newStart, start));
        return new Grammar(nonTerminals, terminals, newStart, productions);
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

    Symbols getNonTerminals() {
        return nonTerminals;
    }

    Symbols getTerminals() {
        return terminals;
    }

    Productions getProductions() {
        return productions;
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
    Symbols() {
        super();
    }

    Symbols(@NotNull Collection<? extends String> symbols) {
        super(symbols);
    }
}

class Productions extends ListWithUniques<Production> {
    Productions() {
        super(Production::compareTo);
    }

    Productions(@NotNull Collection<? extends Production> items) {
        super(items, Production::compareTo);
    }
}
