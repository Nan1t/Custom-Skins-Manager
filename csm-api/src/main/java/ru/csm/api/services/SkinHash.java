package ru.csm.api.services;

import ru.csm.api.player.Skin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinHash {

    private static final Map<String, Skin> HASH = new ConcurrentHashMap<>();

    private SkinHash(){}

    public static Optional<Skin> get(String key){
        return Optional.ofNullable(HASH.get(key));
    }

    public static void add(String key, Skin skin){

    }

}
