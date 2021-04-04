package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

class Stack<T> extends ArrayList<T> {
    Stack() {
        super();
    }

    Stack(Collection<T> items) {
        this.addAll(items);
    }

    Stack(T... items) {
        this.addAll(Arrays.asList(items));
    }

    T pop() {
        int last = this.size() - 1;
        T item = this.remove(last);
        return item;
    }

    void push(T item) {
        this.add(item);
    }

    T peek() {
        if (this.isEmpty()) {
            return null;
        }
        int last = this.size() - 1;
        return this.get(last);
    }
}
