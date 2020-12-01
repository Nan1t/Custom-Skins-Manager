package ru.csm.api.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class EventSkinReset {

    private final SkinPlayer player;
    private final Skin oldSkin;
    private boolean cancelled;

    public EventSkinReset(SkinPlayer player, Skin prevSkin){
        this.player = player;
        this.oldSkin = prevSkin;
    }

    public SkinPlayer getPlayer() {
        return player;
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
