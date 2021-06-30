package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static algorithms.Utility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PSetTest {

    @Test
    void getIncludedStates() {
        PSet expected = makePSet(0, 1, 2, 3, 5);

        PSet targetSet = makePSet(0, 1, 2, 3, 5);
        Set<Move> moves = makeMoves(
                makeMove(0, 'a', 1),
                makeMove(0, 'b', 2),
                makeMove(1, 'a', 3),
                makeMove(1, 'b', 4),
                makeMove(2, 'a', 3),
                makeMove(2, 'b', 4),
                makeMove(3, 'a', 3),
                makeMove(4, 'a', 5),
                makeMove(4, 'b', 5),
                makeMove(5, 'a', 5),
                makeMove(5, 'b', 5),
                makeMove(5, 'b', 5)
        );
        PSet set = makePSet(0, 1, 2, 3, 5);
        Character consumed = 'a';
        PSet actual = targetSet.getIncludedStates(moves, set, consumed);

        assertEquals(expected, actual);
    }

    @Test
    void getExcludedStates() {
        PSet expected = makePSet();

        PSet targetSet = makePSet(0, 1, 2, 3, 5);
        Set<Move> moves = makeMoves(
                makeMove(0, 'a', 1),
                makeMove(0, 'b', 2),
                makeMove(1, 'a', 3),
                makeMove(1, 'b', 4),
                makeMove(2, 'a', 3),
                makeMove(2, 'b', 4),
                makeMove(3, 'a', 3),
                makeMove(4, 'a', 5),
                makeMove(4, 'b', 5),
                makeMove(5, 'a', 5),
                makeMove(5, 'b', 5),
                makeMove(5, 'b', 5)
        );
        PSet set = makePSet(0, 1, 2, 3, 5);
        Character consumed = 'a';
        PSet actual = targetSet.getExcludedStates(moves, set, consumed);

        assertEquals(expected, actual);
    }
}