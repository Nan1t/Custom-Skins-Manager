package ru.csm.velocity.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

/**
 * Called before skin reset. Can be cancelled
 */
public class SkinResetEvent extends SkinEvent {

    private final Skin oldSkin;
    private boolean cancelled;

    public SkinResetEvent(SkinPlayer player, Skin oldSkin){
        super(player);
        this.oldSkin = oldSkin;
    }
    
    public Skin getOldSkin() {
        return oldSkin;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
