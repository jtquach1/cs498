package algorithms;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

class DFAState implements Comparable<DFAState> {
    private static int idCounter;
    private final int id;
    private TreeSet<State> states;

    DFAState() {
        this.id = idCounter++;
        this.states = new TreeSet<>();
    }

    static void setIdCounter(int idCounter) {
        DFAState.idCounter = idCounter;
    }

    int getId() {
        return this.id;
    }

    TreeSet<State> getStates() {
        return this.states;
    }

    @Override
    public int compareTo(@NotNull DFAState other) {
        return Comparator.comparing(DFAState::getId)
                .thenComparing(s -> s.getStates().toString())
                .compare(this, other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAState state = (DFAState) o;
        return this.states.equals(state.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "";
    }

    public void addAll(Set<State> closure) {
        states.addAll(closure);
    }

    public boolean isEmpty() {
        return states.isEmpty();
    }


}
