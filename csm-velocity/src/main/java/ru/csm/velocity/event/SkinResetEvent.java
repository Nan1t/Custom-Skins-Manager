package ru.csm.velocity.event;

import ru.csm.api.event.EventSkinReset;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinResetEvent {

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

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public EventSkinReset getWrappedEvent(){
        return event;
    }

}
