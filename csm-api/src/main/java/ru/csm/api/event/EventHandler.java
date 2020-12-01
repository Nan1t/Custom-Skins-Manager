package ru.csm.api.event;

import java.util.function.Consumer;

public interface EventHandler {

    void fireSkinChange(EventSkinChange event);

    void fireSkinReset(EventSkinReset event);

    default void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback){
        fireSkinChange(event);
        callback.accept(event);
    }

    default void fireSkinReset(EventSkinReset event, Consumer<EventSkinReset> callback){
        fireSkinReset(event);
        callback.accept(event);
    }

}
