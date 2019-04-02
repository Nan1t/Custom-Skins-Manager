package ru.csm.bukkit.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.api.network.Channels;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.player.BukkitSkinPlayer;

import java.io.IOException;
import java.util.UUID;

public class SkinsRefreshListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] bytes) {
        if(!channel.equals(Channels.SKINS_REFRESH)){
            return;
        }

        try {
            String str = IOUtils.toString(bytes, "UTF-8");
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();
            UUID uuid = UUID.fromString(json.get("player").getAsString());

            if (receiver.getUniqueId().equals(uuid)){
                SkinPlayer<Player> player = new BukkitSkinPlayer(receiver);
                player.refreshSkin();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
