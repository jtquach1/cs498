import java.util.Objects;

public class Move {
    private State from;
    private Character consumed;
    private State to;

    public Move(State from, Character consumed, State to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("Move", e);
        e.addAttribute("From", from.toString());
        e.addAttribute("Consumed", consumed.toString());
        e.addAttribute("To", to.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        boolean isFromEqual = Objects.equals(from, move.from);
        boolean isSameConsumed = Objects.equals(consumed, move.consumed);
        boolean isSameTo = Objects.equals(to, move.to);
        return isFromEqual && isSameConsumed && isSameTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, consumed, to);
    }
}
