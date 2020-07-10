package ru.csm.bukkit.npc;

import ru.csm.api.logging.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public final class Npcs {

    private static final String NPC_CLASS = "ru.csm.bukkit.npc.NPC_%s";
    private static MethodHandle constructor;

    private Npcs(){}

    public static void init(String version){
        try{
            Class<?> npcClass = Class.forName(String.format(NPC_CLASS, version));
            Constructor<?> npcConstructor = npcClass.getConstructor();
            constructor = MethodHandles.lookup().unreflectConstructor(npcConstructor);
        } catch (Exception e){
            Logger.severe("Cannot load npc for spigot version %s: %s", version, e.getMessage());
        }
    }

    public static NPC create(){
        try{
            return (NPC) constructor.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create NPC: %s", t.getMessage());
        }
        return null;
    }
}
