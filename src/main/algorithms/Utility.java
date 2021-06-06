package algorithms;

import java.util.ArrayList;
import java.util.Collection;

class Queue<T> extends ArrayList<T> {
    void queue(T item) {
        this.add(0, item);
    }

    T dequeue() {
        return this.remove(0);
    }
}

class Stack<T> extends ArrayList<T> {
    Stack() {
        super();
    }

    Stack(Collection<T> items) {
        this.addAll(items);
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