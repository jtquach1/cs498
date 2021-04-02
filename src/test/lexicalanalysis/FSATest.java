import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FSATest {
    FSA fsa;

    @BeforeEach
    void setUp() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new TreeSet<>();
        State start = new State(0);
        Set<State> finalStates = new TreeSet<>();
        State finalState = new State(1);
        Set<Move> moves = new TreeSet<>();
        Move move = new Move(start, 'a', finalState);

        alphabet.addSymbol('a');
        moves.add(move);
        states.add(start);
        states.add(finalState);
        finalStates.add(finalState);

        fsa = new FSA(alphabet, states, start, finalStates, moves);
    }

    @AfterEach
    void tearDown() {
        fsa = null;
    }

    @Test
    void deepClone() {
        FSA expected = fsa;
        FSA actual = fsa.deepClone();
        assertEquals(expected, actual);

        FSA unexpected = fsa;
        unexpected.addSymbol('b');
        unexpected.addFinalState(new State(2));
        unexpected.addState(new State(2));
        unexpected.addMove(new State(0), 'b', new State(2));
        assertNotEquals(unexpected, actual);
    }

    @Test
    void addSymbol() {
        fsa.addSymbol('b');
        Alphabet actual = fsa.getAlphabet();
        Alphabet expected = new Alphabet();
        expected.addSymbol('a');
        expected.addSymbol('b');
        assertEquals(expected, actual);
    }

    @Test
    void addState() {
        fsa.addState(new State(2));
        Set<State> actual = fsa.getStates();
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        expected.add(new State(2));
        assertEquals(expected, actual);
    }

    @Test
    void addFinalState() {
        fsa.addFinalState(new State(2));
        Set<State> actual = fsa.getFinalStates();
        Set<State> expected = new TreeSet<>();
        expected.add(new State(1));
        expected.add(new State(2));
        assertEquals(expected, actual);
    }

    @Test
    void removeFinalStates() {
        fsa.removeFinalStates();
        Set<State> actual = fsa.getFinalStates();
        Set<State> expected = new TreeSet<>();
        assertEquals(expected, actual);
    }

    @Test
    void addMoveSingleArgument() {
        State from = new State(2);
        Character consumed = 'b';
        State to = new State(3);
        Move move = new Move(from, consumed, to);
        fsa.addMove(move);

        Set<Move> actual = fsa.getMoves();
        Set<Move> expected = new TreeSet<>();
        expected.add(new Move(new State(0), 'a', new State(1)));
        expected.add(new Move(new State(2), 'b', new State(3)));
        assertEquals(expected, actual);
    }

    @Test
    void addMoveThreeArgs() {
        State from = new State(2);
        Character consumed = 'b';
        State to = new State(3);
        fsa.addMove(from, consumed, to);

        Set<Move> actual = fsa.getMoves();
        Set<Move> expected = new TreeSet<>();
        expected.add(new Move(new State(0), 'a', new State(1)));
        expected.add(new Move(new State(2), 'b', new State(3)));
        assertEquals(expected, actual);
    }

    @Test
    void getAlphabet() {
        Alphabet expected = (new Alphabet());
        expected.addSymbol('a');
        Alphabet actual = fsa.getAlphabet();
        assertEquals(expected, actual);
    }

    @Test
    void getStates() {
        Set<State> expected = new TreeSet<>();
        expected.add(new State(0));
        expected.add(new State(1));
        Set<State> actual = fsa.getStates();
        assertEquals(expected, actual);
    }

    @Test
    void getStart() {
        State expected = new State(0);
        State actual = fsa.getStart();
        assertEquals(expected, actual);
    }

    @Test
    void setStart() {
        State expected = new State(1);
        fsa.setStart(new State(1));
        State actual = fsa.getStart();
        assertEquals(expected, actual);
    }

    @Test
    void getFinalStates() {
        Set<State> actual = fsa.getFinalStates();
        Set<State> expected = new TreeSet<>();
        expected.add(new State(1));
        assertEquals(expected, actual);
    }

    @Test
    void getMoves() {
        Set<Move> actual = fsa.getMoves();
        Set<Move> expected = new TreeSet<>();
        expected.add(new Move(new State(0), 'a', new State(1)));
        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
        String expected = "{" +
                "\"alphabet\":[\"a\"]," +
                "\"states\":[0,1]," +
                "\"start\":0," +
                "\"finalStates\":[1]," +
                "\"moves\":[{\"from\":0,\"consumed\":\"a\",\"to\":1}]" +
                "}";
        String actual = fsa.toString();
        assertEquals(expected, actual);
    }

    @Test
    void convertJsonToGraphviz() {
        String json = "{\n" +
                "  \"alphabet\": [\"a\", \"b\"],\n" +
                "  \"states\": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],\n" +
                "  \"start\": 4,\n" +
                "  \"finalStates\": [11],\n" +
                "  \"moves\": [\n" +
                "    { \"from\": 0, \"consumed\": \"a\", \"to\": 1 },\n" +
                "    { \"from\": 1, \"consumed\": \"ɛ\", \"to\": 5 },\n" +
                "    { \"from\": 2, \"consumed\": \"b\", \"to\": 3 },\n" +
                "    { \"from\": 3, \"consumed\": \"ɛ\", \"to\": 5 },\n" +
                "    { \"from\": 4, \"consumed\": \"ɛ\", \"to\": 0 },\n" +
                "    { \"from\": 4, \"consumed\": \"ɛ\", \"to\": 2 },\n" +
                "    { \"from\": 5, \"consumed\": \"ɛ\", \"to\": 8 },\n" +
                "    { \"from\": 6, \"consumed\": \"a\", \"to\": 7 },\n" +
                "    { \"from\": 7, \"consumed\": \"ɛ\", \"to\": 6 },\n" +
                "    { \"from\": 7, \"consumed\": \"ɛ\", \"to\": 9 },\n" +
                "    { \"from\": 8, \"consumed\": \"ɛ\", \"to\": 6 },\n" +
                "    { \"from\": 8, \"consumed\": \"ɛ\", \"to\": 9 },\n" +
                "    { \"from\": 9, \"consumed\": \"ɛ\", \"to\": 10 },\n" +
                "    { \"from\": 10, \"consumed\": \"b\", \"to\": 11 }\n" +
                "  ]\n" +
                "}\n";

        String expected = "digraph finite_state_machine {\n" +
                "\trankdir=LR;\n" +
                "\tsize=\"8,5\";\n" +
                "\n" +
                "\tnode [shape = doublecircle];\n" +
                "\t11 ;\n" +
                "\n" +
                "\tnode [shape = circle];\n" +
                "\t0 -> 1 [label = \"a\"];\n" +
                "\t1 -> 5 [label = \"ɛ\"];\n" +
                "\t2 -> 3 [label = \"b\"];\n" +
                "\t3 -> 5 [label = \"ɛ\"];\n" +
                "\t4 -> 0 [label = \"ɛ\"];\n" +
                "\t4 -> 2 [label = \"ɛ\"];\n" +
                "\t5 -> 8 [label = \"ɛ\"];\n" +
                "\t6 -> 7 [label = \"a\"];\n" +
                "\t7 -> 6 [label = \"ɛ\"];\n" +
                "\t7 -> 9 [label = \"ɛ\"];\n" +
                "\t8 -> 6 [label = \"ɛ\"];\n" +
                "\t8 -> 9 [label = \"ɛ\"];\n" +
                "\t9 -> 10 [label = \"ɛ\"];\n" +
                "\t10 -> 11 [label = \"b\"];\n" +
                "\n" +
                "\tnode [shape = none, label =\"\"];\n" +
                "\tENTRY -> 4;\n" +
                "}\n";

        String actual = FSA.convertJsonToGraphviz(json);
        assertEquals(expected, actual);
    }
}