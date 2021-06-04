package algorithms;

import java.util.Set;
import java.util.TreeSet;

class NFA extends FSA {
    NFA() {
        super();
    }

    NFA(
            Alphabet alphabet,
            Set<State> states,
            State start,
            Set<State> finalStates,
            Set<Move> moves
    ) {
        super(alphabet, states, start, finalStates, moves);
    }

    public static void main(String[] args) {
        String infix;
        try {
            if (args.length == 0) {
                infix = "";
            } else {
                infix = args[0];
            }
            NFA nfa = NFA.regexToNFA(infix);
            String dot = nfa.toString();
            System.out.println(dot);
        } catch (Exception e) {
            System.out.println("Invalid regular expression");
        }
    }

    static NFA regexToNFA(String infix) {
        char[] postfix = Regex.infixToPostfix(infix).toCharArray();
        Stack<NFA> nfaStack = new Stack<>();
        NFA result;

        if (postfix.length == 0) {
            result = makeSingle(EPSILON);
        } else {
            for (char c : postfix) {
                if (c == '.') {
                    NFA second = nfaStack.pop();
                    NFA first = nfaStack.pop();
                    nfaStack.push(concatenate(first, second));
                } else if (c == '|') {
                    NFA second = nfaStack.pop();
                    NFA first = nfaStack.pop();
                    nfaStack.push(alternate(first, second));
                } else if (c == '*') {
                    NFA first = nfaStack.pop();
                    nfaStack.push(kleeneStar(first));
                } else {
                    nfaStack.push(makeSingle(c));
                }
            }
            result = nfaStack.pop();
        }

        result.removeEpsilonFromAlphabet();
        return result;
    }

    static NFA makeSingle(Character consumed) {
        NFA nfa = new NFA();
        State finalState = new State();
        nfa.addState(finalState);
        nfa.addFinalState(finalState);
        nfa.addMove(nfa.getStart(), consumed, finalState);
        nfa.addSymbol(consumed);
        return nfa;
    }

    static NFA concatenate(NFA first, NFA second) {
        NFA result = first.deepClone();
        result.connectOriginalFinalStatesToOtherStart(second);
        result.removeOriginalFinalStates();
        result.copyStates(second);
        result.copyAlphabet(second);
        result.copyMoves(second);
        result.copyFinalStates(second);
        return result;
    }

    private NFA deepClone() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new TreeSet<>();
        State start = this.getStart();
        Set<State> finalStates = new TreeSet<>();
        Set<Move> moves = new TreeSet<>();

        alphabet.addAll(this.getAlphabet());
        states.addAll(this.getStates());
        finalStates.addAll(this.getFinalStates());
        moves.addAll(this.getMoves());

        return new NFA(alphabet, states, start, finalStates, moves);
    }

    private void connectOriginalFinalStatesToOtherStart(NFA other) {
        Set<State> finalStates = this.getFinalStates();
        State otherStart = other.getStart();
        for (State finalState : finalStates) {
            this.addMove(finalState, EPSILON, otherStart);
        }
    }

    private void removeOriginalFinalStates() {
        this.removeFinalStates();
    }

    private void copyStates(NFA other) {
        for (State state : other.getStates()) {
            this.addState(state);
        }
    }

    private void copyAlphabet(NFA other) {
        for (Character consumed : other.getAlphabet()) {
            this.addSymbol(consumed);
        }
    }

    private void copyMoves(NFA other) {
        Set<Move> otherMoves = other.getMoves();
        for (Move move : otherMoves) {
            this.addMove(move);
        }
    }

    private void copyFinalStates(NFA other) {
        for (State state : other.getFinalStates()) {
            this.addFinalState(state);
        }
    }

    static NFA alternate(NFA first, NFA second) {
        NFA result = first.deepClone();
        result.addNewStartForAlternation(second);
        result.copyAlphabet(second);
        result.copyStates(second);
        result.copyMoves(second);
        result.copyFinalStates(second);
        result.addNewFinal();
        return result;
    }

    private void addNewStartForAlternation(NFA other) {
        State newStart = new State();
        State firstStart = this.getStart();
        State secondStart = other.getStart();

        addState(newStart);
        setStart(newStart);
        addMove(newStart, EPSILON, firstStart);
        addMove(newStart, EPSILON, secondStart);
    }

    private void addNewFinal() {
        Set<State> finalStates = getFinalStates();
        State newFinal = new State();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, newFinal);
        }
        removeFinalStates();
        addFinalState(newFinal);
        addState(newFinal);
    }

    static NFA kleeneStar(NFA first) {
        NFA result = first.deepClone();
        result.connectOriginalFinalStatesToOriginalStart();
        result.addNewStartForKleeneStar();
        result.addNewFinal();
        result.connectNewStartToNewFinal();
        return result;
    }

    private void connectOriginalFinalStatesToOriginalStart() {
        State oldStart = getStart();
        Set<State> finalStates = getFinalStates();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, oldStart);
        }
    }

    private void addNewStartForKleeneStar() {
        State oldStart = getStart();
        State newStart = new State();
        addState(newStart);
        addMove(newStart, EPSILON, oldStart);
        setStart(newStart);
    }

    private void connectNewStartToNewFinal() {
        // There is only one new final state, but this is more convenient to write.
        for (State newFinalState : getFinalStates()) {
            addMove(getStart(), EPSILON, newFinalState);
        }
    }

    private void removeEpsilonFromAlphabet() {
        this.removeSymbol(EPSILON);
    }
}
