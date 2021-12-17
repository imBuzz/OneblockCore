package me.buzz.coralmc.oneblockcore.common.database.queues;

import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public abstract class AbstractQueue<V> {
    protected final BlockingQueue<V> queue = new LinkedBlockingQueue<>();
    private final Thread thread;

    protected AbstractQueue(String threadName) {
        thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    V object = this.queue.take();
                    compute(object);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setName(threadName);
    }

    public abstract void compute(V object);

    public void add(final V object) {
        try {
            queue.put(object);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void computeAll() {
        while (!queue.isEmpty()) {
            try {
                V object = this.queue.take();
                compute(object);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

    }

    public void start() {
        thread.start();
    }

    public void stop() {
        computeAll();
        thread.interrupt();
    }

}