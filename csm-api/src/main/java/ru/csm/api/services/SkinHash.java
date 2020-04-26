package ru.csm.api.services;

import ru.csm.api.player.Skin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class SkinHash {

    private static final Map<String, Hash> HASH = new ConcurrentHashMap<>();
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);
    private static ScheduledFuture<?> task;

    private SkinHash(){}

    public static Optional<Skin> get(String key){
        return Optional.ofNullable(HASH.get(key.toLowerCase()));
    }

    public static void add(String key, Skin skin){
        HASH.put(key.toLowerCase(), new Hash(skin));
    }

    public static void startCleaner(){
        task = EXECUTOR.scheduleAtFixedRate(()->{
            HASH.entrySet().removeIf((entry)->System.currentTimeMillis() >= entry.getValue().getExpiryTime());
        }, 0, 30, TimeUnit.SECONDS);
        EXECUTOR.shutdown();
    }

    public static void stopCleaner(){
        if (task != null) task.cancel(true);
    }

    private static class Hash extends Skin {

        private final long expiryTime;

        public Hash(Skin skin){
            super(skin);
            this.expiryTime = System.currentTimeMillis() + 60000;
        }

        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
