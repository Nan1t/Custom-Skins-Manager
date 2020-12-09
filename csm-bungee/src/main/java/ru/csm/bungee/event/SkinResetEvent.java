package ru.csm.bungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

/**
 * Called before skin reset. Can be cancelled
 */
public class SkinResetEvent extends SkinEvent implements Cancellable {

    private final Skin oldSkin;
    private boolean cancelled;

    public SkinResetEvent(SkinPlayer player, Skin oldSkin){
        super(player);
        this.oldSkin = oldSkin;
    }
    
    public Skin getOldSkin() {
        return oldSkin;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
