package ru.csm.api.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class EventSkinChange {

    private final SkinPlayer player;
    private final Source source;
    private final Skin oldSkin;
    private Skin newSkin;
    private boolean cancelled;

    public EventSkinChange(SkinPlayer player, Skin oldSkin, Skin newSkin, Source source){
        this.player = player;
        this.oldSkin = oldSkin;
        this.newSkin = newSkin;
        this.source = source;
    }

    public SkinPlayer getPlayer() {
        return player;
    }

    public Skin getOldSkin() {
        return oldSkin;
    }

    public Skin getNewSkin() {
        return newSkin;
    }

    public void setNewSkin(Skin newSkin) {
        this.newSkin = newSkin;
    }

    public Source getSource() {
        return source;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Source {

        USERNAME,
        IMAGE,
        TEXTURE;

    }
}
