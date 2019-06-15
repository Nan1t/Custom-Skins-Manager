package ru.csm.api.threads;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class ThreadPool {

    private volatile boolean isRunning = true;
    private final LinkedList<Runnable> queue = new LinkedList<>();

    public ThreadPool(int nThreads) {
        PoolWorker[] threads = new PoolWorker[nThreads];

        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    public void execute(Runnable task) {
        synchronized(queue) {
            queue.addLast(task);
            queue.notify();
        }
    }

    public void shutdown(){
        isRunning = false;
    }

    private class PoolWorker extends Thread {
        public void run() {
            Runnable task;

            while (isRunning) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        }
                        catch (InterruptedException ignored){

                        }
                    }

                    task = queue.removeFirst();
                }

                task.run();
            }
        }
    }
}