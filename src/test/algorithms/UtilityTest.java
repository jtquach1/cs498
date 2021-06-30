package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @Test
    void makeAlphabet() {
        Alphabet expected = new Alphabet();
        expected.add('a');
        expected.add('b');
        Alphabet actual = Utility.makeAlphabet('a', 'b');
        assertEquals(expected, actual);
    }

    @Test
    void makeMoves() {
        Set<Move> expected = new TreeSet<>();
        expected.add(new Move(new State(0), 'a', new State(0)));
        expected.add(new Move(new State(0), 'b', new State(0)));
        Set<Move> actual = Utility.makeMoves(
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
    void makeDFAWithoutPhi() {
        DFA expected = new DFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>()
        );
        DFA actual = Utility.makeDFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>()
        );
        assertEquals(expected, actual);
    }

    @Test
    void makeDFAWithPhi() {
        DFA expected = new DFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>(),
                new State(1)
        );
        DFA actual = Utility.makeDFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>(),
                new State(1)
        );
        assertEquals(expected, actual);
    }


    @Test
    void makeNFA() {
        NFA expected = new NFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>()
        );
        NFA actual = Utility.makeNFA(
                new Alphabet(),
                new TreeSet<>(Collections.singletonList(new State(0))),
                new State(0),
                new TreeSet<>(),
                new TreeSet<>()
        );
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
    void makePartition() {
        Partition expected = new Partition();
        PSet pset = new PSet();
        pset.add(new State(0));
        pset.add(new State(1));
        expected.add(pset);
        Partition actual = Utility.makePartition(pset);
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
    void makeStatesWithoutIds() {
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        Set<State> actual = Utility.makeStates(0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void makeStatesWithIds() {
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        Set<State> actual = Utility.makeStates(new State(0), new State(1));
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

    @Test
    void makeGoto() {
        Items s0 = Utility.makeItems(
                "[E' ::= E " + MARKER + ", " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, " + TERMINATOR + "/+]"
        );
        Goto expected = new Goto(s0, "E");
        Goto actual = Utility.makeGoto(s0, "E");

        assertEquals(expected, actual);
    }
}