package ru.csm.bukkit.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final EventSkinChange event;

    public SkinChangeEvent(EventSkinChange event){
        this.event = event;
    }

    public SkinPlayer getPlayer() {
        return event.getPlayer();
    }

    public Skin getOldSkin() {
        return event.getOldSkin();
    }

    public Skin getNewSkin() {
        return event.getNewSkin();
    }

    public void setNewSkin(Skin skin){
        event.setNewSkin(skin);
    }

    public Source getSource() {
        switch (event.getSource()) {
            case USERNAME:
                return Source.USERNAME;
            case IMAGE:
                return Source.IMAGE;
            case TEXTURE:
                return Source.TEXTURE;
        }
        return null;
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

    public enum Source {

        USERNAME,
        IMAGE,
        TEXTURE;

    }
}
