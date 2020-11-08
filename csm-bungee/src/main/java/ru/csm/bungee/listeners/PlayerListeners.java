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

package ru.csm.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.util.BungeeTasks;

public class PlayerListeners implements Listener {

    private final SkinsAPI<ProxiedPlayer> api;

    public PlayerListeners(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        BungeeTasks.runAsync(()->{
            SkinPlayer player = api.loadPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());

            if (player == null){
                player = api.buildPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
                api.createNewPlayer(player);
            }

            api.addPlayer(player);

            player.applySkin();
            player.refreshSkin();
        });
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }
}
