package ru.csm.api.event;

import java.util.function.Consumer;

public final class Events {

    private static EventHandler handler;

    private Events(){}

    public static void registerHandler(EventHandler h){
        handler = h;
    }

    public static void fireSkinChange(EventSkinChange event){
        if (handler != null){
            handler.fireSkinChange(event);
        }
    }

    public static void fireSkinReset(EventSkinReset event){
        if (handler != null){
            handler.fireSkinReset(event);
        }
    }

    public static void fireSkinChange(EventSkinChange event, Consumer<EventSkinChange> callback){
        if (handler != null){
            handler.fireSkinChange(event, callback);
        }
    }

    public static void fireSkinReset(EventSkinReset event, Consumer<EventSkinReset> callback){
        if (handler != null){
            handler.fireSkinReset(event, callback);
        }
    }

}
