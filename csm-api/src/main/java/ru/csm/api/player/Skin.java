/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.player;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ninja.leaping.modded.configurate.ConfigurationNode;
import ninja.leaping.modded.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.modded.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Base64;

public class Skin {

    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private String value;
    private String signature;

    public Skin(){}

    public Skin(Skin skin){
        this.value = skin.getValue();
        this.signature = skin.getSignature();
    }

    public Skin(String value, String signature){
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public SkinModel getModel(){
        return parseModel();
    }

    /**
     * @return Decoded skin url if it exists, or null otherwise
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
        JsonObject json = JSON_PARSER.parse(decoded).getAsJsonObject();
        return json.get("profileId").getAsString();
    }

    /**
     * @return Name of real skin owner
     * */
    public String getOwnerName(){
        String decoded = new String(DECODER.decode(value));
        JsonObject json = JSON_PARSER.parse(decoded).getAsJsonObject();
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
        JsonObject json = JSON_PARSER.parse(decoded).getAsJsonObject();
        return json.get("textures").getAsJsonObject();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Skin){
            Skin skin = (Skin) obj;
            if (skin.getValue() == null || skin.getSignature() == null) return false;
            return skin.getValue().equals(this.value) && skin.getSignature().equals(this.signature);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Skin{texture:%s, signature: %s}", value, signature);
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
