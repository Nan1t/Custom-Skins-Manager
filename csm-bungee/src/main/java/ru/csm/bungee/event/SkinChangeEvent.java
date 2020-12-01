package ru.csm.bungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangeEvent extends Event implements Cancellable {

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

    public enum Source {

        USERNAME,
        IMAGE

    }
}
