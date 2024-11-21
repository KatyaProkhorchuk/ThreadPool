package org.example;

import java.util.ArrayList;
import java.util.Optional;

public class FixedThreadPool implements ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final ArrayList<Thread> workers;

    private static class Workers extends Thread {
        private final BlockingQueue<Runnable> taskQueue;
        public Workers(BlockingQueue<Runnable> taskQueue) {
            this.taskQueue = taskQueue;
        }
        @Override
        public void run() {
            Optional<Runnable> task;
            do {
                task = taskQueue.pop();
                task.ifPresent(Runnable::run);
            } while (task.isPresent());
        }
    }

    public FixedThreadPool(int threadCount) {
        this.workers = new ArrayList<>(threadCount);
        this.taskQueue = new BlockingQueue<>();
        for (int i = 0; i < threadCount; i++) {
            this.workers.add(new Workers(taskQueue));
        }
    }

    @Override
    public void start() {
        for (Thread worker : workers) {
            worker.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        taskQueue.push(runnable);
    }

    @Override
    public void stop(boolean cancelTasks) throws InterruptedException {
        taskQueue.close(cancelTasks);
        for (Thread worker : workers) {
            worker.join();
        }
    }
}
