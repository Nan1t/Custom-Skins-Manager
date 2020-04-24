package ru.csm.bukkit.network;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.bukkit.Skins;

public class PluginMessageService {

    private Plugin plugin;

    public PluginMessageService(Plugin plugin){
        this.plugin = plugin;
    }

    public void sendMessage(Player sender, String channel, JsonObject json){
        sender.sendPluginMessage(plugin, channel, json.toString().getBytes());
    }
}
