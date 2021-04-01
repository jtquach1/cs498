import org.jetbrains.annotations.NotNull;

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

    public int getId() {
        return this.id;
    }

    @Override
    public int compareTo(@NotNull State other) {
        return Integer.compare(this.getId(), other.getId());
    }
}
