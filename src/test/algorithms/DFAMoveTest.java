package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DFAMoveTest {

    @Test
    void convertToMoves() {
        Set<Move> expected = new TreeSet<>(Collections.singletonList(
                new Move(
                        new State(0, new TreeSet<>()),
                        'a',
                        new State(1, new TreeSet<>())
                )
        ));

        Set<DFAMove> dfaMoves = new TreeSet<>(Collections.singletonList(
                new DFAMove(
                        new DFAState(0, new TreeSet<>()),
                        'a',
                        new DFAState(1, new TreeSet<>())
                )
        ));
        Set<Move> actual = DFAMove.convertToMoves(dfaMoves);
        assertEquals(expected, actual);
    }

    @Test
    void convertToMove() {
        Move expected = new Move(
                new State(0, new TreeSet<>()),
                'a',
                new State(1, new TreeSet<>())
        );

        DFAMove dfaMove = new DFAMove(
                new DFAState(0, new TreeSet<>()),
                'a',
                new DFAState(1, new TreeSet<>())
        );
        Move actual = dfaMove.convertToMove();
        assertEquals(expected, actual);
    }
}