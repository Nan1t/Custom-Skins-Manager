package ru.csm.api.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.utils.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public final class MojangQueue extends ImageQueue {

    private static final String AUTH_URL = "https://authserver.mojang.com";
    private static final String SKIN_URL = "https://api.mojang.com/user/profile/%s/skin";
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final SkinsAPI api;
    private final List<Profile> profiles;
    private Iterator<Profile> profileIterator;

    public MojangQueue(SkinsAPI api, List<Profile> profiles) {
        this.api = api;
        this.profiles = profiles;
        this.profileIterator = profiles.iterator();
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

                if(changeSkin(profile, request)){
                    Skin skin = MojangAPI.getPremiumSkin(profile.getUUID());

                    if(skin != null){
                        SkinHash.add(request.getUrl(), skin);
                        api.setCustomSkin(request.getPlayer(), skin);
                        request.getPlayer().sendMessage(api.getLang().of("skin.image.success"));
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
                Logger.info("Error while change license skin. Response code: %s", code);
                return false;
            }

            return true;
        } catch (IOException e) {
            Logger.info("Error while change license skin: %s", e.getMessage());
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
            e.printStackTrace();
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
            Logger.info("Error while refreshing session for Mojang account %s: %s", profile.getUsername(), e.getMessage());
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
            Logger.info("Error while authenticate profile %s: %s", profile.getUsername(), e.getMessage());
        }
    }
}
