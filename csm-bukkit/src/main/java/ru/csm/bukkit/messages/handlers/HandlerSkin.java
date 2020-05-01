package ru.csm.bukkit.messages.handlers;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.handler.SkinHandlers;

public class HandlerSkin implements MessageHandler {

    @Override
    public void execute(JsonObject json) {
        Player player = Bukkit.getPlayer(json.get("player").getAsString());
        Skin skin = new Skin();

        skin.setValue(json.get("skin_value").getAsString());
        skin.setSignature(json.get("skin_signature").getAsString());

        SkinHandlers.getHandler().applySkin(player, skin);
        SkinHandlers.getHandler().updateSkin(player);
    }

}
