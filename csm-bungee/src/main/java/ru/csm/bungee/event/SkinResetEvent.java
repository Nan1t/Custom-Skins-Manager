package ru.csm.bungee.event;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import ru.csm.api.event.EventSkinReset;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

public class SkinResetEvent extends Event implements Cancellable {

    private final EventSkinReset event;

    public SkinResetEvent(EventSkinReset event){
        this.event = event;
    }

    public SkinPlayer getPlayer() {
        return event.getPlayer();
    }

    public Skin getOldSkin() {
        return event.getOldSkin();
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

}
