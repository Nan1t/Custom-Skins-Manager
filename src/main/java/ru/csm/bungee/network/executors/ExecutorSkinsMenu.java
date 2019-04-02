package ru.csm.bungee.network.executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.player.Head;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.MessageExecutor;

import java.util.Map;
import java.util.UUID;

public class ExecutorSkinsMenu implements MessageExecutor {

    private SkinsAPI api;
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public ExecutorSkinsMenu(SkinsAPI api){
        this.api = api;
    }

    @Override
    public JsonMessage execute(JsonObject json) {
        int page = json.get("page").getAsInt();
        UUID playerUuid = UUID.fromString(json.get("player").getAsString());
        ProxiedPlayer player = BungeeCord.getInstance().getPlayer(playerUuid);

        int menuSize = api.getMenuSize();
        Map<UUID, Head> heads = api.getHeads(menuSize, page);

        String jsonStr = gson.toJson(heads);
        JsonObject headsJson = new JsonParser().parse(jsonStr).getAsJsonObject();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("player", playerUuid.toString());
        responseJson.addProperty("page", page);
        responseJson.addProperty("size", menuSize);
        responseJson.add("heads", headsJson);

        return new JsonMessage(Channels.SKINS_MENU, player, responseJson);
    }

}
