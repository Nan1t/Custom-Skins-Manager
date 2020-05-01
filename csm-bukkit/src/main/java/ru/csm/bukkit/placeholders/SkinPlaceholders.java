package ru.csm.bukkit.placeholders;

import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.handler.SkinHandlers;

public class SkinPlaceholders extends PlaceholderHook {

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        switch (params){
            case "skin_url":{
                Skin skin = SkinHandlers.getHandler().getSkin(p);
                if (skin != null){
                    return skin.getURL();
                }
                return null;
            }
            case "skin_texture":{
                Skin skin = SkinHandlers.getHandler().getSkin(p);
                if (skin != null){
                    return skin.getValue();
                }
                return null;
            }
            case "skin_signature":{
                Skin skin = SkinHandlers.getHandler().getSkin(p);
                if (skin != null){
                    return skin.getSignature();
                }
                return null;
            }
        }
        return null;
    }

}
