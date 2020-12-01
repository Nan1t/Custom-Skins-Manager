package ru.csm.bukkit.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.event.EventSkinReset;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinResetEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EventSkinReset event;

    public SkinResetEvent(EventSkinReset event){
        this.event = event;
    }

    public SkinPlayer getPlayer() {
        return event.getPlayer();
    }

    public Skin getOldSkin() {
        return event.getOldSkin();
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
