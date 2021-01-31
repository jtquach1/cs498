import java.util.Arrays;
import java.util.HashSet;

public class Alphabet {
    private HashSet<String> symbols;

    public HashSet<String> getSymbols() {
        return symbols;
    }

    public void addSymbol(String newSymbol) {
        symbols.add(newSymbol);
    }

    @Override
    public String toString() {
        return symbols.toString();
    }
}
