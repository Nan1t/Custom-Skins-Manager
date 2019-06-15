package ru.csm.api.upload.entity;

import ru.csm.api.player.SkinPlayer;

public abstract class SkinRequest {

    private SkinPlayer sender;

    public SkinRequest(SkinPlayer sender){
        this.sender = sender;
    }

    public SkinPlayer getSender(){
        return sender;
    }
}
