package ro.nicuch.elementalsx.protection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FieldQueueRunnable implements Runnable {
    private final static Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }

    public static void offer(Runnable runnable) {
        tasks.offer(runnable);
    }
}
