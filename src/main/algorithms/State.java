public class State {
    private final int id;
    private static int idCounter;

    public State() {
        this.id = idCounter++;
    }

    public State(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
