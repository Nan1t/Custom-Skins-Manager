package ru.csm.bukkit.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;

public final class Placeholders {

    private Placeholders(){}

    public static void init(){
        PlaceholderAPI.registerPlaceholderHook("csm", new SkinPlaceholders());
    }

}
