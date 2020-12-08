package ru.csm.bukkit.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangeEvent extends SkinEvent {

    private final Skin oldSkin;
    private Skin newSkin;

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

}
