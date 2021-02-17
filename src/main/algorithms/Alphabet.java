import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Alphabet extends HashSet<Character> {
    public void addSymbol(Character newSymbol) {
        this.add(newSymbol);
    }

    public void addSymbols(Character... newSymbols) {
        this.addAll(Arrays.asList(newSymbols));
    }

    public void addSymbols(List<Character> newSymbols) {
        this.addAll(newSymbols);
    }

    public void toJSON(JSONElement json) {
        json.addAttribute("Alphabet", this.toString());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
