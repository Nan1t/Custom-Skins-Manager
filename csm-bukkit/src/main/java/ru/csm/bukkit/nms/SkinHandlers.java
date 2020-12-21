package ru.csm.bukkit.nms;

import ru.csm.api.logging.Logger;
import ru.csm.bukkit.player.SkinHandler;

import java.lang.invoke.MethodHandle;

public final class SkinHandlers {

    private static SkinHandler handler;

    private SkinHandlers(){}

    public static SkinHandler getHandler(){
        return handler;
    }

    public static void init(String version){
        MethodHandle handle = NmsRegistry.SKIN_HANDLERS.getSkinHandler(version);

        if (handle != null){
            try {
                handler = (SkinHandler) handle.invoke();
                Logger.info("Loaded skin handler for %s", version);
            } catch (Throwable t){
                Logger.severe("Cannot get skin handler: ", t);
            }
        }
    }

}
