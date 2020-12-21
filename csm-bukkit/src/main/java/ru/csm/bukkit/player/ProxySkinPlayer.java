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

package ru.csm.bukkit.player;

import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

import java.util.UUID;

public class ProxySkinPlayer implements SkinPlayer {

    private Player player;
    private String name;
    private UUID uuid;

    public ProxySkinPlayer(Player player){
        this.player = player;
    }

    public ProxySkinPlayer(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public UUID getUUID() {
        return player == null ? uuid : player.getUniqueId();
    }

    @Override
    public String getName() {
        return player == null ? name :player.getName();
    }

    @Override
    public Skin getDefaultSkin() {
        return null;
    }

    @Override
    public Skin getCustomSkin() {
        return null;
    }

    @Override
    public void setDefaultSkin(Skin skin) {

    }

    @Override
    public void setCustomSkin(Skin skin) {

    }

    @Override
    public void applySkin() {

    }

    @Override
    public void refreshSkin() {

    }

    @Override
    public void resetSkin() {

    }

    @Override
    public void sendMessage(String... message) {
        if (player != null){
            player.sendMessage(message);
        }
    }

    @Override
    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    @Override
    public boolean hasCustomSkin() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player != null && player.hasPermission(permission);
    }
}
