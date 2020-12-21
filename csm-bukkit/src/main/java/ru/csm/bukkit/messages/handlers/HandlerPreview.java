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
        boolean openMenu = json.get("open_menu").getAsBoolean();
        String permission = null;
        Skin skin = Skin.of(json.get("skin_value").getAsString(), json.get("skin_signature").getAsString());

        if (json.has("permission") && !json.get("permission").isJsonNull()){
            permission = json.get("permission").getAsString();
        }

        api.showPreview(player, skin, openMenu, permission);
    }

}
