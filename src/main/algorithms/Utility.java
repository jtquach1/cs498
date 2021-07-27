package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

class Utility {
    static Alphabet makeAlphabet(Character... symbols) {
        return new Alphabet(Arrays.asList(symbols));
    }

    static Moves makeMoves(Move... moves) {
        return new Moves(Arrays.asList(moves));
    }

    static Move makeMove(Integer fromId, Character consumed, Integer toId) {
        return new Move(new State(fromId), consumed, new State(toId));
    }

    static DFA makeDFA(Alphabet alphabet, States states, State start, States finalStates,
                       Moves moves) {
        return new DFA(alphabet, states, start, finalStates, moves);
    }

    static DFA makeDFA(Alphabet alphabet, States states, State start, States finalStates,
                       Moves moves, State phi) {
        return new DFA(alphabet, states, start, finalStates, moves, phi);
    }

    static NFA makeNFA(Alphabet alphabet, States states, State start,
                       States finalStates, Moves moves) {
        return new NFA(alphabet, states, start, finalStates, moves);
    }

    static PSet makePSet(Integer... stateIds) {
        return Arrays
                .stream(stateIds)
                .map(State::new)
                .collect(Collectors.toCollection(PSet::new));
    }

    static Partition makePartition(PSet... sets) {
        return new Partition(Arrays.asList(sets));
    }

    static State makeState(Integer stateId, States states) {
        return new State(stateId, states);
    }

    static States makeStates(Integer... stateIds) {
        return Arrays
                .stream(stateIds)
                .map(State::new)
                .collect(Collectors.toCollection(States::new));
    }

    static States makeStates(State... states) {
        return new States(Arrays.asList(states));
    }

    static LL1ParseOutputEntry makeLL1ParseOutputEntry(Stack<String> stack, Queue<String> input,
                                                       Integer output) {
        String cursor;
        if (input.isEmpty()) {
            cursor = null;
        } else {
            cursor = input.dequeue();
        }
        return new LL1ParseOutputEntry(stack, input, output, cursor);
    }

    static LR1ParseOutputEntry makeLR1ParseOutputEntry(Stack<Pair> stack, Queue<String> input,
                                                       Action action) {
        String cursor;
        if (input.isEmpty()) {
            cursor = null;
        } else {
            cursor = input.dequeue();
        }
        return new LR1ParseOutputEntry(stack, input, action, cursor);
    }

    static Pair makePair(String symbol, Integer stateIndex) {
        return new Pair(symbol, stateIndex);
    }

    @SafeVarargs
    static <T> Stack<T> makeStack(T... items) {
        Stack<T> stack = new Stack<>();
        stack.addAll(Arrays.asList(items));
        return stack;
    }

    static Queue<String> makeQueue(String... terminals) {
        Queue<String> queue = new Queue<>();
        queue.addAll(Arrays.asList(terminals));
        return queue;
    }

    static Symbols makeNonTerminals(String... symbols) {
        return new Symbols(symbols);
    }

    static Symbols makeTerminals(String... symbols) {
        return new Symbols(symbols);
    }

    static Productions makeProductions(String... lines) {
        return Arrays
                .stream(lines)
                .map(Utility::getProductionFromLine)
                .collect(Collectors.toCollection(Productions::new));
    }

    static Production getProductionFromLine(String line) {
        String[] sides = line.split("::=");
        String lhs = sides[0].trim();
        String[] rhs = sides[1].trim().split(" ");
        return new Production(lhs, rhs);
    }

    static Items makeItems(String... lines) {
        return Arrays
                .stream(lines)
                .map(Utility::getItemsFromLine)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(Items::new));
    }

    static Items getItemsFromLine(String line) {
        Items items = new Items();
        line = line.trim();
        if (line.startsWith("[") && line.endsWith("]")) {
            line = line.substring(1, line.length() - 1);
            line = line.trim();
        }

        String[] item = Arrays
                .stream(line.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        Production production = getProductionFromLine(item[0]);
        String[] lookaheads = item[1].split("/");
        String lhs = production.getLhs();
        String[] rhs = production.getRhs().toArray(new String[0]);

        for (String lookahead : lookaheads) {
            items.add(new Item(lookahead, lhs, rhs));
        }

        return items;
    }

    static Transition makeTransition(Items from, String symbol, Items to) {
        return new Transition(from, symbol, to);
    }

    static Action makeAction(Execution execution, Integer index) {
        return new Action(execution, index);
    }

    static Symbols makeFirst(String... symbols) {
        return new Symbols(symbols);
    }

    static Symbols makeFollow(String... symbols) {
        return new Symbols(symbols);
    }
}

