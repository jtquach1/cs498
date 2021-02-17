public class Move {
    private State from;
    private Character consumed;
    private State to;
    private final JSONElement e = new JSONElement();

    public Move(State from, Character consumed, State to) {
        this.from = from;
        this.consumed = consumed;
        this.to = to;
    }

    public void toJSON(JSONElement json) {
        e.addAttribute("From", from.toString());
        e.addAttribute("Consumed", consumed.toString());
        e.addAttribute("To", to.toString());
        json.addChild("Move", e);
    }

}
