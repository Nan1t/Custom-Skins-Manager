package ru.csm.bukkit.messages.handlers;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;

public class HandlerPreview implements MessageHandler {

    private final SkinsAPI<Player> api;

    public HandlerPreview(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(JsonObject json) {
        Player player = Bukkit.getPlayer(json.get("player").getAsString());
        Skin skin = new Skin();
        boolean openMenu = json.get("open_menu").getAsBoolean();
        String permission = null;

        skin.setValue(json.get("skin_value").getAsString());
        skin.setSignature(json.get("skin_signature").getAsString());

        if (json.has("permission") && !json.get("permission").isJsonNull()){
            permission = json.get("permission").getAsString();
        }

        api.showPreview(player, skin, openMenu, permission);
    }

}
