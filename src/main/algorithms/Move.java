import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class Move implements Comparable<Move> {
    private final State from;
    private final Character consumed;
    private final State to;

    Move(State from, Character consumed, State to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    State getFrom() {
        return this.from;
    }

    Character getConsumed() {
        return this.consumed;
    }

    State getTo() {
        return this.to;
    }

    @Override
    public int compareTo(@NotNull Move other) {
        return Integer.compare(this.getFrom().getId(), other.getFrom().getId())
                - Character.compare(this.consumed, other.consumed)
                - Integer.compare(this.getTo().getId(), other.getTo().getId());
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
