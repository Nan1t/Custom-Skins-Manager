package ru.csm.api.services;

import com.google.gson.*;
import ru.csm.api.player.Skin;
import ru.csm.api.logging.Logger;
import ru.csm.api.utils.UuidUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

public final class MojangAPI {

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private MojangAPI(){}

    public static UUID getUUID(String name){
        try{
            URL url = new URL(String.format(UUID_URL, name));
            String jsonString = getLine(url.openStream());

            if(!jsonString.isEmpty()){
                JsonObject object = JSON_PARSER.parse(jsonString).getAsJsonObject();
                String uuid = object.get("id").getAsString();
                return UuidUtil.getUUID(uuid);
            }
        } catch (IOException e){
            Logger.warning("Cannot get uuid of player %s: %s", name, e.getMessage());
        }

        return null;
    }

    public static Skin getPremiumSkin(UUID uuid){
        try{
            URL url = new URL(String.format(SKIN_URL, clearUUID(uuid)));
            String jsonString = getLine(url.openStream());

            if(!jsonString.isEmpty()){
                JsonObject object = JSON_PARSER.parse(jsonString).getAsJsonObject();

                if(object.has("properties")){
                    JsonArray propArr = object.get("properties").getAsJsonArray();

                    if (propArr.size() > 0){
                        JsonObject properties = object.get("properties").getAsJsonArray().get(0).getAsJsonObject();
                        String value = properties.get("value").getAsString();
                        String signature = properties.get("signature").getAsString();
                        String skinUrl = getsSkinURL(value);

                        if(skinUrl != null){
                            Skin skin = new Skin();
                            skin.setValue(value);
                            skin.setSignature(signature);
                            return skin;
                        }
                    }
                }
            }
        } catch (Exception e){
            Logger.warning("Cannot fetch premium skin of %s: %s", uuid, e.getMessage());
        }

        return null;
    }

    public static String getsSkinURL(String base64String) throws JsonParseException {
        String encoded = new String(BASE64_DECODER.decode(base64String));
        JsonObject json = JSON_PARSER.parse(encoded).getAsJsonObject();
        JsonObject textures = json.get("textures").getAsJsonObject();

        if(textures.entrySet().size() != 0){
            return textures.get("SKIN").getAsJsonObject().get("url").getAsString();
        }

        return null;
    }

    private static String getLine(InputStream in){
        Scanner scanner = new Scanner(in);
        StringBuilder builder = new StringBuilder();
        while(scanner.hasNext()) builder.append(scanner.next());
        return builder.toString();
    }

    private static String clearUUID(UUID uuid){
        return uuid.toString().replace("-", "");
    }

}
