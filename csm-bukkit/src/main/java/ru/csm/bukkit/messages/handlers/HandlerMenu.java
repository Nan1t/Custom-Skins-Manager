package ru.csm.bukkit.messages.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.menu.SkinsMenu;
import ru.csm.bukkit.menu.item.HeadItem;
import ru.csm.bukkit.services.MenuManager;

import java.util.ArrayList;
import java.util.List;

public class HandlerMenu implements MessageHandler {

    private final SkinsAPI<Player> api;
    private final MenuManager menuManager;

    public HandlerMenu(SkinsAPI<Player> api, MenuManager menuManager){
        this.api = api;
        this.menuManager = menuManager;
    }

    @Override
    public void execute(JsonObject json) {
        Player player = Bukkit.getPlayer(json.get("player").getAsString());
        int page = json.get("page").getAsInt();
        List<HeadItem> items = new ArrayList<>();

        JsonArray heads = json.get("heads").getAsJsonArray();

        for (JsonElement elem : heads){
            JsonObject head = elem.getAsJsonObject();

            String name = head.get("name").getAsString();
            String texture = head.get("texture").getAsString();
            String signature = head.get("signature").getAsString();

            items.add(new HeadItem(name, new Skin(texture, signature)));
        }

        SkinsMenu menu = menuManager.createMenu(api, items, page);
        menuManager.openMenu(player, menu);
    }

}
