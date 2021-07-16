package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FSATest {
    FSA fsa;

    @BeforeEach
    void setUp() {
        Alphabet alphabet = new Alphabet(Collections.singleton('a'));
        State start = new State(0);
        State finalState = new State(1);
        States states = new States(Arrays.asList(start, finalState));
        States finalStates = new States(Collections.singleton(finalState));
        Moves moves = new Moves(Collections.singleton(new Move(start, 'a', finalState)));

        fsa = new FSA(alphabet, states, start, finalStates, moves);
    }

    @Test
    void addState() {
        fsa.addState(new State(2));
        States actual = fsa.getStates();
        States expected = new States(
                Arrays.asList(
                        new State(0),
                        new State(1),
                        new State(2)
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    void addFinalState() {
        fsa.addFinalState(new State(2));
        States actual = fsa.getFinalStates();
        States expected = new States(
                Arrays.asList(
                        new State(1),
                        new State(2)
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    void removeFinalStates() {
        fsa.removeFinalStates();
        States actual = fsa.getFinalStates();
        States expected = new States();
        assertEquals(expected, actual);
    }

    @Test
    void addMove() {
        State from = new State(2);
        Character consumed = 'b';
        State to = new State(3);
        fsa.addMove(from, consumed, to);

        Moves actual = fsa.getMoves();
        Moves expected = new Moves(
                Arrays.asList(
                        new Move(new State(0), 'a', new State(1)),
                        new Move(new State(2), 'b', new State(3))
                )
        );
        assertEquals(expected, actual);
    }
}