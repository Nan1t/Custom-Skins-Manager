package ru.csm.bukkit.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.player.SkinPlayer;

public abstract class SkinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final SkinPlayer player;
    private boolean cancelled;

    public SkinEvent(SkinPlayer player){
        this.player = player;
    }

    public SkinPlayer getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
