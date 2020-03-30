package ro.nicuch.elementalsx.protection;

import com.mfk.lockfree.queue.LockFreeQueue;

public class FieldQueueRunnable implements Runnable {
    private final static LockFreeQueue<Runnable> tasks = LockFreeQueue.newQueue(32);

    @Override
    public void run() {
        while (tasks.size() > 0) {
            tasks.poll().ifPresent(Runnable::run);
        }
    }

    public static void offer(Runnable runnable) {
        tasks.add(runnable);
    }
}
