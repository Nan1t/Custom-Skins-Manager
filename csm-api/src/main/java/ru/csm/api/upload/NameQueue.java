package ru.csm.api.upload;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class NameQueue implements Runnable {

    private final SkinsAPI<?> api;
    private final Queue<Request> queue = new ConcurrentLinkedQueue<>();
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private ScheduledFuture<?> task;
    private int period;

    public NameQueue(SkinsAPI<?> api){
        this.api = api;
    }

    protected Optional<Request> pop(){
        return Optional.ofNullable(queue.poll());
    }

    public int getWaitSeconds(){
        return queue.size() * period;
    }

    public void push(SkinPlayer player, String name){
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
            if (request.getPlayer().isOnline()){
                Optional<Skin> hashed = SkinHash.get(request.getName());

                if (hashed.isPresent()){
                    api.setCustomSkin(request.getPlayer(), hashed.get());
                    return;
                }

                UUID targetUUID = MojangAPI.getUUID(request.getName());

                if(targetUUID != null){
                    Skin skin = MojangAPI.getPremiumSkin(targetUUID);

                    if(skin != null){
                        SkinHash.add(request.getName(), skin);
                        api.setCustomSkin(request.getPlayer(), skin);
                        return;
                    }
                }

                request.getPlayer().sendMessage(api.getLang().of("skin.name.error"));
            }
        });
    }

    private static class Request {

        private final SkinPlayer player;
        private final String name;

        public Request(SkinPlayer player, String name){
            this.player = player;
            this.name = name;
        }

        public SkinPlayer getPlayer() {
            return player;
        }

        public String getName() {
            return name;
        }
    }
}
