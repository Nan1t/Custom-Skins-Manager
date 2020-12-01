package ru.csm.bungee.event;

import net.md_5.bungee.api.ProxyServer;
import ru.csm.api.event.EventHandler;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.event.EventSkinReset;

import java.util.function.Consumer;

public class BungeeEventHandler implements EventHandler {

    @Override
    public void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback) {
        ProxyServer.getInstance().getPluginManager().callEvent(new SkinChangeEvent(event));
        callback.accept(event);
    }

    @Override
    public void fireSkinChanged(EventSkinChanged event) {
        ProxyServer.getInstance().getPluginManager().callEvent(new SkinChangedEvent(event));
    }

    @Override
    public void fireSkinReset(EventSkinReset event) {
        ProxyServer.getInstance().getPluginManager().callEvent(new SkinResetEvent(event));
    }

}
