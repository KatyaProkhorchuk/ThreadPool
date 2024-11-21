package org.example;

import java.util.Optional;

public class Promise<T> {
    private Optional<T> value;

    public Future<T> getFuture() {
        return new Future<>(this);
    }

    public synchronized void setValue(T value) {
        this.value = Optional.ofNullable(value);
        notifyAll();
    }

    public T getValue() {
        return value.get();
    }


    boolean hasValue() {
        return value.isPresent();
    }
}
