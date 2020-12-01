package ru.csm.velocity.event;

import com.velocitypowered.api.proxy.ProxyServer;
import ru.csm.api.event.EventHandler;
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.event.EventSkinReset;

import java.util.function.Consumer;

public class VelocityEventHandler implements EventHandler {

    private final ProxyServer server;

    public VelocityEventHandler(ProxyServer server){
        this.server = server;
    }

    @Override
    public void fireSkinChange(EventSkinChange event) {
        // Not implemented
    }

    @Override
    public void fireSkinReset(EventSkinReset event) {
        // Not implemented
    }

    @Override
    public void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback) {
        server.getEventManager()
                .fire(new SkinChangeEvent(event))
                .thenAccept((e)->callback.accept(e.getWrappedEvent()));
    }

    @Override
    public void fireSkinReset(EventSkinReset event, Consumer<EventSkinReset> callback) {
        server.getEventManager()
                .fire(new SkinResetEvent(event))
                .thenAccept((e)->callback.accept(e.getWrappedEvent()));
    }
}
