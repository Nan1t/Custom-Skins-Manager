package ru.csm.bungee.event;

import net.md_5.bungee.api.plugin.Event;
import ru.csm.api.player.SkinPlayer;

public abstract class SkinEvent extends Event {

    private final SkinPlayer player;

    public SkinEvent(SkinPlayer player){
        this.player = player;
    }

    public SkinPlayer getPlayer() {
        return player;
    }

}
