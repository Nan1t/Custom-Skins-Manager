package ru.csm.bukkit.npc;

import ru.csm.api.player.Skin;
import ru.csm.bukkit.hologram.Hologram;

import java.util.List;

public abstract class AbstractNPC implements NPC {

    protected int id;
    protected Skin skin;
    protected String permission;
    protected List<String> displayName;
    protected Hologram hologram;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public void setDisplayName(List<String> name) {
        this.displayName = name;
    }
}
