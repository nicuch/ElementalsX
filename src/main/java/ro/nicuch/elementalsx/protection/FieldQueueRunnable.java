package ro.nicuch.elementalsx.protection;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FieldQueueRunnable implements Runnable {

    private final static ConcurrentLinkedQueue<FieldRunnable> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }

    public static void offer(FieldRunnable runnable) {
        tasks.offer(runnable);
    }
}
