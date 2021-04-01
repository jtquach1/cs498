import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class NFA extends FSA {
    public static final char EPSILON = '\u025B';
    public static final Character[] reservedCharacters = new Character[]{'|', '(', ')', '*', EPSILON};

    public NFA() {
        super();
    }

    public NFA(Alphabet alphabet, Set<State> states, State start,
               Set<State> finalStates, Set<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
    }

    public static NFA regexToNFA(String infix) {
        char[] postfix = Regex.infixToPostfix(infix).toCharArray();
        Stack<NFA> nfas = new Stack<>();
        for (char c : postfix) {
            if (c == '.') {
                NFA n2 = nfas.pop();
                NFA n1 = nfas.pop();
                nfas.push(n1.concatenate(n2));
            } else if (c == '|') {
                NFA n2 = nfas.pop();
                NFA n1 = nfas.pop();
                nfas.push(n1.alternate(n2));
            } else if (c == '*') {
                NFA n = nfas.pop();
                nfas.push(n.kleeneStar());
            } else {
                nfas.push(makeSingle(c));
            }
        }
        return nfas.pop();
    }

    public static NFA convert(String json) {
        FSA fsa = FSA.convert(json);
        return new NFA(
                fsa.getAlphabet(),
                fsa.getStates(),
                fsa.getStart(),
                fsa.getFinalStates(),
                fsa.getMoves()
        );
    }

    public static void main(String[] args) {
//        NFA first = getFirstNfa();
//        NFA second = getSecondNfa();
//        NFA concatTest = first.concatenate(second);
//
//        String firstJson = first.toString();
//        String secondJson = second.toString();
//        String concatJson = concatTest.toString();
//
//        NFA converted = NFA.convert(firstJson);

        ////
        String originalInfix = "(cd*)";
//        System.out.println("infix: " + Regex.markWithConcatenation(originalInfix));
//        System.out.println("postfix: " + Regex.infixToPostfix(originalInfix));
        NFA regex2nfa = regexToNFA(originalInfix);
    }

    @NotNull
    private static NFA getSecondNfa() {
        NFA second;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('b');
            Set<State> states = new HashSet<>();
            State start = new State();
            Set<State> finalStates = new HashSet<>();
            State last = new State();
            Set<Move> moves = new HashSet<>();
            Move move = new Move(start, 'b', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            second = new NFA(alphabet, states, start, finalStates, moves);
        }
        return second;
    }

    @NotNull
    private static NFA getFirstNfa() {
        NFA first;
        {
            Alphabet alphabet = new Alphabet();
            alphabet.addSymbol('a');
            Set<State> states = new HashSet<>();
            State start = new State();
            Set<State> finalStates = new HashSet<>();
            State last = new State();
            Set<Move> moves = new HashSet<>();
            Move move = new Move(start, 'a', last);
            moves.add(move);
            states.add(start);
            states.add(last);
            finalStates.add(last);

            first = new NFA(alphabet, states, start, finalStates, moves);
        }
        return first;
    }

    public static NFA makeSingle(Character c) {
        NFA nfa = new NFA();
        State finalState = new State();
        nfa.addState(finalState);
        nfa.addFinalState(finalState);
        nfa.addMove(nfa.getStart(), c, finalState);
        nfa.addSymbol(c);
        return nfa;
    }

    public NFA clone() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new HashSet<>();
        State start = this.getStart();
        Set<State> finalStates = new HashSet<>();
        Set<Move> moves = new HashSet<>();

        alphabet.addAll(this.getAlphabet());
        states.addAll(this.getStates());
        finalStates.addAll(this.getFinalStates());
        moves.addAll(this.getMoves());

        return new NFA(alphabet, states, start, finalStates, moves);
    }

    public NFA concatenate(NFA other) {
        NFA result = this.clone();
        result.connectOriginalFinalStatesToOtherStart(other);
        result.removeOriginalFinalStates();
        result.copyStates(other);
        result.copyAlphabet(other);
        result.copyMoves(other);
        result.copyFinalStates(other);
        return result;
    }

    public NFA kleeneStar() {
        NFA result = this;
        result.connectOriginalFinalStatesToOriginalStart();
        result.addNewFinal();
        result.addNewStart();
        result.connectNewStartToNewFinal();
        return result;
    }

    public NFA alternate(NFA other) {
        NFA result = this;
        result.addNewStart();
        result.addNewFinal();
        NFA helperFinal = new NFA();
        return result;
    }

    private void copyFinalStates(NFA other) {
        for (State state : other.getFinalStates()) {
            this.addFinalState(state);
        }
    }

    private void removeOriginalFinalStates() {
        this.removeFinalStates();
    }

    private void copyAlphabet(NFA other) {
        for (Character consumed : other.getAlphabet()) {
            this.addSymbol(consumed);
        }
    }

    private void copyStates(NFA other) {
        for (State state : other.getStates()) {
            this.addState(state);
        }
    }

    private void connectOriginalFinalStatesToOtherStart(NFA other) {
        Set<State> finalStates = this.getFinalStates();
        State otherStart = other.getStart();
        for (State finalState : finalStates) {
            this.addMove(finalState, EPSILON, otherStart);
        }
    }

    private void copyMoves(NFA other) {
        Set<Move> otherMoves = other.getMoves();
        for (Move move : otherMoves) {
            this.addMove(move);
        }
    }

    private void connectNewStartToNewFinal() {
        // There is only one new final state, but this is more convenient to write.
        for (State newFinalState : getFinalStates()) {
            addMove(getStart(), EPSILON, newFinalState);
        }
    }

    private void addNewFinal() {
        Set<State> finalStates = getFinalStates();
        State newFinal = new State();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, newFinal);
        }
        removeFinalStates();
        addFinalState(newFinal);
    }

    private void connectOriginalFinalStatesToOriginalStart() {
        State oldStart = getStart();
        Set<State> finalStates = getFinalStates();
        for (State finalState : finalStates) {
            addMove(finalState, EPSILON, oldStart);
        }
    }

    private void addNewStart() {
        State oldStart = getStart();
        State newStart = new State();
        addNewStart(oldStart, newStart);
    }

    private void addNewStart(State oldStart, State newStart) {
        addState(newStart);
        addMove(newStart, EPSILON, oldStart);
        setStart(newStart);

    }

}
