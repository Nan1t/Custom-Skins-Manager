package ru.csm.api.player;

public class HashedSkin extends Skin {

    private String name;
    private long expiryTime;

    public HashedSkin(String name, long expiryTime){
        this.name = name;
        this.expiryTime = expiryTime;
    }

    public String getName(){
        return name;
    }
    
    public long getExpiryTime(){
        return expiryTime;
    }

}