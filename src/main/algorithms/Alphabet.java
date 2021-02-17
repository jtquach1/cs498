import java.util.Arrays;
import java.util.HashSet;

public class Alphabet {
    private HashSet<Character> symbols = new HashSet<>();

    public HashSet<Character> getSymbols() {
        return symbols;
    }

    public void addSymbol(Character newSymbol) {
        symbols.add(newSymbol);
    }

    public void addSymbols(Character... newSymbols) {
        symbols.addAll(Arrays.asList(newSymbols));
    }

    public void toJSON(JSONElement json) {
        json.addAttribute("Alphabet", symbols.toString());
    }

}
