package ru.csm.bukkit.gui;

import org.bukkit.Material;

public class Materials {

    public static Material getPlayerHead(){
        Material mat = Material.getMaterial("PLAYER_HEAD");
        if(mat == null){
            mat = Material.getMaterial("SKULL_ITEM");
        }
        return mat;
    }

}
