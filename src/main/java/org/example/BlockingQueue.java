package org.example;


import java.util.ArrayList;
import java.util.Optional;

public class BlockingQueue<T> {
    private final ArrayList<T> data;
    volatile boolean closed;

    public BlockingQueue() {
        this.data = new ArrayList<>();
    }

    public synchronized void push(T value)  {
            data.add(value);
            notify();
    }

    public synchronized Optional<T> tryPop() {
        if (!data.isEmpty()) {
            return Optional.of(data.remove(0));
        }
        else {
            return Optional.empty();
        }
    }

    public synchronized Optional<T> pop() {
        try {
            while (data.isEmpty() && !closed) {
                wait();
            }
            if (!data.isEmpty()) {
                return  Optional.of(data.remove(0));
            }
            return Optional.empty();
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    public synchronized boolean isEmpty() {
        return data.isEmpty();
    }

    public synchronized void close(boolean cancelTasks) {
        if (cancelTasks) {
            data.clear();
        }
        closed = true;
        notifyAll();
    }
}
