package algorithms;

import org.jetbrains.annotations.NotNull;

class Regex {
    static String infixToPostfix(String infix) {
        infix = markWithConcatenation(infix);
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

    // standalone programs:
    // all this becomes eventually j--.jar
    // and then run these programs like
    // java -cp .:./lib/j--.jar Regex2NFA
    // -cp is just classpath. look at curr dir and in addition j--.jar
    // tells java where to look at for the classes
    public static void main(String[] args) {
        String originalInfix = "(cd*|b)*a";
        System.out.println("infix: " + Regex.markWithConcatenation(originalInfix));
        System.out.println("postfix: " + Regex.infixToPostfix(originalInfix));

        originalInfix = "a(bb)a";
        System.out.println("infix: " + Regex.markWithConcatenation(originalInfix));
        System.out.println("postfix: " + Regex.infixToPostfix(originalInfix));
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