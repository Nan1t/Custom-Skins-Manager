package ru.csm.velocity.event;

import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinReset;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangeEvent {

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

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public EventSkinChange getWrappedEvent(){
        return event;
    }

    public enum Source {

        USERNAME,
        IMAGE

    }
}
