import java.util.ArrayList;

class Queue<T> extends ArrayList<T> {
    void queue(T item) {
        this.add(0, item);
    }

    T dequeue() {
        return this.remove(0);
    }
}