package ru.csm.bukkit.util;

import org.bukkit.Material;

public final class Materials {

    public static Material HEAD;

    static {
        HEAD = Material.getMaterial("PLAYER_HEAD");

        if (HEAD == null){
            HEAD = Material.getMaterial("SKULL_ITEM");
        }
    }

    private Materials(){}
}
