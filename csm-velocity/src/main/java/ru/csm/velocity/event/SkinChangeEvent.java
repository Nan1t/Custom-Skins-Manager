package ru.csm.velocity.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

/**
 * Called before skin change. Can be cancelled
 */
public class SkinChangeEvent extends SkinEvent {

    private final Skin oldSkin;
    private Skin newSkin;
    private boolean cancelled;

    public SkinChangeEvent(SkinPlayer player, Skin oldSkin, Skin newSkin){
        super(player);
        this.oldSkin = oldSkin;
        this.newSkin = newSkin;
    }

    public Skin getOldSkin() {
        return oldSkin;
    }

    public Skin getNewSkin() {
        return newSkin;
    }

    public void setNewSkin(Skin skin){
        this.newSkin = skin;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
