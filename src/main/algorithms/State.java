import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class State implements Comparable<State> {
    private static int idCounter;

    // For marking the states visually
    private final int id;

    public State() {
        this.id = idCounter++;
    }

    public State(int id) {
        this.id = id;
    }

    public static void setIdCounter(int idCounter) {
        State.idCounter = idCounter;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int compareTo(@NotNull State other) {
        return Integer.compare(this.getId(), other.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id == state.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
