package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        WaitGroup wg = new WaitGroup();

        FixedThreadPool fixedTP = new FixedThreadPool(104);
        final int[] counter = { 0 };
        for (int i = 0; i < 42; ++i) {
            wg.add(1);
            fixedTP.execute(() -> {
                for (int j = 0; j < 10000; ++j) {
                    synchronized (counter) {
                        counter[0] += 1;
                    }
                }
                wg.done();
            });
        }
        fixedTP.start();
        wg.await();
        fixedTP.stop(false);
    }
}