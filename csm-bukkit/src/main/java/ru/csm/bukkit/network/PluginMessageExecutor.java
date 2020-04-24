package ru.csm.bukkit.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;

public abstract class PluginMessageExecutor implements PluginMessageListener {

    private String channel;

    public PluginMessageExecutor(String channel){
        this.channel = channel;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!this.channel.equals(channel)){
            return;
        }

        try{
            String str = IOUtils.toString(bytes, "UTF-8");
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();
            execute(player, json);
        } catch (IOException e){
            return;
        }
    }

    public abstract void execute(Player player, JsonObject json);
}
