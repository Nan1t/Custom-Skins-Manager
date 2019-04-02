package ru.csm.bungee.network.executors;

import com.google.gson.JsonObject;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinModel;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.MessageExecutor;
import ru.csm.bungee.player.BungeCitizensPlayer;

import java.util.UUID;

public class ExecutorSkinsCitizens implements MessageExecutor {

    private SkinsAPI api;

    public ExecutorSkinsCitizens(SkinsAPI api){
        this.api = api;
    }

    @Override
    public JsonMessage execute(JsonObject json) {
        UUID senderUUID = UUID.fromString(json.get("sender").getAsString());
        ProxiedPlayer proxiedPlayer = BungeeCord.getInstance().getPlayer(senderUUID);

        String npcUUID = json.get("npc").getAsString();
        String url = json.get("url").getAsString();
        SkinModel model = SkinModel.fromName(json.get("model").getAsString());

        BungeCitizensPlayer player = new BungeCitizensPlayer(proxiedPlayer, npcUUID);
        api.setSkinFromImage(player, url, model);
        return null;
    }

}
