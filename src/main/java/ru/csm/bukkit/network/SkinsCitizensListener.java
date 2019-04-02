package ru.csm.bukkit.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.player.CitizensSkinPlayer;

import java.io.IOException;
import java.util.UUID;

public class SkinsCitizensListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if(!channel.equals(Channels.SKINS_CITIZENS)){
            return;
        }

        try {
            String str = IOUtils.toString(bytes, "UTF-8");
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();
            UUID senderUUID = UUID.fromString(json.get("sender").getAsString());

            if(!player.getUniqueId().equals(senderUUID)){
                return;
            }

            Player sender = Bukkit.getPlayer(senderUUID);

            UUID npcUUID = UUID.fromString(json.get("npc").getAsString());
            String value = json.get("value").getAsString();
            String signature = json.get("signature").getAsString();
            Skin skin = new Skin(value, signature);

            NPC npc = CitizensAPI.getNPCRegistry().getByUniqueIdGlobal(npcUUID);

            if(npc != null){
                CitizensSkinPlayer skinPlayer = new CitizensSkinPlayer(sender, npc);
                skinPlayer.setCustomSkin(skin);
                skinPlayer.applySkin();
                skinPlayer.refreshSkin();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
