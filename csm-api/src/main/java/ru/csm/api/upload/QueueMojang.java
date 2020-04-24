package ru.csm.api.upload;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;

import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.upload.entity.Profile;
import ru.csm.api.upload.entity.RequestImage;
import ru.csm.api.upload.entity.SkinRequest;

import java.io.IOException;
import java.util.*;

public class QueueMojang extends QueueService {

    private static final String AUTH_URL = "https://authserver.mojang.com";
    private static final String SKIN_URL = "https://api.mojang.com/user/profile/%s/skin";

    private SkinsAPI api;
    private Database db;
    private Configuration conf;
    private Language lang;
    private Timer timer;

    private List<Profile> profiles = new LinkedList<>();

    private int index;

    public QueueMojang(SkinsAPI api, Database database, Configuration conf, Language lang, long requestPeriod) {
        super(requestPeriod);

        this.api = api;
        this.db = database;
        this.conf = conf;
        this.lang = lang;
        this.timer = new Timer();

        fetchProfiles();

        for(Profile profile : profiles){
            authenticate(profile);
        }
    }

    @Override
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SkinRequest request = getRequestQueue().poll();

                if(request != null){
                    if(!request.getSender().isOnline()){
                        return;
                    }

                    RequestImage requestImage = (RequestImage) request;
                    Profile freeProfile = profiles.get(index);

                    System.out.println("Using profile " + freeProfile.getUsername() + " to change player skin");

                    if(changeSkin(freeProfile, requestImage)){
                        Skin skin = MojangAPI.getPremiumSkin(freeProfile.getUUID());

                        if(skin != null) {
                            request.getSender().setCustomSkin(skin);
                            request.getSender().applySkin();
                            request.getSender().refreshSkin();
                            request.getSender().sendMessage(lang.of("skin.image.success"));
                            api.savePlayer(request.getSender());
                        }

                        addIndex();
                        return;
                    }

                    request.getSender().sendMessage(lang.of("skin.image.error"));
                }
            }
        }, 1000, getRequestPeriod());
    }

    private void addIndex(){
        if(index >= (profiles.size()-1)){
            index = 0;
            return;
        }
        index++;
    }

    private boolean changeSkin(Profile profile, RequestImage request) {
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
                System.out.println("Error while change license skin. Response code: " + code);
                return false;
            }

            return true;
        } catch (IOException e) {
            System.out.println("Error while change license skin: " + e.getMessage());
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
            JsonObject responseJson = new JsonParser().parse(response.getResponse()).getAsJsonObject();
            String accessToken = responseJson.get("accessToken").getAsString();
            String clientToken = responseJson.get("clientToken").getAsString();

            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
            System.out.println("Successfully refreshing session for mojang account " + profile.getUsername());
        } catch (IOException e){
            System.out.println("Error while refreshing session for mojang account " + profile.getUsername() + ": " + e.getMessage());
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
            JsonObject responseJson = new JsonParser().parse(response.getResponse()).getAsJsonObject();

            String accessToken = responseJson.get("accessToken").getAsString();
            String clientToken = responseJson.get("clientToken").getAsString();
            String uuid = responseJson.get("selectedProfile").getAsJsonObject().get("id").getAsString();

            profile.setAccessToken(accessToken);
            profile.setClientToken(clientToken);
            profile.setUUID(uuid);

            System.out.println("Successfully authenticate account " + profile.getUsername());
        } catch (Exception e){
            System.out.println("Error while authenticate profile " + profile.getUsername() + ": " + e.getMessage());
        }
    }

    private void fetchProfiles(){
        ArrayList<LinkedHashMap> profiles = (ArrayList<LinkedHashMap>) conf.get().getNode("skins", "mojang", "accounts").getValue();

        for(LinkedHashMap map : profiles){
            Profile profile = new Profile(map.get("login").toString(), map.get("password").toString());
            this.profiles.add(profile);
        }
    }
}