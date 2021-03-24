import java.util.Objects;

public class State {
    private final int id;
    private static int idCounter;

    public State() {
        this.id = idCounter++;
    }

    public int getId() {
        return this.id;
    }
}
