package org.example;

import java.util.ArrayList;
import java.util.Optional;

public class ScalableThreadPool implements ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final ArrayList<Thread> workers;

    // thread counters
    private int aliveThreads;
    private final int maxThreads;
    private final int minThreads;

    private static class Worker extends Thread {
        private final ScalableThreadPool threadPool;
        private final boolean important;


        public Worker(ScalableThreadPool threadPool, boolean important) {
            this.important = important;
            this.threadPool = threadPool;
        }

        @Override
        public void run() {
            Optional<Runnable> task;
            do {
                if (important) {
                    task = threadPool.taskQueue.pop();
                } else {
                    task = threadPool.taskQueue.tryPop();
                }
                task.ifPresent(Runnable::run);
            } while (task.isPresent());
            synchronized (threadPool) {
                threadPool.aliveThreads -= 1;
            }
        }
    }

    private synchronized void ensureMinThreads() {
        while (aliveThreads < minThreads) {
            workers.add(new Worker(this, true));
            aliveThreads++;
        }
    }

    public ScalableThreadPool(int minThreads, int maxThreads) {
        workers = new ArrayList<>(minThreads);
        taskQueue = new BlockingQueue<>();
        aliveThreads = minThreads;
        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        for (int i = 0; i < minThreads; i++) {
            workers.add(new Worker(this, true));
        }
    }

    @Override
    public synchronized void start() {
        for (Thread worker : workers) {
            worker.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        removeGarbageThreads();
        ensureMinThreads();
        synchronized (this) {
            if (!taskQueue.isEmpty() && aliveThreads < maxThreads) {
                ++aliveThreads;
                workers.add(new Worker(this, false));
            }
        }
        taskQueue.push(runnable);
    }

    @Override
    public void stop(boolean cancelTasks) throws InterruptedException {
        removeGarbageThreads();
        taskQueue.close(cancelTasks);
        for (Thread worker : workers) {
            worker.join();
        }
    }

    private synchronized void removeGarbageThreads() {
        if (aliveThreads < workers.size()) {
            workers.removeIf(thread -> !thread.isAlive());
        }
    }
}
