package ro.nicuch.elementalsx.protection;

import com.mfk.lockfree.queue.LockFreeQueue;

public class FieldQueueRunnable implements Runnable {
    private final static LockFreeQueue<FieldRunnable> tasks = LockFreeQueue.newQueue(1);

    @Override
    public void run() {
        while (tasks.size() > 0) {
            tasks.poll().ifPresent(FieldRunnable::run);
        }
    }

    public static void offer(FieldRunnable runnable) {
        tasks.add(runnable);
    }
}
