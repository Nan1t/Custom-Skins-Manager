package ru.csm.bungee.event;

import net.md_5.bungee.api.plugin.Event;
import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangedEvent extends Event {

    private final EventSkinChanged event;

    public SkinChangedEvent(EventSkinChanged event){
        this.event = event;
    }

    public SkinPlayer getPlayer() {
        return event.getPlayer();
    }

    public Skin getSkin() {
        return event.getSkin();
    }

}
