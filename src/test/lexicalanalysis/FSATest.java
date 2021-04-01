import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FSATest {
    FSA fsa;

    @BeforeEach
    void setUp() {
        Alphabet alphabet = new Alphabet();
        alphabet.addSymbol('a');
        Set<State> states = new TreeSet<>();
        State start = new State();
        Set<State> finalStates = new TreeSet<>();
        State last = new State();
        Set<Move> moves = new TreeSet<>();
        Move move = new Move(start, 'a', last);
        moves.add(move);
        states.add(start);
        states.add(last);
        finalStates.add(last);
        fsa = new FSA(alphabet, states, start, finalStates, moves);
    }

    @AfterEach
    void tearDown() {
        fsa = null;
    }

    @Test
    void convert() {

    }

    @Test
    void testClone() {
    }

    @Test
    void addSymbol() {
    }

    @Test
    void addState() {
    }

    @Test
    void addFinalState() {
    }

    @Test
    void removeFinalStates() {
    }

    @Test
    void addMove() {
    }

    @Test
    void testAddMove() {
    }

    @Test
    void getAlphabet() {
        assertEquals("[a]", fsa.getAlphabet().toString());
    }

    @Test
    void getStates() {
    }

    @Test
    void getStart() {
    }

    @Test
    void setStart() {
    }

    @Test
    void getFinalStates() {
    }

    @Test
    void getMoves() {
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
}