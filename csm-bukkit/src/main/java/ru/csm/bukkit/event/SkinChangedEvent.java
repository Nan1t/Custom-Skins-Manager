package ru.csm.bukkit.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EventSkinChanged event;

    public SkinChangedEvent(EventSkinChanged event){
        this.event = event;
    }

    public SkinPlayer getPlayer() {
        return event.getPlayer();
    }

    public Skin getSkin() {
        return event.getSkin();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
