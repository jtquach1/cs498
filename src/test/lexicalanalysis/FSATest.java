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
    }

    @Test
    public void testState() {
        State start = new State();
        State other = new State();
        assertEquals(0, start.getId());
        assertEquals(1, other.getId());
    }

    @Test
    void testFSA() {
        FSA fsa = new FSA();
        State start = fsa.getStart();
        fsa.addSymbol('a');
        fsa.addMove(start, 'a', start);
        fsa.addFinalState(start);
        String expected = "{\n" +
                "    \"Alphabet\": [a],\n" +
                "    \"States\":\n" +
                "    {\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"0\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"Start\":\n" +
                "    {\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"0\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"FinalStates\":\n" +
                "    {\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"0\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"Moves\":\n" +
                "    {\n" +
                "        \"Move\":\n" +
                "        {\n" +
                "            \"From\": \"0\",\n" +
                "            \"Consumed\": \"a\",\n" +
                "            \"To\": \"0\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        assertEquals(expected, fsa.toString());
    }

}