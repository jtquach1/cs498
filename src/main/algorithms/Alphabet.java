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
}
