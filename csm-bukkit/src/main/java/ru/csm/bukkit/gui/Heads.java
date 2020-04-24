package ru.csm.bukkit.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.csm.api.player.Head;
import ru.csm.api.utils.text.Colors;

public class Heads {

    public static ItemStack toItemStack(Head head){
        ItemStack item = Skull.getCustomSkull(head.getSkin().getURL());
        NBTItem nbt = new NBTItem(item);
        nbt.setString("SkinOwnerUUID", head.getOwnerUuid().toString());
        nbt.setString("InventoryAction", "spawnNPC");

        if(head.getPermission() != null){
            nbt.setString("SkinPermission", head.getPermission());
        }

        item = nbt.getItem();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colors.of("&e" + head.getOwnerName()));

        if(head.getLore() != null){
            meta.setLore(head.getLore());
        }

        item.setItemMeta(meta);
        return item;
    }

}
