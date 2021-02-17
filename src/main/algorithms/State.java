public class State {
    private final int id;
    private static int idCounter;
    private final JSONElement e = new JSONElement();

    public State() {
        this.id = idCounter++;
        e.addAttribute("id", Integer.toString(id));
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    public void toJSON(JSONElement json) {
        json.addChild("State", e);
    }
}
