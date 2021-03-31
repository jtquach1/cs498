import java.util.ArrayList;
import java.util.List;

public class State {
    private static int idCounter;

    // For marking the states visually
    private final int id;
    private State out;
    private State out1;
    private int lastlist;


    public State() {
        this.id = idCounter++;
    }

    public State(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setOut(State state) {
        this.out = state;
    }
}

class Frag {
    State start;
    List<State> outgoingArrows;

    public static List<State> initialize(State out) {
        List<State> list = new ArrayList<>();
        list.add(out);
        return list;
    }

    public static List<State> join(List<State> first, List<State> second) {
        List<State> list = new ArrayList<>();
        list.addAll(first);
        list.addAll(second);
        return list;
    }

    public static void patch(List<State> list, State state) {
        for (State s : list) {
            s.setOut(state);
        }
    }
}