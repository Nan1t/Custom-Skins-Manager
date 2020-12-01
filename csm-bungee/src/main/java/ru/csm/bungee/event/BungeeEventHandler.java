package ru.csm.bungee.event;

import net.md_5.bungee.api.ProxyServer;
import ru.csm.api.event.EventHandler;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinReset;

public class BungeeEventHandler implements EventHandler {

    @Override
    public void fireSkinChange(EventSkinChange event) {
        ProxyServer.getInstance().getPluginManager().callEvent(new SkinChangeEvent(event));
    }

    @Override
    public void fireSkinReset(EventSkinReset event) {
        ProxyServer.getInstance().getPluginManager().callEvent(new SkinResetEvent(event));
    }

}
