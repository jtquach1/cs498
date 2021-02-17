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

    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("State", e);
        e.addAttribute("id", Integer.toString(id));
    }

    @Override
    public String toString() {
        return Integer.toString(id);
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
