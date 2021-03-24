public class Move {
    private State from;
    private Character consumed;
    private State to;

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

}
