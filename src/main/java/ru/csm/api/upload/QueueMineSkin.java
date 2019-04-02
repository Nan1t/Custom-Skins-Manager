package ru.csm.api.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.csm.api.http.HttpPost;
import ru.csm.api.http.entity.HttpEntity;
import ru.csm.api.http.entity.HttpResponse;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.upload.entity.RequestImage;
import ru.csm.api.upload.entity.SkinRequest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class QueueMineSkin extends QueueService {

    private static final String API_URL = "https://api.mineskin.org/generate/url";

    private Timer timer;
    private Language lang;
    private SkinsAPI api;

    public QueueMineSkin(SkinsAPI api, Language lang, long period){
        super(period);
        this.api = api;
        this.timer = new Timer();
        this.lang = lang;
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

                    try {
                        if(executeRequest(request)){
                            request.getSender().sendMessage(lang.of("skin.image.success"));
                            api.savePlayer(request.getSender());
                            return;
                        }
                    } catch (IOException e){
                        System.out.println(e.getMessage());
                    }

                    request.getSender().sendMessage(lang.of("skin.image.error"));
                }
            }
        }, 1000, getRequestPeriod());
    }

    private boolean executeRequest(SkinRequest request) throws IOException {
        RequestImage requestImage = (RequestImage) request;
        HttpPost post = new HttpPost(API_URL);
        HttpEntity entity = new HttpEntity();

        entity.addParam("url", requestImage.getUrl());
        entity.addParam("visibility", "1");
        entity.addParam("model", requestImage.getModel().getName());

        post.setEntity(entity);

        HttpResponse response = post.execute();

        if(response.getResponse() != null){
            Skin skin = parseResponse(response);

            if(skin != null){
                request.getSender().setCustomSkin(skin);
                request.getSender().applySkin();
                request.getSender().refreshSkin();
                return true;
            }
        }

        return false;
    }

    private Skin parseResponse(HttpResponse response) {
        String responseJson = response.getResponse();

        if(responseJson != null && !responseJson.isEmpty()){
            JsonObject json = new JsonParser().parse(responseJson).getAsJsonObject();

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
