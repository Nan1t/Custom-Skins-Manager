package ru.csm.api.upload.data;

import ru.csm.api.player.SkinPlayer;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class NameQueue implements Runnable {

    private final Queue<Request> queue = new ConcurrentLinkedQueue<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> task;
    private int period;

    protected Optional<Request> pop(){
        return Optional.ofNullable(queue.poll());
    }

    public int getWaitSeconds(){
        return queue.size() * period;
    }

    public void push(SkinPlayer<?> player, String name){
        queue.offer(new Request(player, name));
    }

    public void start(int period){
        this.period = period;
        if (task != null) stop();
        task = executor.scheduleAtFixedRate(this, 0, period, TimeUnit.SECONDS);
    }

    public void stop(){
        task.cancel(true);
    }

    @Override
    public void run() {
        pop().ifPresent((request)->{

        });
    }

    private static class Request {

        private final SkinPlayer<?> player;
        private final String name;

        public Request(SkinPlayer<?> player, String name){
            this.player = player;
            this.name = name;
        }

        public SkinPlayer<?> getPlayer() {
            return player;
        }

        public String getUrl() {
            return name;
        }
    }
}
