import java.util.ArrayList;

public class Queue<T> extends ArrayList<T> {
    public void queue(T item) {
        this.add(0, item);
    }

    public T dequeue() {
        return this.remove(0);
    }
}