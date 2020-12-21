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

package ru.csm.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.Collections;
import java.util.List;

public final class PlayerListeners {

    private final SkinsAPI<Player> api;

    public PlayerListeners(SkinsAPI<Player> api){
        this.api = api;
    }

    @Subscribe
    public void profileRequest(GameProfileRequestEvent event){
        SkinPlayer player = api.getPlayer(event.getUsername());

        if (player != null){
            event.setGameProfile(getProfile(event.getGameProfile(), player));
            player.applySkin();
            player.refreshSkin();
        } else {
            player = api.loadPlayer(event.getGameProfile().getId(), event.getUsername());

            if (player == null){
                player = api.buildPlayer(event.getGameProfile().getId(), event.getUsername());
                api.createNewPlayer(player);
            }

            api.addPlayer(player);

            player.applySkin();
            player.refreshSkin();

            event.setGameProfile(getProfile(event.getGameProfile(), player));
        }
    }

    private GameProfile getProfile(GameProfile def, SkinPlayer player){
        Skin skin = player.hasCustomSkin() ? player.getCustomSkin() : player.getDefaultSkin();
        List<GameProfile.Property> props = Collections.singletonList(new GameProfile.Property("textures", skin.getValue(), skin.getSignature()));
        return def.withProperties(props);
    }

    @Subscribe
    public void onLogout(DisconnectEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }

}
