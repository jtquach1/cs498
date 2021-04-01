import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NFATest {
    NFA nfa;

    @BeforeEach
    void setUp() {
        Alphabet alphabet = new Alphabet();
        Set<State> states = new TreeSet<>();
        State start = new State(4);
        Set<State> finalStates = new TreeSet<>();
        State finalState = new State(12);
        Set<Move> moves = new TreeSet<>();

        alphabet.addSymbols('a', 'b');
        moves.add(new Move(new State(0), 'a', new State(1)));
        moves.add(new Move(new State(1), NFA.EPSILON, new State(5)));
        moves.add(new Move(new State(4), NFA.EPSILON, new State(0)));
        moves.add(new Move(new State(5), NFA.EPSILON, new State(10)));
        moves.add(new Move(new State(7), 'a', new State(8)));
        moves.add(new Move(new State(8), NFA.EPSILON, new State(7)));
        moves.add(new Move(new State(9), NFA.EPSILON, new State(11)));
        moves.add(new Move(new State(10), NFA.EPSILON, new State(7)));
        moves.add(new Move(new State(11), 'b', new State(12)));
        states.add(start);
        states.add(finalState);
        finalStates.add(finalState);

        nfa = new NFA(alphabet, states, start, finalStates, moves);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void regexToNFA() {
        String originalInfix = "(a|b)a*b";
//        String originalInfix = "(a|b)";
        NFA expected = nfa;
        NFA actual = NFA.regexToNFA(originalInfix);
        assertEquals(expected, actual);
    }

    @Test
    void convert() {
    }

    @Test
    void makeSingle() {
    }

    @Test
    void testClone() {
    }

    @Test
    void concatenate() {
    }

    @Test
    void kleeneStar() {
    }

    @Test
    void alternate() {
    }
}