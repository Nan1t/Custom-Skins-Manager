package ru.csm.bukkit.protocol;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class HologramReflection {

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }

    public static Object getChatComponent(String text){
        try{
            Class<?> chatClass = getNMSClass("ChatComponentText");
            Constructor<?> chatConstructor = chatClass.getConstructor(String.class);

            Object component = chatConstructor.newInstance(text);

            return component;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Object getOptionalChatComponent(String text){
        return Optional.of(getChatComponent(text));
    }

}
