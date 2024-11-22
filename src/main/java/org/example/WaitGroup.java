package org.example;

public class WaitGroup {
    private int countWorkers = 0;

    public synchronized void add(int i) {
        countWorkers += i;
    }

    public synchronized void done() {
        if (--countWorkers == 0) {
            notifyAll();
        }
    }
    
    public synchronized void await() throws InterruptedException {
        while (countWorkers > 0) {
            wait();
        }
    }
}
