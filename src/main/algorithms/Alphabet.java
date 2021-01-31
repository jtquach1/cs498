import java.util.HashSet;

public class Alphabet {
    private HashSet<Character> symbols = new HashSet<>();

    public static void main(String[] args) {
        Alphabet alphabet = new Alphabet();
        alphabet.addSymbol('a');
        System.out.println(alphabet);
        alphabet.addSymbol('b');
        System.out.println(alphabet);
    }

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
