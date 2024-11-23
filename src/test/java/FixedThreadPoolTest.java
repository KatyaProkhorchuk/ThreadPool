import org.example.FixedThreadPool;
import org.example.Future;
import org.example.Promise;
import org.example.WaitGroup;
//import org.junit.jupiter.api.Test;
import org.testng.annotations.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FixedThreadPoolTest {

    @Test
    public void TestIncrement() throws InterruptedException {

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
        assertEquals(10000 * 42, counter[0]);
        fixedTP.stop(false);
    }

    @Test
    public void TestStop() throws InterruptedException {

        FixedThreadPool fixedTP = new FixedThreadPool(100);
        final int[] counter = { 0 };
        for (int i = 0; i < 100000; ++i) {
            fixedTP.execute(() -> {
                for (int j = 0; j < 200; ++j) {
                    synchronized (counter) {
                        counter[0] += 1;
                    }
                }
            });
        }
        fixedTP.start();
        fixedTP.stop(false);
        assertEquals(100000 * 200, counter[0]);
    }

    @Test
    public void TestFuture() throws InterruptedException {
        Promise<Integer> asyncTaskResult = new Promise<>();
        Future<Integer> future = asyncTaskResult.getFuture();

        FixedThreadPool fixedTP = new FixedThreadPool(100);
        final int[] counter = { 0 };
        for (int i = 0; i < 100000; ++i) {
            fixedTP.execute(() -> {
                for (int j = 0; j < 200; ++j) {
                    synchronized (counter) {
                        counter[0] += 1;
                        if (counter[0] == 100) {
                            asyncTaskResult.setValue(100);
                        }
                    }
                }
            });
        }
        fixedTP.start();
        assertEquals(100, future.get());
        fixedTP.stop(false);
        assertEquals(200 * 100000, counter[0]);
    }
}
