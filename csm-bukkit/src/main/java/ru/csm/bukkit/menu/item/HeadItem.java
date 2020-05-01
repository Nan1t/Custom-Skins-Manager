package ru.csm.bukkit.menu.item;

import ru.csm.api.player.Skin;

public class HeadItem {

    private final String name;
    private final Skin skin;

    public HeadItem(String name, Skin skin) {
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
