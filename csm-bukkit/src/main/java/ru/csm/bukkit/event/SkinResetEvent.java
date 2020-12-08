package ru.csm.bukkit.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinResetEvent extends SkinEvent {

    private final Skin oldSkin;

    public SkinResetEvent(SkinPlayer player, Skin oldSkin){
        super(player);
        this.oldSkin = oldSkin;
    }

    public Skin getOldSkin() {
        return oldSkin;
    }

}
