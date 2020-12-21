/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
