public class State {
    private static int idCounter;
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
}
