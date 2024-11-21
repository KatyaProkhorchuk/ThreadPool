package org.example;

public class Future<T> {
    final Promise<T> promise;

    public Future(Promise<T> promise) {
        this.promise = promise;
    }

    public T get() throws InterruptedException {
        synchronized (promise) {
            while (!promise.hasValue()) {
                promise.wait();
            }
            return promise.getValue();
        }
    }


}
