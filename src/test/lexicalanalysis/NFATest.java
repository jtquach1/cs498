import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class NFATest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testConcatenate() {
        NFA first;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('a');
            HashSet<State> states = new HashSet<>();
            State start = new State();
            HashSet<State> finalStates = new HashSet<>();
            State last = new State();
            HashSet<Move> moves = new HashSet<>();
            Move move = new Move(start, 'a', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            first = new NFA(alphabet, states, start, finalStates, moves);
        }

        NFA second;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('b');
            HashSet<State> states = new HashSet<>();
            State start = new State();
            HashSet<State> finalStates = new HashSet<>();
            State last = new State();
            HashSet<Move> moves = new HashSet<>();
            Move move = new Move(start, 'b', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            second = new NFA(alphabet, states, start, finalStates, moves);
        }
        NFA result = first.concatenate(second);

        assertEquals("[a, b]", result.getAlphabet().toString());
        assertEquals("0", result.getStart().toString());
        assertEquals("[3]", result.getFinalStates().toString());
        assertEquals("[1, 2, 3, 0]", result.getStates().toString());
        assertEquals("{State{id=1}={ɛ=[State{id=2}]}, State{id=2}={b=[State{id=3}]}, State{id=0}={a=[State{id=1}]}}", result.getMoves().toString());
        assertEquals("{\n" +
                "    \"Alphabet\": [a, b],\n" +
                "    \"States\":\n" +
                "    {\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"1\"\n" +
                "        },\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"2\"\n" +
                "        },\n" +
                "        \"State\":\n" +
                "        {\n" +
                "            \"id\": \"3\"\n" +
                "        },\n" +
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
                "            \"id\": \"3\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"Moves\":\n" +
                "    {\n" +
                "        \"Move\":\n" +
                "        {\n" +
                "            \"From\": \"2\",\n" +
                "            \"Consumed\": \"b\",\n" +
                "            \"To\": \"3\"\n" +
                "        },\n" +
                "        \"Move\":\n" +
                "        {\n" +
                "            \"From\": \"1\",\n" +
                "            \"Consumed\": \"ɛ\",\n" +
                "            \"To\": \"2\"\n" +
                "        },\n" +
                "        \"Move\":\n" +
                "        {\n" +
                "            \"From\": \"0\",\n" +
                "            \"Consumed\": \"a\",\n" +
                "            \"To\": \"1\"\n" +
                "        }\n" +
                "    }\n" +
                "}", result.toString());
    }

}