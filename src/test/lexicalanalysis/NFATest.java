import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        NFA first = new NFA();
        State s0 = new State(0);
        State s1 = new State(1);
        first.addSymbol('a');
        first.addState(s0);
        first.addState(s1);
        first.setStart(s0);
        first.addFinalState(s1);
        first.addMove(s0, 'a', s1);

        NFA second = new NFA();
        State s2 = new State(2);
        State s3 = new State(3);
        second.addSymbol('b');
        second.addState(s2);
        second.addState(s3);
        second.setStart(s2);
        second.addFinalState(s3);
        second.addMove(s2, 'b', s3);

        NFA result = first.concatenate(second);
        assertEquals("[a, b]", result.getAlphabet().toString());
        assertEquals("State{id=0}", result.getStart().toString());
        assertEquals("[State{id=3}]", result.getFinalStates().toString());
        assertEquals("[State{id=1}, State{id=2}, State{id=3}, State{id=0}]", result.getStates().toString());
        assertEquals("{State{id=1}={ɛ=[State{id=2}]}, State{id=2}={b=[State{id=3}]}, State{id=0}={a=[State{id=1}]}}", result.getMoves().toString());
    }

}