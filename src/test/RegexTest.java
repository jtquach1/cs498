import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexTest {

    @Test
    void infixToPostfix() {
        String expected = "ab|a*.b.";
        String actual = Regex.infixToPostfix("(a|b)a*b");
        assertEquals(expected, actual);
    }

    @Test
    void markWithConcatenation() {
        String expected = "(a|b).a*.b";
        String actual = Regex.markWithConcatenation("(a|b)a*b");
        assertEquals(expected, actual);
    }
}