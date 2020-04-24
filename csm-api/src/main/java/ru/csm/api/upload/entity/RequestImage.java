package ru.csm.api.upload.entity;

import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;

public class RequestImage extends SkinRequest {

    private String url;
    private SkinModel model;

    public RequestImage(SkinPlayer sender, String imageURL, SkinModel model){
        super(sender);

        this.url = imageURL;
        this.model = model;
    }

    public String getUrl(){
        return url;
    }

    public SkinModel getModel(){
        return model;
    }

}
