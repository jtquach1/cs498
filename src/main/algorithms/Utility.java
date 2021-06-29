package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

class Utility {
    static void addSymbols(FSA fsa, Character... symbols) {
        for (Character symbol : symbols) {
            fsa.addSymbol(symbol);
        }
    }

    static void addStates(FSA fsa, Integer... stateIds) {
        for (Integer id : stateIds) {
            fsa.addState(new State(id));
        }
    }

    static void addStates(Set<State> states, Integer... stateIds) {
        for (Integer id : stateIds) {
            states.add(new State(id));
        }
    }

    static void addStates(FSA fsa, State... states) {
        for (State state : states) {
            fsa.addState(state);
        }
    }

    static void addFinalStates(FSA fsa, Integer... stateIds) {
        for (Integer id : stateIds) {
            fsa.addFinalState(new State(id));
        }
    }

    static void addMoves(FSA fsa, Move... moves) {
        for (Move move : moves) {
            fsa.addMove(move);
        }
    }

    static void addMoves(Set<Move> set, Move... moves) {
        set.addAll(Arrays.asList(moves));
    }

    static Move makeMove(Integer fromId, Character consumed, Integer toId) {
        return new Move(new State(fromId), consumed, new State(toId));
    }

    static DFA makeDFA(Integer start) {
        return new DFA(new State(start), null);
    }

    static DFA makeDFA(Integer start, Integer phi) {
        return new DFA(new State(start), new State(phi));
    }

    static DFA makeDFA(State start, State phi) {
        return new DFA(start, phi);
    }

    static NFA makeNFA(Integer start) {
        return new NFA(new State(start));
    }

    static PSet makePSet(Integer... stateIds) {
        PSet set = new PSet();
        for (Integer stateId : stateIds) {
            set.add(new State(stateId));
        }
        return set;
    }

    static void addPSets(Partition partition, PSet... sets) {
        partition.addAll(Arrays.asList(sets));
    }

    static State makeState(Integer stateId, Set<State> states) {
        return new State(stateId, states);
    }

    static Set<State> makeStates(Integer... stateIds) {
        Set<State> states = new TreeSet<>();
        for (int stateId : stateIds) {
            states.add(new State(stateId));
        }
        return states;
    }

    static LL1ParseOutputEntry makeEntry(Stack<String> stack, Queue<String> input, Integer output) {
        return new LL1ParseOutputEntry(stack, input, output);
    }

    static Stack<String> makeStack(String... symbols) {
        Stack<String> stack = new Stack<>();
        stack.addAll(Arrays.asList(symbols));
        return stack;
    }

    static Queue<String> makeQueue(String... terminals) {
        Queue<String> queue = new Queue<>();
        queue.addAll(Arrays.asList(terminals));
        return queue;
    }

    static Symbols makeNonTerminals(String... symbols) {
        return new Symbols(Arrays.asList(symbols));
    }

    static Symbols makeTerminals(String... symbols) {
        return new Symbols(Arrays.asList(symbols));
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

    static Goto makeGoto(Items from, String symbol) {
        return new Goto(from, symbol);
    }
}

class Queue<T> extends ArrayList<T> {
    void queue(T item) {
        this.add(0, item);
    }

    T dequeue() {
        return this.remove(0);
    }
}

class Stack<T> extends ArrayList<T> {
    Stack() {
        super();
    }

    Stack(Collection<T> items) {
        this.addAll(items);
    }

    T pop() {
        int last = this.size() - 1;
        T item = this.remove(last);
        return item;
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
    /* Similar to a set, where only unique elements are stored but
    their order of insertion is preserved. */
    private final Comparator<T> comparator;

    ListWithUniques(final Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    ListWithUniques(@NotNull Collection<? extends T> items, Comparator<T> comparator) {
        super();
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
