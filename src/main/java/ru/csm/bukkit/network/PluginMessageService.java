package ru.csm.bukkit.network;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.bukkit.Skins;

public abstract class PluginMessageService implements PluginMessageListener {

    public static void sendMessage(Player sender, String channel, JsonObject json){
        sender.sendPluginMessage(Skins.getPlugin(), channel, json.toString().getBytes());
    }

}
