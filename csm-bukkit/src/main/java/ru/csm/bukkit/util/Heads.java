package ru.csm.bukkit.util;

import org.bukkit.inventory.ItemStack;
import ru.csm.api.player.Head;
import ru.csm.bukkit.menu.item.Skull;

public final class Heads {

    private Heads(){}

    public static ItemStack toItemStack(Head head){
        return Skull.getCustomSkull(head.getUrl());
    }

}
