package ru.csm.bukkit.network.executors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.api.network.Channels;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.network.PluginMessageExecutor;
import ru.csm.bukkit.player.BukkitSkinPlayer;

import java.io.IOException;
import java.util.UUID;

public class SkinsRefreshExecutor extends PluginMessageExecutor {

    public SkinsRefreshExecutor(){
        super(Channels.SKINS_REFRESH);
    }

    @Override
    public void execute(Player receiver, JsonObject json) {
        UUID uuid = UUID.fromString(json.get("player").getAsString());

        if (receiver.getUniqueId().equals(uuid)){
            SkinPlayer<Player> player = new BukkitSkinPlayer(receiver);
            player.refreshSkin();
        }
    }

}
