package ru.csm.api.event;

import java.util.function.Consumer;

public interface EventHandler {

    void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback);

    void fireSkinChanged(EventSkinChanged event);

    void fireSkinReset(EventSkinReset event);

    default void fireSkinReset(EventSkinReset event, Consumer<EventSkinReset> callback){
        fireSkinReset(event);
        callback.accept(event);
    }

}
