package ru.csm.api.upload;

import ru.csm.api.player.HashedSkin;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Language;
import ru.csm.api.upload.entity.RequestLicense;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class QueueLicense extends QueueService {

    private SkinsAPI api;
    private Language lang;
    private Timer timer;

    public QueueLicense(SkinsAPI api, Language lang, long period){
        super(period);
        this.api = api;
        this.lang = lang;
        timer = new Timer();
    }

    @Override
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RequestLicense request = (RequestLicense) getRequestQueue().poll();

                if(request != null){
                    try{
                        Skin hashedSkin = api.getHashedSkin(request.getTargetName());

                        if(hashedSkin != null){
                            setSkin(request.getSender(), request.getTargetName(), hashedSkin);
                            return;
                        }

                        if(request.getSender().isOnline()){
                            executeRequest(request);
                        }
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                        request.getSender().sendMessage(lang.of("skin.name.error"));
                    }
                }
            }
        }, 1000, getRequestPeriod());
    }

    private void executeRequest(RequestLicense request){
        String targetName = request.getTargetName();
        UUID targetUUID = MojangAPI.getUUID(targetName);

        if(targetUUID != null){
            Skin skin = MojangAPI.getPremiumSkin(targetUUID);

            if(skin != null){
                HashedSkin hashedSkin = new HashedSkin(targetName, System.currentTimeMillis() + 60000);
                hashedSkin.setValue(skin.getValue());
                hashedSkin.setSignature(skin.getSignature());

                api.hashSkin(targetName, hashedSkin);
                setSkin(request.getSender(), targetName, skin);
                return;
            }
        }

        request.getSender().sendMessage(lang.of("skin.name.error"));
    }

    private void setSkin(SkinPlayer player, String targetName, Skin skin){
        player.setCustomSkin(skin);
        player.applySkin();
        player.refreshSkin();
        api.savePlayer(player);
        player.sendMessage(String.format(lang.of("skin.name.success"), targetName));
    }
}
