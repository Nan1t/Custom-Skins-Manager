package ru.csm.bungee.player;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bungee.Skins;
import ru.csm.bungee.network.JsonMessage;

import java.util.UUID;

public class BungeCitizensPlayer implements SkinPlayer<ProxiedPlayer> {

    private ProxiedPlayer sender;
    private String npcUUID;
    private Skin skin;

    public BungeCitizensPlayer(ProxiedPlayer sender, String npcUUID){
        this.sender = sender;
        this.npcUUID = npcUUID;
    }

    public String getNpcUUID(){
        return npcUUID;
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return sender;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return "NPC";
    }

    @Override
    public Skin getDefaultSkin() {
        return skin;
    }

    @Override
    public Skin getCustomSkin() {
        return skin;
    }

    @Override
    public void setPlayer(ProxiedPlayer player) {

    }

    @Override
    public void setDefaultSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public void applySkin() {

    }

    @Override
    public void refreshSkin() {
        JsonObject json = new JsonObject();
        json.addProperty("sender", sender.getUniqueId().toString());
        json.addProperty("npc", npcUUID);
        json.addProperty("value", skin.getValue());
        json.addProperty("signature", skin.getSignature());

        JsonMessage message = new JsonMessage(Channels.SKINS_CITIZENS, sender, json);
        Skins.getPluginMessageService().sendMessage(message);
    }

    @Override
    public void resetSkin() {

    }

    @Override
    public void sendMessage(String... message) {
        if(sender.isConnected()){
            for(String str : message){
                sender.sendMessage(TextComponent.fromLegacyText(str));
            }
        }
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean hasCustomSkin() {
        return true;
    }

    @Override
    public boolean hasDefaultSkin() {
        return true;
    }
}
