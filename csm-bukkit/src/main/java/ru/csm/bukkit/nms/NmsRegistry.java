package ru.csm.bukkit.nms;

import ru.csm.api.logging.Logger;
import ru.csm.bukkit.nms.skin.*;
import ru.csm.bukkit.nms.hologram.*;
import ru.csm.bukkit.nms.npc.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public enum NmsRegistry {

    SKIN_HANDLERS {
        {
            regSkinHandler("v1_8_R3", Handler_v1_8_R3.class);
            regSkinHandler("v1_9_R2", Handler_v1_9_R2.class);
            regSkinHandler("v1_10_R1", Handler_v1_10_R1.class);
            regSkinHandler("v1_11_R1", Handler_v1_11_R1.class);
            regSkinHandler("v1_12_R1", Handler_v1_12_R1.class);
            regSkinHandler("v1_13_R2", Handler_v1_13_R2.class);
            regSkinHandler("v1_14_R1", Handler_v1_14_R1.class);
            regSkinHandler("v1_15_R1", Handler_v1_15_R1.class);
            regSkinHandler("v1_16_R1", Handler_v1_16_R1.class);
            regSkinHandler("v1_16_R2", Handler_v1_16_R2.class);
            regSkinHandler("v1_16_R3", Handler_v1_16_R3.class);
        }
    },
    HOLOGRAMS{
        {
            regHologram("v1_8_R3", Hologram_v1_8_R3.class);
            regHologram("v1_9_R2", Hologram_v1_9_R2.class);
            regHologram("v1_10_R1", Hologram_v1_10_R1.class);
            regHologram("v1_11_R1", Hologram_v1_11_R1.class);
            regHologram("v1_12_R1", Hologram_v1_12_R1.class);
            regHologram("v1_13_R2", Hologram_v1_13_R2.class);
            regHologram("v1_14_R1", Hologram_v1_14_R1.class);
            regHologram("v1_15_R1", Hologram_v1_15_R1.class);
            regHologram("v1_16_R1", Hologram_v1_16_R1.class);
            regHologram("v1_16_R2", Hologram_v1_16_R2.class);
            regHologram("v1_16_R3", Hologram_v1_16_R3.class);
        }
    },
    NPC{
        {
            regNpc("v1_8_R3", NPC_v1_8_R3.class);
            regNpc("v1_9_R2", NPC_v1_9_R2.class);
            regNpc("v1_10_R1", NPC_v1_10_R1.class);
            regNpc("v1_11_R1", NPC_v1_11_R1.class);
            regNpc("v1_12_R1", NPC_v1_12_R1.class);
            regNpc("v1_13_R2", NPC_v1_13_R2.class);
            regNpc("v1_14_R1", NPC_v1_14_R1.class);
            regNpc("v1_15_R1", NPC_v1_15_R1.class);
            regNpc("v1_16_R1", NPC_v1_16_R1.class);
            regNpc("v1_16_R2", NPC_v1_16_R2.class);
            regNpc("v1_16_R3", NPC_v1_16_R3.class);
        }
    };

    private final Map<String, MethodHandle> skinHandlers = new HashMap<>();
    private final Map<String, MethodHandle> holograms = new HashMap<>();
    private final Map<String, MethodHandle> npc = new HashMap<>();

    public MethodHandle getSkinHandler(String version){
        MethodHandle handle = skinHandlers.get(version);

        if (handle == null){
            Logger.severe("Unsupported version: %s. Cannot load skin handler!", version);
            return null;
        }

        return handle;
    }

    public MethodHandle getHologram(String version){
        MethodHandle handle = holograms.get(version);

        if (handle == null){
            Logger.severe("Unsupported version: %s. Cannot load holograms!", version);
            return null;
        }

        return handle;
    }

    public MethodHandle getNpc(String version){
        MethodHandle handle = npc.get(version);

        if (handle == null){
            Logger.severe("Unsupported version: %s. Cannot load NPC!", version);
            return null;
        }

        return handle;
    }

    public void regSkinHandler(String version, Class<?> handlerType){
        try {
            skinHandlers.put(version, MethodHandles.lookup()
                    .findConstructor(handlerType, MethodType.methodType(void.class)));
        } catch (Exception e){
            // Ignore
        }
    }

    public void regHologram(String version, Class<?> handlerType){
        try {
            holograms.put(version, MethodHandles.lookup()
                    .findConstructor(handlerType, MethodType.methodType(void.class)));
        } catch (Exception e){
            // Ignore
        }
    }

    public void regNpc(String version, Class<?> handlerType){
        try {
            npc.put(version, MethodHandles.lookup()
                    .findConstructor(handlerType, MethodType.methodType(void.class)));
        } catch (Exception e){
            // Ignore
        }
    }

}
