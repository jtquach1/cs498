package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static algorithms.Execution.SHIFT;
import static algorithms.Grammar.TERMINATOR;
import static algorithms.Item.MARKER;
import static algorithms.Pair.noSuchSymbol;
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
        Moves expected = new Moves();
        expected.add(new Move(new State(0), 'a', new State(0)));
        expected.add(new Move(new State(0), 'b', new State(0)));
        Moves actual = Utility.makeMoves(
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
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves()
        );
        DFA actual = Utility.makeDFA(
                new Alphabet(),
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves()
        );
        assertEquals(expected, actual);
    }

    @Test
    void makeDFAWithPhi() {
        DFA expected = new DFA(
                new Alphabet(),
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves(),
                new State(1)
        );
        DFA actual = Utility.makeDFA(
                new Alphabet(),
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves(),
                new State(1)
        );
        assertEquals(expected, actual);
    }


    @Test
    void makeNFA() {
        NFA expected = new NFA(
                new Alphabet(),
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves()
        );
        NFA actual = Utility.makeNFA(
                new Alphabet(),
                new States(Collections.singletonList(new State(0))),
                new State(0),
                new States(),
                new Moves()
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
        States states = new States();
        states.add(new State(1));
        states.add(new State(2));
        State expected = new State(0, states);
        State actual = Utility.makeState(0, states);
        assertEquals(expected, actual);
    }

    @Test
    void makeStatesWithoutIds() {
        States expected = new States();
        expected.add(new State(0));
        expected.add(new State(1));
        States actual = Utility.makeStates(0, 1);
        assertEquals(expected, actual);
    }

    @Test
    void makeStatesWithIds() {
        States expected = new States();
        expected.add(new State(0));
        expected.add(new State(1));
        States actual = Utility.makeStates(new State(0), new State(1));
        assertEquals(expected, actual);
    }

    @Test
    void makeEntry() {
        LL1ParseOutputEntry expected = new LL1ParseOutputEntry(new Stack<>(), new Queue<>(),
                null, null);
        LL1ParseOutputEntry actual = Utility.makeLL1ParseOutputEntry(new Stack<>(), new Queue<>(),
                null);
        assertEquals(expected, actual);

        expected = new LL1ParseOutputEntry(new Stack<>(), new Queue<>(), null, "id");
        actual = Utility.makeLL1ParseOutputEntry(new Stack<>(),
                new Queue<>(Collections.singleton("id")), null);
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
    void getItemsFromLine() {
        String line = "[E' ::= " + MARKER + " E, " + TERMINATOR + "/+]";
        Items actual = Utility.getItemsFromLine(line);
        Items expected = new Items();
        expected.add(new Item(TERMINATOR, "E'", MARKER, "E"));
        expected.add(new Item("+", "E'", MARKER, "E"));
        assertEquals(expected, actual);
    }

    @Test
    void makeTransition() {
        Items s0 = Utility.makeItems(
                "[E' ::= " + MARKER + " E, " + TERMINATOR + "]",
                "[E ::= " + MARKER + " E + T, " + TERMINATOR + "/+]",
                "[E ::= " + MARKER + " T, " + TERMINATOR + "/+]",
                "[T ::= " + MARKER + " T * F, " + TERMINATOR + "/+/*]",
                "[T ::= " + MARKER + " F, " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " ( E ), " + TERMINATOR + "/+/*]",
                "[F ::= " + MARKER + " id, " + TERMINATOR + "/+/*]"
        );

        Items s1 = Utility.makeItems(
                "[E' ::= E " + MARKER + ", " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, " + TERMINATOR + "]",
                "[E ::= E " + MARKER + " + T, +]"
        );

        Transition expected = new Transition(s0, "E", s1);
        Transition actual = Utility.makeTransition(s0, "E", s1);

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
    void makeLL1ParseOutputEntry() {
        LL1ParseOutputEntry actual = Utility.makeLL1ParseOutputEntry(
                new Stack<>(Arrays.asList(TERMINATOR, "E")),
                new Queue<>(Arrays.asList("id", "+", "id", "*", "id", TERMINATOR)),
                null
        );

        LL1ParseOutputEntry expected = new LL1ParseOutputEntry(
                new Stack<>(Arrays.asList(TERMINATOR, "E")),
                new Queue<>(Arrays.asList("+", "id", "*", "id", TERMINATOR)),
                null,
                "id"
        );
        assertEquals(expected, actual);
    }

    @Test
    void makeLR1ParseOutputEntry() {
        LR1ParseOutputEntry actual = Utility.makeLR1ParseOutputEntry(
                new Stack<>(Collections.singleton(new Pair(noSuchSymbol, 9))),
                new Queue<>(Arrays.asList("id", "+", "id", "*", "id", TERMINATOR)),
                new Action(SHIFT, 14)
        );

        LR1ParseOutputEntry expected = new LR1ParseOutputEntry(
                new Stack<>(Collections.singleton(new Pair(noSuchSymbol, 9))),
                new Queue<>(Arrays.asList("+", "id", "*", "id", TERMINATOR)),
                new Action(SHIFT, 14),
                "id"
        );
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
    void makePair() {
        assertEquals(
                new Pair(noSuchSymbol, 9),
                Utility.makePair(noSuchSymbol, 9)
        );
    }

    @Test
    void makeAction() {
        assertEquals(
                new Action(SHIFT, 14),
                Utility.makeAction(SHIFT, 14)
        );
    }

    @Test
    void makeFirst() {
        Symbols expected = new Symbols();
        expected.add("id");
        expected.add("+");
        Symbols actual = Utility.makeFirst("id", "+");
        assertEquals(expected, actual);
    }

    @Test
    void makeFollow() {
        Symbols expected = new Symbols();
        expected.add("id");
        expected.add("+");
        Symbols actual = Utility.makeFollow("id", "+");
        assertEquals(expected, actual);
    }
}