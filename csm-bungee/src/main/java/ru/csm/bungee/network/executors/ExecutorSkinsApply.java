package ru.csm.bungee.network.executors;

import com.google.gson.JsonObject;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.MessageExecutor;

import java.util.UUID;

public class ExecutorSkinsApply implements MessageExecutor {

    private SkinsAPI api;

    public ExecutorSkinsApply(SkinsAPI api){
        this.api = api;
    }

    @Override
    public JsonMessage execute(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("player").getAsString());
        SkinPlayer player = api.getPlayer(uuid);

        if(player != null && player.isOnline()){
            JsonObject skinJson = json.get("skin").getAsJsonObject();
            Skin skin = new Skin();
            skin.setValue(skinJson.get("value").getAsString());
            skin.setSignature(skinJson.get("signature").getAsString());
            api.setCustomSkin(player, skin);
        }

        return null;
    }

}
