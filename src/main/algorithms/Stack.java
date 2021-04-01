import java.util.ArrayList;

public class Stack<T> extends ArrayList<T> {
    public T pop() {
        int last = this.size() - 1;
        T item = this.remove(last);
        return item;
    }

    public void push(T item) {
        this.add(item);
    }

    public T peek() {
        if (this.isEmpty()) {
            return null;
        }
        int last = this.size() - 1;
        return this.get(last);
    }
}
