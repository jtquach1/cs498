import java.util.HashSet;

public class Alphabet {
    private HashSet<Character> symbols = new HashSet<>();

    public HashSet<Character> getSymbols() {
        return symbols;
    }

    public void addSymbol(Character newSymbol) {
        symbols.add(newSymbol);
    }

    @Override
    public String toString() {
        return symbols.toString();
    }
}
