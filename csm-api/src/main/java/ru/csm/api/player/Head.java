package ru.csm.api.player;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.UUID;

public class Head {

    @Expose(serialize = false)
    private UUID ownerUuid;
    @Expose
    private String ownerName;
    @Expose
    private Skin skin;
    @Expose(serialize = false)
    private List<String> lore;
    @Expose(serialize = false)
    private String permission;

    public Head(UUID uuid, String name, Skin skin){
        this.ownerUuid = uuid;
        this.ownerName = name;
        this.skin = skin;
    }

    public UUID getOwnerUuid(){
        return ownerUuid;
    }

    public String getOwnerName(){
        return ownerName;
    }

    public Skin getSkin(){
        return skin;
    }

    public void setLore(List<String> lore){
        this.lore = lore;
    }

    public List<String> getLore(){
        return lore;
    }

    public String getPermission(){
        return permission;
    }

    public void setPermission(String permission){
        this.permission = permission;
    }

}
