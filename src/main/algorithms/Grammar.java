package algorithms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static algorithms.Grammar.EPSILON;
import static algorithms.Item.MARKER;
import static algorithms.Pair.noSuchSymbol;

class Grammar implements DOT {
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
            Sequence rhs = production.getRhs();
            int n = rhs.size();

            for (int i = 0; i < n; i++) {
                Symbols followOfSymbol = getFollowOfSymbol(followMap, rhs, i);

                // We do not calculate the follow sets for terminals.
                if (followOfSymbol == null) continue;

                Sequence subsequence = rhs.subList(i + 1, n);
                Symbols firstOfSubsequence = firstMap.first(subsequence);
                followOfSymbol.addAll(firstOfSubsequence);
                followOfSymbol.remove(EPSILON);

                if (i == n - 1 || firstOfSubsequence.contains(EPSILON)) {
                    followOfSymbol.addAll(followMap.get(lhs));
                }
            }
        };
    }

    @Nullable
    private Symbols getFollowOfSymbol(FollowMap followMap, Sequence rhs, int i) {
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

            for (Production production : subset) {
                int productionIndex = productions.indexOf(production);
                Symbols firstOfRhs = firstMap.first(production.getRhs());

                for (String firstTerminal : firstOfRhs) {
                    if (!firstTerminal.equals(EPSILON)) {
                        table.set(nonTerminal, firstTerminal, productionIndex);
                    }
                    if (firstOfRhs.contains(EPSILON)) {
                        Symbols followOfNonTerminal = followMap.get(nonTerminal);

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
        Stack<String> stack = initializeStackForLL1Parsing();
        String symbol = sentence.dequeue();
        LL1ParseOutput parseOutput = initializeLL1ParseOutput(sentence, stack, symbol);

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
            parseOutput.add(new LL1ParseOutputEntry(stack, sentence, index, symbol));
        }
        return parseOutput;
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
    private Integer replaceTopWithRule(LL1ParseTable table, Stack<String> stack, String symbol,
                                       String top) throws Exception {
        Integer index = table.get(top, symbol);
        if (index != null) {
            Production rule = productions.get(index);
            Sequence rhs = new Sequence(rule.getRhs());
            Collections.reverse(rhs);
            stack.addAll(rhs);
        } else {
            throw new Exception("No rule to follow");
        }
        return index;
    }

    @NotNull
    private static LL1ParseOutput initializeLL1ParseOutput(Queue<String> sentence,
                                                           Stack<String> stack, String symbol) {
        LL1ParseOutput output = new LL1ParseOutput();
        output.add(new LL1ParseOutputEntry(stack, sentence, null, symbol));
        return output;
    }

    private boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    @NotNull
    private static Queue<String> initializeSentence(String delimitedBySpaces) {
        /* If the sentence doesn't end with the terminator, we will getIndex an
        ArrayIndexOutOfBounds exception otherwise during LL1 or LR1 parsing. */
        if (!delimitedBySpaces.endsWith(TERMINATOR)) {
            delimitedBySpaces += " " + TERMINATOR;
        }

        List<String> sentence = Arrays.asList(delimitedBySpaces.strip().split(" "));
        return new Queue<>(sentence);
    }

    @NotNull
    private Stack<String> initializeStackForLL1Parsing() {
        Stack<String> stack = new Stack<>();
        stack.push(TERMINATOR);
        stack.push(start);
        return stack;
    }

    boolean isLL1(LL1ParseTable table) {
        // A grammar is LL1 only if the table has at most 1 index per entry.
        return nonTerminals
                .stream()
                .noneMatch(nonTerminal -> table
                        .get(nonTerminal)
                        .values()
                        .stream()
                        .anyMatch(indices -> indices.size() > 1));
    }

    Grammar removeLeftRecursion() {
        Grammar grammar = this.deepClone();
        Sequence enumerations = enumerateNonTerminals();
        int n = enumerations.size();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                grammar.eliminateIndirectLeftRecursion(enumerations, i, j);
            }
            grammar.eliminateDirectLeftRecursion(enumerations, i);
        }

        return grammar;
    }

    private void eliminateDirectLeftRecursion(Sequence enumerations, int i) {
        Productions alphaForms = getAlphaForms(isLeftRecursiveAlphaForm(enumerations, i));
        Productions betaForms = getBetaForms(isNonLeftRecursiveBetaForm(enumerations, i));

        for (Production alphaForm : alphaForms) {
            // Remove Y ::= Y α
            productions.remove(alphaForm);

            Sequence rhs = alphaForm.getRhs();
            Sequence alpha = rhs.subList(1, rhs.size());
            String y = alphaForm.getLhs();
            String yPrime = y + "'";

            while (isNonTerminal(yPrime)) {
                yPrime += "'";
            }

            for (Production betaForm : betaForms) {
                Sequence beta = betaForm.getRhs();

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

    private boolean addBetaForm(String y, String yPrime, Sequence beta) {
        // Add Y ::= β Y'
        String[] newRhs = getNewRhs(beta, Collections.singleton(yPrime));
        Production newProduction = new Production(y, newRhs);
        return productions.add(newProduction);
    }

    private boolean addAlphaForm(Sequence alpha, String yPrime) {
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

    private static Predicate<Production> isNonLeftRecursiveBetaForm(Sequence enumerations, int j) {
        // Does the production have the form `Y ::= β` where β does not contain Y?
        return production -> {
            String expectedY = enumerations.get(j);
            String actualY = production.getLhs();
            boolean doesNotContainY = !production.getRhs().get(0).equals(actualY);
            return expectedY.equals(actualY) && doesNotContainY;
        };
    }

    private static Predicate<Production> isLeftRecursiveAlphaForm(Sequence enumerations, int i) {
        // Does the production have the form `Y ::= Y α`?
        return production -> {
            String expectedY = enumerations.get(i);
            String actualY = production.getLhs();
            String firstSymbolOfRhs = production.getRhs().get(0);
            return expectedY.equals(actualY) && expectedY.equals(firstSymbolOfRhs);
        };
    }

    private void eliminateIndirectLeftRecursion(Sequence enumerations, int i, int j) {
        Productions alphaForms = getAlphaForms(isAlphaForm(enumerations, i, j));
        Productions betaForms = getBetaForms(isBetaForm(enumerations, j));

        for (Production alphaForm : alphaForms) {
            // Remove Xi ::= Xj α
            productions.remove(alphaForm);

            Sequence rhs = alphaForm.getRhs();
            Sequence alpha = rhs.subList(1, rhs.size());
            String xi = alphaForm.getLhs();

            for (Production betaForm : betaForms) {
                Sequence beta = betaForm.getRhs();
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
        Sequence newRhs = new Sequence();
        newRhs.addAll(first);
        newRhs.addAll(second);
        return newRhs.toArray(new String[0]);
    }

    private static Predicate<Production> isBetaForm(Sequence enumerations, int j) {
        // Does the production have the form `Xj ::= β` where β can be directly left recursive?
        return production -> {
            String expectedXj = enumerations.get(j);
            String actualXj = production.getLhs();
            return expectedXj.equals(actualXj);
        };
    }

    private static Predicate<Production> isAlphaForm(Sequence enumerations, int i, int j) {
        // Does the production have the form `Xi ::= Xj α`?
        return production -> {
            String expectedXi = enumerations.get(i);
            String actualXi = production.getLhs();
            String expectedXj = enumerations.get(j);
            String actualXj = production.getRhs().get(0);
            return expectedXi.equals(actualXi) && expectedXj.equals(actualXj);
        };
    }

    private Sequence enumerateNonTerminals() {
        Sequence enumerations = new Sequence();
        ListWithUniques<String> withoutStart = new ListWithUniques<>(nonTerminals,
                String::compareTo);
        withoutStart.remove(start);

        /* If we don't begin with the start symbol, the converted grammar will
        have completely different productions. */
        enumerations.add(start);
        enumerations.addAll(withoutStart);
        return enumerations;
    }

    private Grammar deepClone() {
        Symbols nonTerminals = new Symbols(this.nonTerminals);
        Symbols terminals = new Symbols(this.terminals);
        Productions productions = new Productions(this.productions);
        return new Grammar(nonTerminals, terminals, start, productions);
    }

    LR1Collection computeLR1Collection() {
        Grammar augmented = this.augment();
        FirstMap firstMap = augmented.first();
        Items startState = augmented.getStartState(firstMap);
        LR1Collection collection = new LR1Collection(startState, new Transitions());

        boolean newStatesAreBeingAdded = true;
        LR1Collection previous;

        while (newStatesAreBeingAdded) {
            previous = collection.deepClone();
            previous.forEach(augmented.populate(collection, firstMap));
            newStatesAreBeingAdded = !collection.equals(previous);
        }

        return collection;
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

    @NotNull
    private Consumer<Items> populate(LR1Collection collection, FirstMap firstMap) {
        Symbols symbols = new Symbols(terminals);
        symbols.addAll(nonTerminals);

        return from -> symbols.forEach(
                symbol -> {
                    Items to = from.computeGoto(symbol, firstMap, productions);
                    Transition transition = new Transition(from, symbol, to);
                    if (!to.isEmpty() && !collection.contains(transition)) {
                        collection.add(transition);
                    }
                }
        );
    }

    private Items getStartState(FirstMap firstMap) {
        Item kernel = getKernel();
        Items startState = new Items();
        startState.add(kernel);
        return startState.closure(firstMap, productions);
    }

    @NotNull
    private Item getKernel() {
        Production startRule = productions
                .stream()
                .filter(p -> p.getLhs().equals(start))
                .toArray(Production[]::new)[0];

        String lhs = startRule.getLhs();

        Sequence rhs = new Sequence();
        rhs.add(MARKER);
        rhs.addAll(startRule.getRhs());

        return new Item(TERMINATOR, lhs, rhs.toArray(new String[0]));
    }

    Grammar augment() {
        Symbols nonTerminals = new Symbols(this.nonTerminals);
        Symbols terminals = new Symbols(this.terminals);
        Productions productions = new Productions(this.productions);

        String newStart = getNewStart();
        productions.add(new Production(newStart, start));
        return new Grammar(nonTerminals, terminals, newStart, productions);
    }

    @NotNull
    private String getNewStart() {
        String newStart = start + "'";

        // Might be possible that newStart already exists
        while (isNonTerminal(newStart)) {
            newStart += "'";
        }
        return newStart;
    }

    LR1ParseTable generateLR1ParseTable(LR1Collection collection) {
        ActionTable actionTable = constructActionTable(collection);
        GotoTable gotoTable = constructGotoTable(collection);

        /* During LR1 collection creation, any arbitrary state would have its
        index shifted due to automatic sorting, hence why we look it up now. */
        Integer startIndex = collection.indexOf(collection.getStart());
        return new LR1ParseTable(actionTable, gotoTable, startIndex);
    }

    private ActionTable constructActionTable(LR1Collection collection) {
        ActionTable table = new ActionTable();
        Transitions transitions = getTransitionsOnlyWithTerminals(collection);

        for (Transition transition : transitions) {
            Items from = transition.getFrom();
            Items to = transition.getTo();

            Integer fromIndex = collection.indexOf(from);
            String terminal = transition.getSymbol();
            Integer toIndex = collection.indexOf(to);

            table.populateWithShift(fromIndex, terminal, toIndex);
        }

        for (Items from : collection) {
            Integer fromIndex = collection.indexOf(from);

            // An accept state doesn't have a production to reduce to.
            if (isAcceptState(from)) {
                table.populateWithAccept(fromIndex);
            } else {
                table.populateWithReduce(productions, from, fromIndex);
            }
        }

        return table;
    }

    private Transitions getTransitionsOnlyWithTerminals(LR1Collection collection) {
        return collection
                .getTransitions()
                .stream()
                .filter(transition -> isTerminal(transition.getSymbol()))
                .collect(Collectors.toCollection(Transitions::new));
    }

    private boolean isAcceptState(Items from) {
        return from.contains(new Item(TERMINATOR, getNewStart(), start, MARKER));
    }

    private GotoTable constructGotoTable(LR1Collection collection) {
        GotoTable table = new GotoTable();
        Transitions transitions = getTransitionsOnlyWithNonTerminals(collection);

        for (Transition transition : transitions) {
            Items from = transition.getFrom();
            String symbol = transition.getSymbol();
            Items to = transition.getTo();

            Integer fromId = collection.indexOf(from);
            Integer toId = collection.indexOf(to);

            table.set(fromId, symbol, toId);
        }

        return table;
    }

    private Transitions getTransitionsOnlyWithNonTerminals(LR1Collection collection) {
        return collection
                .getTransitions()
                .stream()
                .filter(transition -> isNonTerminal(transition.getSymbol()))
                .collect(Collectors.toCollection(Transitions::new));
    }

    LR1ParseOutput parseSentence(LR1ParseTable table, String delimitedBySpaces) throws Exception {
        Queue<String> sentence = initializeSentence(delimitedBySpaces);
        Stack<Pair> stack = initializeStackForLR1Parsing(table);
        String terminal = sentence.dequeue();
        LR1ParseOutput parseOutput = new LR1ParseOutput();

        while (true) {
            Pair pair = stack.peek();
            Integer topState = pair.getStateIndex();
            Action action = table.getActionTable().get(topState, terminal);
            parseOutput.add(new LR1ParseOutputEntry(stack, sentence, action, terminal));

            if (action == null) {
                throw new Exception("No such Action at state " + topState + " and symbol " + terminal);
            } else if (action.executesShift()) {
                Integer state = action.getIndex();
                stack.push(new Pair(terminal, state));
                terminal = sentence.dequeue();
            } else if (action.executesReduce()) {
                Integer rule = action.getIndex();
                Production production = productions.get(rule);
                removeRhsOfProductionFromStack(stack, production);
                pushLhsAndGotoEntryOntoStack(table, stack, production);
            } else if (action.executesAccept()) {
                break;
            }
        }

        return parseOutput;
    }

    private static void removeRhsOfProductionFromStack(Stack<Pair> stack, Production rule) {
        Stack<String> rhs = new Stack<>(rule.getRhs());

        while (!rhs.isEmpty()) {
            String fromRhs = rhs.pop();
            Pair currentPair = stack.pop();
            String fromStack = currentPair.getSymbol();

            // Let's check that we're reducing to the correct production.
            assert fromRhs.equals(fromStack);
        }
    }

    private static void pushLhsAndGotoEntryOntoStack(LR1ParseTable table, Stack<Pair> stack,
                                                     Production rule) {
        Pair pair = stack.peek();
        Integer topState = pair.getStateIndex();

        String lhs = rule.getLhs();
        Integer state = table.getGotoTable().get(topState, lhs);

        stack.push(new Pair(lhs, state));
    }

    @NotNull
    private Stack<Pair> initializeStackForLR1Parsing(LR1ParseTable table) {
        Stack<Pair> stack = new Stack<>();
        Integer index = table.getStartIndex();
        stack.push(new Pair(noSuchSymbol, index));
        return stack;
    }

    boolean isLR1(LR1ParseTable table) {
        ActionTable actionTable = table.getActionTable();
        Collection<TreeMap<String, List<Action>>> entries = actionTable.values();

        // A grammar is LR1 only if the Action table has at most 1 action per entry.
        return entries
                .stream()
                .noneMatch(entry -> entry
                        .values()
                        .stream()
                        .anyMatch(actions -> actions.size() > 1));
    }

    boolean containsEpsilonProductions() {
        return productions
                .stream()
                .anyMatch(production -> production.getRhs().contains(EPSILON));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grammar other = (Grammar) o;
        return Objects.equals(nonTerminals, other.nonTerminals) &&
                Objects.equals(terminals, other.terminals) &&
                Objects.equals(start, other.start) &&
                Objects.equals(productions, other.productions);
    }

    @Override
    public String toDOT() {
        StringBuilder list = new StringBuilder();

        int i = 1;
        for (Production production : productions) {
            list.append("<tr><td align=\"left\">" + i + ". " + production.toDOT() + "</tr>");
            i++;
        }

        return "<tr><td>Grammar, start: " + start + "</td></tr>" + list;
    }

    public String augmentedGrammarToDOT() {
        StringBuilder list = new StringBuilder();
        List<Production> productions = new ArrayList<>(this.productions);

        Optional<Production> startProduction = productions
                .stream()
                .filter(production -> production.getLhs().equals(this.start))
                .findFirst();

        if (startProduction.isPresent()) {
            productions.remove(startProduction.get());
            productions.add(0, startProduction.get());
        }

        for (int i = 0; i < productions.size(); i++) {
            Production production = productions.get(i);
            list.append("<tr><td align=\"left\">" + i + ". " + production.toDOT() + "</tr>");
        }

        return "<tr><td>Grammar, start: " + start + "</td></tr>" + list;
    }
}

class Sequence extends ArrayList<String> implements Comparable<Sequence> {
    Sequence() {
    }

    Sequence(@NotNull Collection<? extends String> c) {
        super(c);
    }

    public Sequence subList(int fromIndex, int toIndex) {
        return new Sequence(super.subList(fromIndex, toIndex));
    }

    @Override
    public int compareTo(@NotNull Sequence other) {
        return Comparator
                .comparing(Sequence::toString)
                .compare(this, other);
    }
}

class Production implements Comparable<Production>, DOT {
    protected final String lhs;
    protected final Sequence rhs;

    Production(String lhs, String... rhs) {
        this.lhs = lhs;
        this.rhs = new Sequence(Arrays.asList(rhs));
    }

    @Override
    public int compareTo(@NotNull Production other) {
        return Comparator
                .comparing(Production::getLhs)
                .thenComparing(Production::getRhs)
                .compare(this, other);
    }

    String getLhs() {
        return lhs;
    }

    Sequence getRhs() {
        return rhs;
    }

    boolean beginsWithEpsilon() {
        String firstSymbol = rhs.get(0);
        return firstSymbol.equals(EPSILON);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production other = (Production) o;
        return lhs.equals(other.lhs) && rhs.equals(other.rhs);
    }

    @Override
    public String toDOT() {
        return lhs + " ::= " + String.join(" ", rhs) + "</td>";
    }
}

class Symbols extends TreeSet<String> implements DOT {
    Symbols() {
    }

    Symbols(@NotNull Collection<? extends String> symbols) {
        super(symbols);
    }

    Symbols(String... symbols) {
        super(Arrays.asList(symbols));
    }

    @Override
    public String toDOT() {
        return String.join(", ", this);
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
