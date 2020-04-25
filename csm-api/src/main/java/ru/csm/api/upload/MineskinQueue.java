package ru.csm.api.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.utils.Logger;

import java.io.IOException;
import java.util.Optional;

public final class MineskinQueue extends ImageQueue {

    private static final String API_URL = "https://api.mineskin.org/generate/url";
    private static final JsonParser JSON_PARSER = new JsonParser();
    private final SkinsAPI api;

    public MineskinQueue(SkinsAPI api) {
        this.api = api;
    }

    @Override
    public void run() {
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
