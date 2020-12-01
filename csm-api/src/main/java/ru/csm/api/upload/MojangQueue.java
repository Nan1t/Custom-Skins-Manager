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
import ru.csm.api.event.EventSkinChange;
import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.logging.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public final class MojangQueue extends ImageQueue {

    private static final String AUTH_URL = "https://authserver.mojang.com";
    private static final String SKIN_URL = "https://api.mojang.com/user/profile/%s/skin";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final SkinsAPI<?> api;
    private final List<Profile> profiles;
    private Iterator<Profile> profileIterator;

    public MojangQueue(SkinsAPI<?> api, List<Profile> profiles, int period) {
        super(period);
        this.api = api;
        this.profiles = profiles;
        this.profileIterator = profiles.iterator();

        for (Profile profile : profiles){
            authenticate(profile);
        }
    }

    private Profile getAvailableProfile(){
        if (!profileIterator.hasNext()) profileIterator = profiles.iterator();
        return profileIterator.next();
    }

    @Override
    public void run() {
        pop().ifPresent((request)->{
            if (request.getPlayer().isOnline()){
                Profile profile = getAvailableProfile();

                Logger.info("Using profile %s", profile.toString());

                if (!validate(profile)){
                    refresh(profile);
                }

                if(changeSkin(profile, request)){
                    Skin skin = MojangAPI.getPremiumSkin(profile.getUUID());

                    if(skin != null){
                        fireChangeEvent(request.getPlayer(), skin, (event)->{
                            if (!event.isCancelled()){
                                SkinHash.add(request.getUrl(), event.getNewSkin());
                                api.setCustomSkin(request.getPlayer(), event.getNewSkin());
                            }
                        });
                        return;
                    }
                    Logger.severe("Cannot get skin of premium profile %s", profile);
                    return;
                }

                request.getPlayer().sendMessage(api.getLang().of("skin.image.error"));
            }
        });
    }

    private boolean changeSkin(Profile profile, Request request) {
        try {
            HttpPost post = new HttpPost(String.format(SKIN_URL, profile.getUUID().toString().replace("-", "")));
            post.addHeader("Authorization", "Bearer " + profile.getAccessToken());
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity entity = new HttpEntity();
            entity.addParam("url", request.getUrl());
            entity.addParam("model", request.getModel().getName());

            post.setEntity(entity);

            HttpResponse response = post.execute();
            int code = response.getCode();

            if (code != 204) {
                Logger.severe("Error while change license skin. Response code: %s", code);
                return false;
            }

            return true;
        } catch (IOException e) {
            Logger.severe("Error while change license skin: %s", e.getMessage());
            return false;
        }
    }

    private boolean validate(Profile profile){
        try {
            HttpPost request = new HttpPost(AUTH_URL + "/validate");
            request.addHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            json.addProperty("accessToken", profile.getAccessToken());
            request.setEntity(new HttpEntity(json.toString()));

            HttpResponse response = request.execute();

            return response.getCode() == 204;
        } catch (Exception e){
            return false;
        }
    }

    private void refresh(Profile profile){
        try{
            HttpPost request = new HttpPost(AUTH_URL + "/refresh");
            request.addHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            json.addProperty("accessToken", profile.getAccessToken());
            json.addProperty("clientToken", profile.getClientToken());

            request.setEntity(new HttpEntity(json.toString()));

            HttpResponse response = request.execute();
            JsonObject responseJson = JSON_PARSER.parse(response.getResponse()).getAsJsonObject();
            String accessToken = responseJson.get("accessToken").getAsString();
            String clientToken = responseJson.get("clientToken").getAsString();

            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
            Logger.info("Successfully refreshing session for Mojang account %s", profile.getUsername());
        } catch (IOException e){
            Logger.severe("Error while refreshing session for Mojang account %s: %s", profile.getUsername(), e.getMessage());
        }
    }

    private void authenticate(Profile profile){
        try{
            HttpPost request = new HttpPost(AUTH_URL + "/authenticate");
            request.addHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            JsonObject agent = new JsonObject();
            agent.addProperty("name", "Minecraft");
            agent.addProperty("version", 1);

            json.add("agent", agent);
            json.addProperty("username", profile.getUsername());
            json.addProperty("password", profile.getPassword());

            request.setEntity(new HttpEntity(json.toString()));

            HttpResponse response = request.execute();
            JsonObject responseJson = JSON_PARSER.parse(response.getResponse()).getAsJsonObject();

            String accessToken = responseJson.get("accessToken").getAsString();
            String clientToken = responseJson.get("clientToken").getAsString();
            String uuid = responseJson.get("selectedProfile").getAsJsonObject().get("id").getAsString();

            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
            profile.setUUID(uuid);

            Logger.info("Successfully authenticate account %s", profile.getUsername());
        } catch (Exception e){
            Logger.severe("Error while authenticate profile %s: %s", profile.getUsername(), e.getMessage());
        }
    }
}
