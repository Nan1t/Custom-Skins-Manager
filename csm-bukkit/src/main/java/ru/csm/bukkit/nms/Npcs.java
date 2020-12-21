package ru.csm.bukkit.nms;

import ru.csm.api.logging.Logger;
import ru.csm.bukkit.npc.NPC;

import java.lang.invoke.MethodHandle;

public final class Npcs {

    private static MethodHandle handle;

    private Npcs(){}

    public static NPC create(){
        try {
            return (NPC) handle.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create NPC: ", t);
            return null;
        }
    }

    public static void init(String version){
        handle = NmsRegistry.NPC.getNpc(version);

        if (handle != null){
            Logger.info("Loaded NPC handler for %s", version);
        }
    }

}
