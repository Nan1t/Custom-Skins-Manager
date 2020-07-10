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

package ru.csm.api.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.logging.Logger;

import java.io.IOException;
import java.util.Optional;

public final class MineskinQueue extends ImageQueue {

    private static final String API_URL = "https://api.mineskin.org/generate/url";
    private static final JsonParser JSON_PARSER = new JsonParser();
    private final SkinsAPI api;

    private long nextRequest;

    public MineskinQueue(SkinsAPI api) {
        this.api = api;
    }

    @Override
    public int getWaitSeconds(){
        return ((int) Math.abs(nextRequest - System.currentTimeMillis()) / 1000) + super.getWaitSeconds();
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() >= nextRequest){

            nextRequest = System.currentTimeMillis();

            pop().ifPresent((request)->{
                if (request.getPlayer().isOnline()){
                    Optional<Skin> hashed = SkinHash.get(request.getUrl());

                    if (hashed.isPresent()){
                        api.setCustomSkin(request.getPlayer(), hashed.get());
                        return;
                    }

                    try{
                        if (!executeRequest(request)) {
                            request.getPlayer().sendMessage(api.getLang().of("skin.image.error"));
                        }
                    } catch (IOException e){
                        Logger.severe("Cannot execute request to mineskin.org: %s", e.getMessage());
                    }
                }
            });
        }
    }

    private boolean executeRequest(Request request) throws IOException {
        HttpPost post = new HttpPost(API_URL);
        HttpEntity entity = new HttpEntity();

        entity.addParam("url", request.getUrl());
        entity.addParam("visibility", "1");
        entity.addParam("model", request.getModel().getName());

        post.setEntity(entity);

        HttpResponse response = post.execute();

        if(response.getResponse() != null){
            Skin skin = parseResponse(response);

            if(skin != null){
                SkinHash.add(request.getUrl(), skin);
                api.setCustomSkin(request.getPlayer(), skin);
                return true;
            }
        }

        return false;
    }

    private Skin parseResponse(HttpResponse response) {
        String responseJson = response.getResponse();

        if(responseJson != null && !responseJson.isEmpty()){
            JsonObject json = JSON_PARSER.parse(responseJson).getAsJsonObject();

            nextRequest = System.currentTimeMillis() + json.get("nextRequest").getAsInt() * 1000;

            if(json.has("data")){
                JsonObject texture = json.get("data").getAsJsonObject().get("texture").getAsJsonObject();
                String value = texture.get("value").getAsString();
                String signature = texture.get("signature").getAsString();

                return new Skin(value, signature);
            }
        }

        return null;
    }

}
