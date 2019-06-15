package ru.csm.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.player.SkinPlayer;

public class SkinResetEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private SkinPlayer player;

    public SkinPlayer getPlayer() {
        return player;
    }

    public SkinResetEvent(SkinPlayer player){
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
