package ru.csm.bukkit.messages.handlers;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.csm.api.network.MessageHandler;
import ru.csm.bukkit.menu.item.Skull;

public class HandlerSkull implements MessageHandler {

    @Override
    public void execute(JsonObject json) {
        Player player = Bukkit.getPlayer(json.get("player").getAsString());

        if (player != null){
            ItemStack item = Skull.getCustomSkull(json.get("url").getAsString());
            player.getInventory().addItem(item);
        }
    }

}
