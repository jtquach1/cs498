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
                        new State(0, new States()),
                        'a',
                        new State(1, new States())
                )
        ));

        Set<DFAMove> dfaMoves = new TreeSet<>(Collections.singletonList(
                new DFAMove(
                        new DFAState(0, new States()),
                        'a',
                        new DFAState(1, new States())
                )
        ));
        Set<Move> actual = DFAMove.convertToMoves(dfaMoves);
        assertEquals(expected, actual);
    }

    @Test
    void convertToMove() {
        Move expected = new Move(
                new State(0, new States()),
                'a',
                new State(1, new States())
        );

        DFAMove dfaMove = new DFAMove(
                new DFAState(0, new States()),
                'a',
                new DFAState(1, new States())
        );
        Move actual = dfaMove.convertToMove();
        assertEquals(expected, actual);
    }
}