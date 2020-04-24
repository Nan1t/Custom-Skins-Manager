package ru.csm.bungee.network.executors;

import com.google.gson.JsonObject;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.MessageExecutor;

import java.util.UUID;

public class ExecutorCommandPlayer implements MessageExecutor {

    private final SkinsAPI api;

    public ExecutorCommandPlayer(SkinsAPI api){
        this.api = api;
    }

    @Override
    public JsonMessage execute(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("player").getAsString());
        String name = json.get("name").getAsString();
        SkinPlayer<?> player = api.getPlayer(uuid);

        if(player != null){
            api.setSkinFromName(player, name);
            return null;
        }

        return null;
    }

}
