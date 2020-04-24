package ru.csm.bukkit.handler;

import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.util.BukkitTasks;

public interface SkinHandler {

    Skin getSkin(Player player);

    void applySkin(Player player, Skin skin);

    void updateSkin(Player player);

    default void updateData(Player player){
        BukkitTasks.runTask(()->{
            player.updateInventory();
            player.setExp(player.getExp());
            player.setLevel(player.getLevel());
            player.setHealth(player.getHealth());
            player.setFlying(player.isFlying());
            player.setPlayerListName(player.getPlayerListName());
        });
    }
}
