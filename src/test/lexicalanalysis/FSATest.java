import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FSATest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testAlphabet() {
        Alphabet alphabet = new Alphabet();
        alphabet.addSymbol('a');
        alphabet.addSymbol('b');
        assertEquals("[a, b]", alphabet.toString());
        assertEquals("[a, b]", alphabet.getSymbols().toString());
    }

    @Test
    public void testState() {
        State start = new State();
        State other = new State();
        assertEquals(0, start.getId());
        assertEquals(1, other.getId());
    }

    @Test void testFSA() {
        State start = new State();
        FSA fsa = new FSA();
        fsa.addState(start);
        fsa.addSymbol('a');
        fsa.addMove(start, 'a', start);
        fsa.addFinalState(start);
        assertEquals("", fsa);
    }

}