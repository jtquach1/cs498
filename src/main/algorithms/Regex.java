import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Regex {
    private final String infix;

    public Regex(String infix) {
        this.infix = markWithConcatenation(infix);
    }

    private static String markWithConcatenation(String originalInfix) {
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

    public static void main(String[] args) {
        Regex regex = new Regex("(cd*|b)*a");
        System.out.println("infix: " + regex.getInfix());
        System.out.println("postfix: " + regex.infixToPostfix());
    }

    private static boolean isOperand(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    private static boolean isOperator(char c) {
        return c == '.' | c == '*' | c == '|';
    }

    private static boolean hasGreaterPrecedence(Character top, Character token) {
        return getPrecedence(top) > getPrecedence(token);
    }

    private static boolean hasEqualPrecedence(Character top, Character token) {
        return getPrecedence(top) == getPrecedence(token);
    }

    private static int getPrecedence(Character operator) {
        // Default is unimplemented operator
        int precedence = switch (operator) {
            case '.' -> 1;
            case '|' -> 2;
            case '*' -> 3;
            default -> 0;
        };

        return precedence;
    }

    private static boolean isLeftParenthesis(Character top) {
        return top == '(';
    }

    private static boolean isRightParenthesis(Character top) {
        return top == ')';
    }

    private static boolean isStar(Character top) {
        return top == '*';
    }

    private static boolean isLeftAssociative(Character token) {
        // | is reflexive
        return token == '.' || token == '*';
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

    @NotNull
    private static String generateInfixString(Queue<Character> input) {
        StringBuilder sb = new StringBuilder();
        while (!input.isEmpty()) {
            sb.append(input.dequeue());
        }
        return sb.toString();
    }

    private static void handleLeftParenthesis(Stack<Character> operators, char token) {
        operators.push(token);
    }

    private static void handleRightParenthesis(Stack<Character> postfix, Stack<Character> operators) {
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

    private static void handleOperand(Stack<Character> postfix, char token) {
        postfix.push(token);
    }

    private static void handleOperator(Stack<Character> postfix, Stack<Character> operators, char token) {
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

    private static void handleRemainingOperators(Stack<Character> postfix, Stack<Character> operators) {
        while (!operators.isEmpty()) {
            postfix.push(operators.pop());
        }
    }

    public String getInfix() {
        return this.infix;
    }

    public String infixToPostfix() {
        // Following Dijkstra's Shunting-Yard Algorithm
        char[] infix = this.infix.toCharArray();
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
}


class Stack<T> extends ArrayList<T> {
    public T pop() {
        int last = this.size() - 1;
        T item = this.remove(last);
        return item;
    }

    public void push(T item) {
        this.add(item);
    }

    public T peek() {
        if (this.isEmpty()) {
            return null;
        }
        int last = this.size() - 1;
        return this.get(last);
    }
}

class Queue<T> extends ArrayList<T> {
    public void queue(T item) {
        this.add(0, item);
    }

    public T dequeue() {
        return this.remove(0);
    }
}