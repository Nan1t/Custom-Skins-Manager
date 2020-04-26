package ru.csm.bukkit.menu;

import ru.csm.api.player.Skin;

public class PlayerHead {

    private final String name;
    private final Skin skin;

    public PlayerHead(String name, Skin skin) {
        this.name = name;
        this.skin = skin;
    }

    public String getName() {
        return name;
    }

    public Skin getSkin() {
        return skin;
    }
}
