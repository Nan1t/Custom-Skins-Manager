package ru.csm.api.player;

import java.util.List;
import java.util.UUID;

public class Head {

    private final UUID ownerUuid;
    private final String ownerName;
    private final Skin skin;
    private List<String> lore;
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
