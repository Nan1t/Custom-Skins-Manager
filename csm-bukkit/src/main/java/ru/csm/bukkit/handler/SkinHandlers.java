package ru.csm.bukkit.handler;

import ru.csm.api.utils.Logger;

import java.lang.reflect.Constructor;

public final class SkinHandlers {

    private static final String HANDLER_TEMPLATE = "ru.csm.bukkit.handler.Handler_%s";
    private static SkinHandler handler;

    private SkinHandlers(){}

    public static SkinHandler getHandler(){
        return handler;
    }

    public static void init(String version){
        try{
            Class<?> handlerClass = Class.forName(String.format(HANDLER_TEMPLATE, version));
            Constructor<?> handlerConstructor = handlerClass.getConstructor();
            handler = (SkinHandler) handlerConstructor.newInstance();
            Logger.info("Loaded skin handler for spigot version %s", version);
        } catch (Exception e){
            Logger.severe("Cannot load skin handler for spigot version " + version);
        }
    }
}
