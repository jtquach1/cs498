package algorithms;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

class Utility {
    static void addSymbols(FSA fsa, Character... symbols) {
        for (Character symbol : symbols) {
            fsa.addSymbol(symbol);
        }
    }

    static void addStates(FSA fsa, Integer... stateIds) {
        for (Integer id : stateIds) {
            fsa.addState(new State(id));
        }
    }

    static void addStates(Set<State> states, Integer... stateIds) {
        for (Integer id : stateIds) {
            states.add(new State(id));
        }
    }

    static void addFinalStates(FSA fsa, Integer... stateIds) {
        for (Integer id : stateIds) {
            fsa.addFinalState(new State(id));
        }
    }

    static void addMoves(FSA fsa, Move... moves) {
        for (Move move : moves) {
            fsa.addMove(move);
        }
    }

    static void addMoves(Set<Move> set, Move... moves) {
        for (Move move : moves) {
            set.add(move);
        }
    }

    static Move makeMove(Integer fromId, Character consumed, Integer toId) {
        return new Move(new State(fromId), consumed, new State(toId));
    }

    static FSA makeFSA(Integer stateId) {
        return new FSA(
                new Alphabet(),
                new TreeSet<>(),
                new State(stateId),
                new TreeSet<>(), new TreeSet<>()
        );
    }


    static DFA makeDFA(Integer start) {
        return new DFA(
                new Alphabet(),
                new TreeSet<>(),
                new State(start),
                new TreeSet<>(),
                new TreeSet<>(),
                null
        );
    }

    static DFA makeDFA(Integer start, Integer phi) {
        return new DFA(
                new Alphabet(),
                new TreeSet<>(),
                new State(start),
                new TreeSet<>(),
                new TreeSet<>(),
                new State(phi)
        );
    }

    static NFA makeNFA(Integer stateId) {
        return new NFA(
                new Alphabet(),
                new TreeSet<>(),
                new State(stateId),
                new TreeSet<>(),
                new TreeSet<>()
        );
    }

    static PSet makePSet(Integer... stateIds) {
        PSet set = new PSet();
        for (Integer stateId : stateIds) {
            set.add(new State(stateId));
        }
        return set;
    }

    static void addPSets(Partition partition, PSet... sets) {
        for (PSet set : sets) {
            partition.add(set);
        }
    }
}
