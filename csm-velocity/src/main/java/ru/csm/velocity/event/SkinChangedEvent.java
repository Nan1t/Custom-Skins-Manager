package ru.csm.velocity.event;

import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinChangedEvent {

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
