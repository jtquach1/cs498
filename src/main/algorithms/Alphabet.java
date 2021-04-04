package algorithms;

import java.util.TreeSet;

class Alphabet extends TreeSet<Character> {
    Alphabet() {
        super();
    }

    Alphabet(Alphabet alphabet) {
        addAll(alphabet);
    }

    void addSymbol(Character newSymbol) {
        this.add(newSymbol);
    }
}
