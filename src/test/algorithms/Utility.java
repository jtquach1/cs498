package algorithms;

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

    static void addStates(Set states, Integer... stateIds) {
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

    static void addMoves(Set set, Move... moves) {
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
}
