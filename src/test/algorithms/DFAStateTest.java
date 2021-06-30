package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static algorithms.Utility.makeStates;
import static org.junit.jupiter.api.Assertions.*;

class DFAStateTest {

    @Test
    void convertToStates() {
        Set<State> expected = new TreeSet<>(
                Arrays.asList(
                        new State(0, new TreeSet<>()),
                        new State(1, makeStates(2, 3))
                )
        );

        Set<DFAState> dfaStates = new TreeSet<>(
                Arrays.asList(
                        new DFAState(0, new TreeSet<>()),
                        new DFAState(1, makeStates(2, 3))
                )
        );
        Set<State> actual = DFAState.convertToStates(dfaStates);
        assertEquals(expected, actual);
    }

    @Test
    void convertToState() {
        State expected = new State(0, new TreeSet<>());
        DFAState dfaState = new DFAState(0, new TreeSet<>());
        State actual = dfaState.convertToState();
        assertEquals(expected, actual);

        expected = new State(1, makeStates(2, 3));
        dfaState = new DFAState(1, makeStates(2, 3));
        actual = dfaState.convertToState();
        assertEquals(expected, actual);
    }

    @Test
    void isEmpty() {
        DFAState dfaState = new DFAState(0, new TreeSet<>());
        assertTrue(dfaState.isEmpty());

        dfaState = new DFAState(0, makeStates(0));
        assertFalse(dfaState.isEmpty());
    }

    @Test
    void updateWithExistingId() {
        DFAState expected = new DFAState(1, makeStates(0, 1, 2));

        Set<DFAState> dfaStates = new TreeSet<>(
                Arrays.asList(
                        new DFAState(1, makeStates(0, 1, 2)),
                        new DFAState(2, makeStates(3))
                )
        );
        DFAState actual = new DFAState(0, makeStates(0, 1, 2));
        actual.updateWithExistingId(dfaStates);

        assertEquals(expected, actual);
    }

    @Test
    void isNewState() {
        Set<DFAState> dfaStates = new TreeSet<>(
                Arrays.asList(
                        new DFAState(0, new TreeSet<>()),
                        new DFAState(1, makeStates(2, 3))
                )
        );

        DFAState dfaState = new DFAState(2, makeStates(4));
        assertTrue(dfaState.isNewState(dfaStates));

        dfaState = new DFAState(1, makeStates(2, 3));
        assertFalse(dfaState.isNewState(dfaStates));
    }
}