package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @Test
    void addSymbols() {
        Alphabet alphabet = new Alphabet();
        alphabet.add('a');
        alphabet.add('b');
        FSA expected = new FSA(alphabet, null, null, null, null);
        FSA actual = new FSA(new Alphabet(), null, null, null, null);
        Utility.addSymbols(actual, 'a', 'b');
        assertEquals(expected, actual);
    }

    @Test
    void addStatesToFSAWithIds() {
        Set<State> states = new TreeSet<>();
        states.add(new State(0));
        states.add(new State(1));
        FSA expected = new FSA(null, states, null, null, null);
        FSA actual = new FSA(null, new TreeSet<>(), null, null, null);
        Utility.addStates(actual, 0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void addStatesToSetWithIds() {
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        Set<State> actual = new TreeSet<>();
        Utility.addStates(actual, 0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void addStatesToFSAWithStates() {
        Set<State> states = new TreeSet<>();
        states.add(new State(0));
        states.add(new State(1));
        FSA expected = new FSA(null, states, null, null, null);
        FSA actual = new FSA(null, new TreeSet<>(), null, null, null);
        Utility.addStates(actual, new State(0), new State(1));
        assertEquals(expected, actual);
    }

    @Test
    void addFinalStates() {
        Set<State> finalStates = new TreeSet<>();
        finalStates.add(new State(0));
        finalStates.add(new State(1));
        FSA expected = new FSA(null, null, null, finalStates, null);
        FSA actual = new FSA(null, null, null, new TreeSet<>(), null);
        Utility.addFinalStates(actual, 0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void addMovesToFSA() {
        Set<Move> moves = new TreeSet<>();
        moves.add(new Move(new State(0), 'a', new State(0)));
        moves.add(new Move(new State(0), 'b', new State(0)));
        FSA expected = new FSA(null, null, null, null, moves);
        FSA actual = new FSA(null, null, null, null, new TreeSet<>());
        Utility.addMoves(actual,
                new Move(new State(0), 'a', new State(0)),
                new Move(new State(0), 'b', new State(0))
        );
        assertEquals(expected, actual);
    }

    @Test
    void addMovesToSet() {
        Set<Move> expected = new TreeSet<>();
        expected.add(new Move(new State(0), 'a', new State(0)));
        expected.add(new Move(new State(0), 'b', new State(0)));
        Set<Move> actual = new TreeSet<>();
        Utility.addMoves(actual,
                new Move(new State(0), 'a', new State(0)),
                new Move(new State(0), 'b', new State(0))
        );
        assertEquals(expected, actual);
    }

    @Test
    void makeMove() {
        Move expected = new Move(new State(0), 'a', new State(0));
        Move actual = Utility.makeMove(0, 'a', 0);
        assertEquals(expected, actual);
    }

    @Test
    void makeDFAWithoutPhiAndWithIds() {
        DFA expected = new DFA(new State(0), null);
        DFA actual = Utility.makeDFA(0);
        assertEquals(expected, actual);
    }

    @Test
    void makeDFAWithPhiAndWithIds() {
        DFA expected = new DFA(new State(0), new State(1));
        DFA actual = Utility.makeDFA(0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void makeDFAWithPhiAndWithStates() {
        DFA expected = new DFA(new State(0), new State(1));
        DFA actual = Utility.makeDFA(new State(0), new State(1));
        assertEquals(expected, actual);
    }

    @Test
    void makeNFA() {
        NFA expected = new NFA(new State(0));
        NFA actual = Utility.makeNFA(0);
        assertEquals(expected, actual);
    }

    @Test
    void makePSet() {
        PSet expected = new PSet();
        expected.add(new State(0));
        expected.add(new State(1));
        PSet actual = Utility.makePSet(0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void addPSets() {
        Partition expected = new Partition();
        PSet pset = new PSet();
        pset.add(new State(0));
        pset.add(new State(1));
        expected.add(pset);
        Partition actual = new Partition();
        Utility.addPSets(actual, pset);
        assertEquals(expected, actual);
    }

    @Test
    void makeState() {
        Set<State> states = new TreeSet<>();
        states.add(new State(1));
        states.add(new State(2));
        State expected = new State(0, states);
        State actual = Utility.makeState(0, states);
        assertEquals(expected, actual);
    }

    @Test
    void makeStates() {
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        Set<State> actual = Utility.makeStates(0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void makeEntry() {
        LL1ParseOutputEntry expected = new LL1ParseOutputEntry(new Stack<>(), new Queue<>(), null);
        LL1ParseOutputEntry actual = Utility.makeEntry(new Stack<>(), new Queue<>(), null);
        assertEquals(expected, actual);
    }

    @Test
    void makeStack() {
        Stack<String> expected = new Stack<>();
        expected.add("E");
        expected.add("F");
        Stack<String> actual = Utility.makeStack("E", "F");
        assertEquals(expected, actual);
    }

    @Test
    void makeQueue() {
        Queue<String> expected = new Queue<>();
        expected.add("E");
        expected.add("F");
        Queue<String> actual = Utility.makeQueue("E", "F");
        assertEquals(expected, actual);
    }

    @Test
    void makeNonTerminals() {
        Symbols expected = new Symbols();
        expected.add("E");
        expected.add("F");
        Symbols actual = Utility.makeNonTerminals("E", "F");
        assertEquals(expected, actual);
    }

    @Test
    void makeTerminals() {
        Symbols expected = new Symbols();
        expected.add("id");
        expected.add("+");
        Symbols actual = Utility.makeNonTerminals("id", "+");
        assertEquals(expected, actual);
    }

    @Test
    void makeProductions() {
        Productions expected = new Productions();
        expected.add(new Production("E", "E", "+", "T"));
        expected.add(new Production("E", "T"));
        Productions actual = Utility.makeProductions("E ::= E + T", "E ::= T");
        assertEquals(expected, actual);
    }

    @Test
    void getProductionFromLine() {
        String line = "    E ::= E + T   ";
        Production actual = Utility.getProductionFromLine(line);
        Production expected = new Production("E", "E", "+", "T");
        assertEquals(expected, actual);

        line = "E ::= T\n";
        actual = Utility.getProductionFromLine(line);
        expected = new Production("E", "T");
        assertEquals(expected, actual);
    }

    @Test
    void makeItems() {
        Items expected = new Items();
        expected.add(new Item(TERMINATOR, "E'", MARKER, "E"));
        expected.add(new Item(TERMINATOR, "E", MARKER, "E", "+", "T"));
        Items actual = Utility.makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "]"
        );
        assertEquals(expected, actual);
    }

    @Test
    void getItemsFromLine() {
        String line = "[E' ::= " + MARKER + " E, " + TERMINATOR + "/+]";
        Items actual = Utility.getItemsFromLine(line);
        Items expected = new Items();
        expected.add(new Item(TERMINATOR, "E'", MARKER, "E"));
        expected.add(new Item("+", "E'", MARKER, "E"));
        assertEquals(expected, actual);
    }
}