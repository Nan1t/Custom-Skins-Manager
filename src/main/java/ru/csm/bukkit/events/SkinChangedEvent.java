package ru.csm.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private SkinPlayer player;
    private Skin skin;

    public SkinPlayer getPlayer() {
        return player;
    }

    public Skin getSkin() {
        return skin;
    }

    public SkinChangedEvent(SkinPlayer player, Skin skin){
        this.player = player;
        this.skin = skin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
