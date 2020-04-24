package ru.csm.api.player;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Base64;

public class Skin {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @Expose
    private String value;
    @Expose
    private String signature;
    @Expose(serialize = false)
    private SkinModel model;

    public Skin(){}

    public Skin(String value, String signature){
        this.value = value;
        this.signature = signature;
        setModel(parseModel());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        setModel(parseModel());
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public SkinModel getModel(){
        return model;
    }

    public void setModel(SkinModel model){
        this.model = model;
    }

    /**
     * @return Decoded skin url string, or null if skin value empty
     * */
    public String getURL(){
        JsonObject textures = getTextures();

        if(textures.entrySet().size() != 0){
            return textures.get("SKIN").getAsJsonObject().get("url").getAsString();
        }

        return null;
    }

    /**
     * @return UUID string of real skin owner
     * */
    public String getOwnerUUID(){
        String decoded = new String(DECODER.decode(value));
        JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
        return json.get("profileId").getAsString();
    }

    /**
     * @return Name of real skin owner
     * */
    public String getOwnerName(){
        String decoded = new String(DECODER.decode(value));
        JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
        return json.get("profileName").getAsString();
    }

    private SkinModel parseModel(){
        JsonObject textures = getTextures();

        if(textures.entrySet().size() != 0){
            JsonObject skinObj = textures.get("SKIN").getAsJsonObject();
            if(skinObj.has("metadata")){
                JsonObject metadata = skinObj.get("metadata").getAsJsonObject();
                if(metadata.has("model")){
                    return SkinModel.ALEX;
                }
            }
        }

        return SkinModel.STEVE;
    }

    private JsonObject getTextures(){
        String decoded = new String(DECODER.decode(value));
        JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
        return json.get("textures").getAsJsonObject();
    }

    public static class Serializer implements TypeSerializer<Skin> {

        @Override
        public Skin deserialize(TypeToken<?> type, ConfigurationNode node) throws ObjectMappingException {
            String texture = node.getNode("texture").getString();
            String signature = node.getNode("signature").getString();
            return new Skin(texture, signature);
        }

        @Override
        public void serialize(TypeToken<?> type, Skin obj, ConfigurationNode value) throws ObjectMappingException {

        }
    }
}
