import org.example.*;
import org.testng.annotations.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.example.WaitGroup;

public class ScalableThreadPoolTest {

    @Test
    public void TestIncrement() throws InterruptedException {

        WaitGroup wg = new WaitGroup();

        ScalableThreadPool fixedTP = new ScalableThreadPool(200, 300);
        final int[] counter = { 0 };
        for (int i = 0; i < 4455; ++i) {
            wg.add(1);
            fixedTP.execute(() -> {
                for (int j = 0; j < 100; ++j) {
                    synchronized (counter) {
                        counter[0] += 1;
                    }
                }
                wg.done();
            });
        }
        fixedTP.start();
        wg.await();
        assertEquals(100 * 4455, counter[0]);
        fixedTP.stop(true);
    }

    @Test
    public void TestStop() throws InterruptedException {

        ScalableThreadPool fixedTP = new ScalableThreadPool(100, 200);
        final int[] counter = { 0 };
        for (int i = 0; i < 4455; ++i) {
            fixedTP.execute(() -> {
                for (int j = 0; j < 100; ++j) {
                    synchronized (counter) {
                        counter[0] += 1;
                    }
                }
            });
        }
        fixedTP.start();
        fixedTP.stop(false);
        assertEquals(4455 * 100, counter[0]);
    }

    @Test
    public void TestFeature() throws InterruptedException {
        Promise<Integer> asyncTaskResult = new Promise<>();
        Future<Integer> future = asyncTaskResult.getFuture();

        ScalableThreadPool fixedTP = new ScalableThreadPool(100, 200);
        final int[] counter = { 0 };
        for (int i = 0; i < 4455; ++i) {
            fixedTP.execute(() -> {
                for (int j = 0; j < 100; ++j) {
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
        fixedTP.stop(false);
        assertEquals(100, future.get());
        assertEquals(4455 * 100, counter[0]);
    }
}
