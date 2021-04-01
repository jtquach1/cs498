import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Alphabet extends TreeSet<Character> {
    public Alphabet() {
        super();
    }

    public Alphabet(Alphabet alphabet) {
        addAll(alphabet);
    }

    public void addSymbol(Character newSymbol) {
        this.add(newSymbol);
    }

    public void addSymbols(Character... newSymbols) {
        this.addAll(Arrays.asList(newSymbols));
    }
    public void addSymbols(List<Character> newSymbols) {
        this.addAll(newSymbols);
    }

}
