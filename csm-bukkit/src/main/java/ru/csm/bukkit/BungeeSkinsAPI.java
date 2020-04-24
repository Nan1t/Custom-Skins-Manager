package ru.csm.bukkit;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.bukkit.network.PluginMessageService;

public class BungeeSkinsAPI extends SkinsAPI {

    private final PluginMessageService pmService;

    public BungeeSkinsAPI(Database database, Configuration conf, Language lang, PluginMessageService pmService) {
        super(database, conf, lang);
        this.pmService = pmService;
    }

    @Override
    public void setCustomSkin(SkinPlayer<?> player, Skin skin){
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUUID().toString());

        JsonObject skinJson = new JsonObject();
        skinJson.addProperty("value", skin.getValue());
        skinJson.addProperty("signature", skin.getSignature());

        json.add("skin", skinJson);

        sendJsonMessage(player, Channels.SKINS_APPLY, json);
    }

    @Override
    public void setSkinFromImage(SkinPlayer<?> player, String imageUrl, SkinModel model) {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUUID().toString());
        json.addProperty("url", imageUrl);
        json.addProperty("model", model.getName());
        sendJsonMessage(player, Channels.SKINS_URL, json);
    }

    @Override
    public void setSkinFromName(SkinPlayer<?> player, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUUID().toString());
        json.addProperty("name", name);

        sendJsonMessage(player, Channels.SKINS_PLAYER, json);
    }

    @Override
    public void resetSkin(SkinPlayer<?> player) {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUUID().toString());

        sendJsonMessage(player, Channels.SKINS_RESET, json);
    }

    private void sendJsonMessage(SkinPlayer<?> player, String channel, JsonObject json){
        Player bukkitPlayer = (Player) player.getPlayer();
        pmService.sendMessage(bukkitPlayer, channel, json);
    }
}
