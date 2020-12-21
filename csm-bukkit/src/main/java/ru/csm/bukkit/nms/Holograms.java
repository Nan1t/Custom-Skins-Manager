package ru.csm.bukkit.nms;

import ru.csm.api.logging.Logger;
import ru.csm.bukkit.hologram.Hologram;

import java.lang.invoke.MethodHandle;

public final class Holograms {

    private static MethodHandle handle;

    private Holograms(){}

    public static Hologram create(){
        try {
            return (Hologram) handle.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create hologram: ", t);
            return null;
        }
    }

    public static void init(String version){
        handle = NmsRegistry.HOLOGRAMS.getHologram(version);

        if (handle != null){
            Logger.info("Loaded holograms handler for %s", version);
        }
    }

}
