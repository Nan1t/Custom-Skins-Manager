package ru.csm.api.upload;

import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ImageQueue implements Runnable {

    private final Queue<Request> queue = new ConcurrentLinkedQueue<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> task;
    private int period;

    protected Optional<Request> pop(){
        return Optional.ofNullable(queue.poll());
    }

    public int getSize(){
        return queue.size();
    }

    public int getWaitSeconds(){
        return queue.size() * period;
    }

    public void push(SkinPlayer<?> player, String url, SkinModel model){
        queue.offer(new Request(player, url, model));
    }

    public void start(int period){
        this.period = period;
        if (task != null) stop();
        task = executor.scheduleAtFixedRate(this, 0, period, TimeUnit.SECONDS);
    }

    public void stop(){
        task.cancel(true);
    }

    static class Request {

        private final SkinPlayer<?> player;
        private final String url;
        private final SkinModel model;

        public Request(SkinPlayer<?> player, String url, SkinModel model){
            this.player = player;
            this.url = url;
            this.model = model;
        }

        public SkinPlayer<?> getPlayer() {
            return player;
        }

        public String getUrl() {
            return url;
        }

        public SkinModel getModel() {
            return model;
        }
    }
}
