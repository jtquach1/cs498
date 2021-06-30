package algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FSATest {
    FSA fsa;

    @BeforeEach
    void setUp() {
        Alphabet alphabet = new Alphabet(Collections.singleton('a'));
        State start = new State(0);
        State finalState = new State(1);
        Set<State> states = new TreeSet<>(Arrays.asList(start, finalState));
        Set<State> finalStates = new TreeSet<>(Collections.singleton(finalState));
        Set<Move> moves = new TreeSet<>(Collections.singleton(new Move(start, 'a', finalState)));

        fsa = new FSA(alphabet, states, start, finalStates, moves);
    }

    @Test
    void addState() {
        fsa.addState(new State(2));
        Set<State> actual = fsa.getStates();
        Set<State> expected = new TreeSet<>(
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
        Set<State> actual = fsa.getFinalStates();
        Set<State> expected = new TreeSet<>(
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
        Set<State> actual = fsa.getFinalStates();
        Set<State> expected = new TreeSet<>();
        assertEquals(expected, actual);
    }

    @Test
    void addMove() {
        State from = new State(2);
        Character consumed = 'b';
        State to = new State(3);
        fsa.addMove(from, consumed, to);

        Set<Move> actual = fsa.getMoves();
        Set<Move> expected = new TreeSet<>(
                Arrays.asList(
                        new Move(new State(0), 'a', new State(1)),
                        new Move(new State(2), 'b', new State(3))
                )
        );
        assertEquals(expected, actual);
    }
}