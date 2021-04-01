import org.jetbrains.annotations.NotNull;

public class Move implements Comparable<Move> {
    private final State from;
    private final Character consumed;
    private final State to;

    public Move(State from, Character consumed, State to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    public State getFrom() {
        return this.from;
    }

    public Character getConsumed() {
        return this.consumed;
    }

    public State getTo() {
        return this.to;
    }

    @Override
    public int compareTo(@NotNull Move other) {
        return Integer.compare(this.getFrom().getId(), other.getFrom().getId());
    }
}
