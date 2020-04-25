package ru.csm.bukkit.hologram;

import ru.csm.api.utils.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public final class Holograms {

    private static final String HOLOGRAM_CLASS = "ru.csm.bukkit.hologram.Hologram_%s";
    private static MethodHandle constructor;

    private Holograms(){}

    public static Hologram create(){
        try{
            return (Hologram)constructor.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create new hologram: %s", t.getMessage());
        }
        return null;
    }

    public static void init(String version){
        try{
            Class<?> holoClass = Class.forName(String.format(HOLOGRAM_CLASS, version));
            Constructor<?> holoConstructor = holoClass.getConstructor();
            constructor = MethodHandles.lookup().unreflectConstructor(holoConstructor);
        } catch (Exception e){
            Logger.severe("Cannot initialize hologram class for spigot version %s: %s", version, e.getMessage());
        }
    }

}