class Queue<T> extends ArrayList<T> {
    Queue() {
    }

    Queue(@NotNull Collection<? extends T> c) {
        super(c);
    }

    void queue(T item) {
        this.add(0, item);
    }

    T dequeue() {
        return this.remove(0);
    }
}

class Stack<T> extends ArrayList<T> {
    Stack() {
    }

    Stack(Collection<T> items) {
        this.addAll(items);
    }

    T pop() {
        int last = this.size() - 1;
        return this.remove(last);
    }

    void push(T item) {
        this.add(item);
    }

    T peek() {
        if (this.isEmpty()) {
            return null;
        }
        int last = this.size() - 1;
        return this.get(last);
    }
}

class ListWithUniques<T> extends ArrayList<T> {
    /* Similar to a set, where only unique elements are stored.
    Elements are indexed, but their indices may change due to sorting. */
    private final Comparator<T> comparator;

    ListWithUniques(final Comparator<T> comparator) {
        this.comparator = comparator;
    }

    ListWithUniques(@NotNull Collection<? extends T> items, Comparator<T> comparator) {
        this.comparator = comparator;
        this.addAll(items);
    }

    @Override
    public void add(int index, T item) {
        add(item);
    }

    @Override
    public boolean add(T item) {
        boolean isAdded = false;
        if (!contains(item)) {
            isAdded = super.add(item);
            sort(comparator);
        }
        return isAdded;
    }

    @Override
    public boolean addAll(Collection<? extends T> items) {
        boolean hasChanged = false;
        for (T item : items) {
            hasChanged |= add(item);
        }
        return hasChanged;
    }
}

class Table<K1, K2, V> extends TreeMap<K1, TreeMap<K2, List<V>>> {
    void set(K1 key1, K2 key2, V value) {
        TreeMap<K2, List<V>> entry = this.computeIfAbsent(key1, k -> new TreeMap<>());

        List<V> values = entry.get(key2);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);

        entry.put(key2, values);
    }

    V get(K1 key1, K2 key2) {
        TreeMap<K2, List<V>> entry = this.get(key1);
        if (entry == null) {
            return null;
        }

        List<V> values = entry.get(key2);
        if (key2 == null || values == null) {
            return null;
        }

        // In case of conflicts, we just want to return one value.
        return values.get(0);
    }

    @Override
    public String toString() {
        String rows = this
                .keySet()
                .stream()
                .map(key1 -> {
                    TreeMap<K2, List<V>> entry = this.get(key1);
                    String entries = entry
                            .keySet()
                            .stream()
                            .map(key2 -> "{\"" + key2 + "\":" + entry.get(key2) + "}")
                            .collect(Collectors.joining(","));

                    return "\"" + key1 + "\":" + "[" + entries + "]";
                })
                .collect(Collectors.joining(","));

        return "{" + rows + "}";
    }
}

class OutputEntry<E1, E2, E3> {
    Stack<E1> stack;
    Queue<E2> input;
    E3 output;

    OutputEntry(Stack<E1> stack, Queue<E2> input, E3 output, E2 cursor) {
        /* We need to deep clone the parameters because their references are
        constantly during parsing. */
        this.stack = new Stack<>(stack);

        // Lecture slides show the cursor as part of the input.
        this.input = new Queue<>(input);

        if (cursor != null) {
            this.input.queue(cursor);
        }

        this.output = output;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack, input, output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputEntry<?, ?, ?> other = (OutputEntry<?, ?, ?>) o;
        return Objects.equals(stack, other.stack) &&
                Objects.equals(input, other.input) &&
                Objects.equals(output, other.output);
    }

    @Override
    public String toString() {
        return "{\"stack\":\"" + stack + "\"," +
                "\"input\":\"" + input + "\"," +
                "\"output\":\"" + output + "\"}\n";
    }
}
