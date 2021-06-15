package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

import static algorithms.Grammar.EPSILON;
import static algorithms.Grammar.GREEK_EPSILON;

class NFA extends FSA {
    NFA(Alphabet alphabet, Set<State> states, State start, Set<State> finalStates,
        Set<Move> moves) {
        super(alphabet, states, start, finalStates, moves);
    }

    NFA(State start) {
        super(start);
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
        State start = new State();
        State finalState = new State();

        NFA nfa = new NFA(start);
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
        State start = this.getStart();
        Alphabet alphabet = new Alphabet(this.getAlphabet());
        Set<State> states = new TreeSet<>(this.getStates());
        Set<State> finalStates = new TreeSet<>(this.getFinalStates());
        Set<Move> moves = new TreeSet<>(this.getMoves());

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

class Regex {
    static String infixToPostfix(String infix) {
        infix = infix.replaceAll(GREEK_EPSILON, EPSILON);
        infix = Regex.markWithConcatenation(infix);
        return infixToPostfix(infix.toCharArray());
    }

    static String markWithConcatenation(String originalInfix) {
        char[] infix = originalInfix.toCharArray();
        Queue<Character> temp = new Queue<>();
        int limit = originalInfix.length();

        for (int i = 0; i < limit; i++) {
            temp.add(infix[i]);
            if (canConcatenate(infix, limit, i)) {
                temp.add('.');
            }
        }

        return generateInfixString(temp);
    }

    private static boolean canConcatenate(char[] infixArray, int limit, int index) {
        boolean existsNextChar = index + 1 < limit;
        char current = infixArray[index];
        if (existsNextChar) {
            boolean isCurrentAnOperand = isOperand(current);
            boolean isCurrentARightParenthesis = isRightParenthesis(current);
            boolean isCurrentAStar = isStar(current);
            char next = infixArray[index + 1];
            if (isCurrentAnOperand || isCurrentARightParenthesis || isCurrentAStar) {
                boolean isNextAnOperand = isOperand(next);
                boolean isNextALeftParenthesis = isLeftParenthesis(next);
                return (isNextAnOperand || isNextALeftParenthesis);
            }
        }
        return false;
    }

    private static boolean isStar(Character token) {
        return token == '*';
    }

    @NotNull
    private static String generateInfixString(Queue<Character> input) {
        StringBuilder sb = new StringBuilder();
        while (!input.isEmpty()) {
            sb.append(input.dequeue());
        }
        return sb.toString();
    }

    private static String infixToPostfix(char[] infix) {
        // Following Dijkstra's Shunting-Yard Algorithm
        Stack<Character> postfix = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (char token : infix) {
            if (isOperand(token)) {
                handleOperand(postfix, token);
            } else if (isOperator(token)) {
                handleOperator(postfix, operators, token);
            } else if (isLeftParenthesis(token)) {
                handleLeftParenthesis(operators, token);
            } else if (isRightParenthesis(token)) {
                handleRightParenthesis(postfix, operators);
            }
        }

        handleRemainingOperators(postfix, operators);
        return generatePostfixString(postfix);
    }

    private static boolean isOperand(char c) {
        return !(isOperator(c) || isLeftParenthesis(c) || isRightParenthesis(c));
    }

    private static void handleOperand(Stack<Character> postfix, char token) {
        postfix.push(token);
    }

    private static boolean isOperator(char c) {
        return c == '.' | c == '*' | c == '|';
    }

    private static void handleOperator(
            Stack<Character> postfix,
            Stack<Character> operators,
            char token
    ) {
        Character top = operators.peek();
        boolean existsTopOperator = top != null;
        while (existsTopOperator
                && (hasGreaterPrecedence(top, token)
                || (hasEqualPrecedence(top, token) && isLeftAssociative(token)))
                && !isLeftParenthesis(top)
        ) {
            postfix.push(operators.pop());
            top = operators.peek();
            existsTopOperator = top != null;
        }
        operators.push(token);
    }

    private static boolean hasGreaterPrecedence(Character top, Character token) {
        return getPrecedence(top) > getPrecedence(token);
    }

    private static boolean hasEqualPrecedence(Character top, Character token) {
        return getPrecedence(top) == getPrecedence(token);
    }

    private static boolean isLeftAssociative(Character token) {
        // | is reflexive
        return token == '.' || token == '*';
    }

    private static int getPrecedence(Character operator) {
        int precedence;

        switch (operator) {
            case '.':
                precedence = 1;
                break;
            case '|':
                precedence = 2;
                break;
            case '*':
                precedence = 3;
                break;

            // Unimplemented operator
            default:
                precedence = 0;
                break;
        }

        return precedence;
    }

    private static boolean isLeftParenthesis(Character token) {
        return token == '(';
    }

    private static void handleLeftParenthesis(
            Stack<Character> operators,
            char token
    ) {
        operators.push(token);
    }

    private static boolean isRightParenthesis(Character token) {
        return token == ')';
    }

    private static void handleRightParenthesis(
            Stack<Character> postfix,
            Stack<Character> operators
    ) {
        Character top = operators.peek();
        while (!isLeftParenthesis(top)) {
            postfix.push(operators.pop());
            top = operators.peek();
        }
        top = operators.peek();
        if (isLeftParenthesis(top)) {
            operators.pop();
        }
    }

    private static void handleRemainingOperators(
            Stack<Character> postfix,
            Stack<Character> operators
    ) {
        while (!operators.isEmpty()) {
            postfix.push(operators.pop());
        }
    }

    @NotNull
    private static String generatePostfixString(Stack<Character> input) {
        StringBuilder sb = new StringBuilder();
        Stack<Character> reverse = new Stack<>();
        while (!input.isEmpty()) {
            reverse.push(input.pop());
        }
        while (!reverse.isEmpty()) {
            sb.append(reverse.pop());
        }
        return sb.toString();
    }
}