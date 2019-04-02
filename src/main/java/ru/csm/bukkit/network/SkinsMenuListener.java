package ru.csm.bukkit.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Head;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.gui.SkinMenu;
import ru.csm.bukkit.gui.managers.MenuManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinsMenuListener implements PluginMessageListener {

    private MenuManager manager;

    public SkinsMenuListener(MenuManager menuManager){
        this.manager = menuManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] bytes) {
        if(!channel.equals(Channels.SKINS_MENU)){
            return;
        }
        try {
            String str = IOUtils.toString(bytes, "UTF-8");
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();

            UUID playerUUID = UUID.fromString(json.get("player").getAsString());
            int page = json.get("page").getAsInt();
            int menuSize = json.get("size").getAsInt();

            if(receiver.getUniqueId().equals(playerUUID)){
                Player player = Bukkit.getPlayer(playerUUID);
                JsonObject heads = json.get("heads").getAsJsonObject();

                Map<UUID, Head> headsMap = new HashMap<>();

                for(Map.Entry<String, JsonElement> entry : heads.entrySet()){
                    UUID ownerUuid = UUID.fromString(entry.getKey());
                    JsonObject head = entry.getValue().getAsJsonObject();
                    String ownerName = head.get("ownerName").getAsString();

                    JsonObject skinJson = head.get("skin").getAsJsonObject();
                    String value = skinJson.get("value").getAsString();
                    String signature = skinJson.get("signature").getAsString();
                    Skin skin = new Skin(value, signature);

                    headsMap.put(ownerUuid, new Head(ownerUuid, ownerName, skin));
                }

                SkinMenu menu = new SkinMenu(menuSize, page, headsMap);
                manager.openMenu(player, menu);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
