package ru.csm.bukkit.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

/**
 * Called when skin already changed
 */
public class SkinChangedEvent extends SkinEvent {

    private final Skin newSkin;

    public SkinChangedEvent(SkinPlayer player, Skin newSkin){
        super(player);
        this.newSkin = newSkin;
    }

    public Skin getNewSkin() {
        return newSkin;
    }

}
