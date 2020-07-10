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

package ru.csm.velocity.message.handlers;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.network.MessageHandler;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class HandlerSkin implements MessageHandler {

    private final SkinsAPI<Player> api;

    public HandlerSkin(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(JsonObject json) {
        SkinPlayer player = api.getPlayer(json.get("player").getAsString());

        if (player != null){
            String action = json.get("action").getAsString();

            switch (action){
                case "set":{
                    Skin skin = new Skin();
                    skin.setValue(json.get("skin_value").getAsString());
                    skin.setSignature(json.get("skin_signature").getAsString());
                    api.setCustomSkin(player, skin);
                    break;
                }
                case "reset":{
                    api.resetSkin(player);
                    break;
                }
                case "name":{
                    String name = json.get("name").getAsString();
                    api.setSkinFromName(player, name);
                    break;
                }
                case "image":{
                    String url = json.get("url").getAsString();
                    SkinModel model = SkinModel.valueOf(json.get("model").getAsString());
                    api.setSkinFromImage(player, url, model);
                    break;
                }
            }
        }
    }
}
