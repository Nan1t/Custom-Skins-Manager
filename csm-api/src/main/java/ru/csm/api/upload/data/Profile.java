package ru.csm.api.upload.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;
import ru.csm.api.utils.UuidUtil;

import java.util.UUID;

public class Profile {

    private String username;
    private String password;
    private String accessToken;
    private String clientToken;
    private UUID uuid;

    public Profile(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getClientToken(){
        return clientToken;
    }

    public UUID getUUID(){
        return uuid;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void setClientToken(String clientToken){
        this.clientToken = clientToken;
    }

    public void setUUID(String uuid){
        this.uuid = UuidUtil.getUUID(uuid);
    }

    public void setUUID(UUID uuid){
        this.uuid = uuid;
    }

    public static class Serializer implements TypeSerializer<Profile> {

        @Override
        public Profile deserialize(TypeToken<?> type, ConfigurationNode node) {
            String login = node.getNode("login").getString();
            String password = node.getNode("password").getString();
            return new Profile(login, password);
        }

        @Override
        public void serialize(TypeToken<?> type, Profile profile, ConfigurationNode node) {

        }
    }
}
