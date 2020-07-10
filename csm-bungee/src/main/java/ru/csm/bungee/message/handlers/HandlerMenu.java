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

package ru.csm.bungee.message.handlers;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.services.SkinsAPI;

public class HandlerMenu implements MessageHandler {

    private final SkinsAPI<ProxiedPlayer> api;

    public HandlerMenu(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(JsonObject json) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(json.get("player").getAsString());
        int page = json.get("page").getAsInt();
        api.openSkinsMenu(player, page);
    }

}
