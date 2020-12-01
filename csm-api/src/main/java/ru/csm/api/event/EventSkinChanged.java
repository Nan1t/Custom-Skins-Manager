package ru.csm.api.event;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class EventSkinChanged {

    private final SkinPlayer player;
    private final Skin skin;

    public EventSkinChanged(SkinPlayer player, Skin skin){
        this.player = player;
        this.skin = skin;
    }

    public SkinPlayer getPlayer() {
        return player;
    }

    public Skin getSkin() {
        return skin;
    }

}
