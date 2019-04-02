package ru.csm.bungee.network.executors;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.MessageExecutor;

import java.util.UUID;

public class ExecutorCommandUrl implements MessageExecutor {

    private SkinsAPI api;

    public ExecutorCommandUrl(SkinsAPI api){
        this.api = api;
    }

    @Override
    public JsonMessage execute(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("player").getAsString());
        String url = json.get("url").getAsString();
        SkinModel model = SkinModel.fromName(json.get("model").getAsString());
        SkinPlayer<ProxiedPlayer> player = api.getPlayer(uuid);

        if(player != null){
            api.setSkinFromImage(player, url, model);
            return null;
        }

        return null;
    }

}
