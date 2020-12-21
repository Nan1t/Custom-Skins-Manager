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

package ru.csm.bukkit.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.nms.SkinHandlers;

public class SkinPlaceholders extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "csm";
    }

    @Override
    public String getAuthor() {
        return "Nanit";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (p instanceof Player){
            Player player = (Player) p;

            switch (params){
                case "skin_url":{
                    Skin skin = SkinHandlers.getHandler().getSkin(player);
                    return skin == null ? null : skin.getURL();
                }
                case "skin_texture":{
                    Skin skin = SkinHandlers.getHandler().getSkin(player);
                    return skin == null ? null : skin.getValue();
                }
                case "skin_signature":{
                    Skin skin = SkinHandlers.getHandler().getSkin(player);
                    return skin == null ? null : skin.getSignature();
                }
            }
        }

        return null;
    }

}
