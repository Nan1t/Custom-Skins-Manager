package ru.csm.velocity.event;

import ru.csm.api.player.SkinPlayer;

public abstract class SkinEvent {

    private final SkinPlayer player;

    public SkinEvent(SkinPlayer player){
        this.player = player;
    }

    public SkinPlayer getPlayer() {
        return player;
    }

}
