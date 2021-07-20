package algorithms;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static algorithms.Utility.makePSet;
import static algorithms.Utility.makePartition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PartitionTest {

    @Test
    void getExistingSetContainingState() {
        PSet set1 = makePSet(3);
        PSet set2 = makePSet(1);
        PSet set3 = makePSet(0, 2);
        Partition partition = makePartition(set1, set2, set3);

        PSet expected = makePSet(0, 2);
        PSet actual = partition.getExistingSetContainingState(new State(0));
        assertEquals(expected, actual);

        actual = partition.getExistingSetContainingState(new State(4));
        assertNull(actual);
    }

    @Test
    void convertToDfaStates() {
        DFAStates expected = new DFAStates(
                Arrays.asList(
                        new DFAState(0, new States(makePSet(0, 2))),
                        new DFAState(1, new States(makePSet(1))),
                        new DFAState(2, new States(makePSet(3)))
                )
        );

        PSet set1 = makePSet(3);
        PSet set2 = makePSet(1);
        PSet set3 = makePSet(0, 2);
        Partition partition = makePartition(set1, set2, set3);
        DFAStates actual = partition.convertToDFAStates();
        assertEquals(expected, actual);
    }

    @Test
    void replaceSet() {
        Partition expected = makePartition(
                makePSet(4),
                makePSet(5, 6),
                makePSet(3),
                makePSet(1)
        );

        PSet set1 = makePSet(3);
        PSet set2 = makePSet(1);
        PSet set3 = makePSet(0, 2);
        Partition actual = makePartition(set1, set2, set3);
        actual.replaceSet(
                makePSet(0, 2),
                makePSet(4),
                makePSet(5, 6)
        );

        assertEquals(expected, actual);
    }
}