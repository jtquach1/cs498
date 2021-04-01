import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(from, move.from)
                && Objects.equals(consumed, move.consumed)
                && Objects.equals(to, move.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, consumed, to);
    }
}
