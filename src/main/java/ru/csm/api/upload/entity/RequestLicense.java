package ru.csm.api.upload.entity;

import ru.csm.api.player.SkinPlayer;

public class RequestLicense extends SkinRequest {

    private String targetName;

    public RequestLicense(SkinPlayer sender, String targetName) {
        super(sender);
        this.targetName = targetName;
    }

    public String getTargetName(){
        return targetName;
    }

}
