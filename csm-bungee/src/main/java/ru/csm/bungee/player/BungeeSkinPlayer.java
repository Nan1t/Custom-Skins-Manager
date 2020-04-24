package ru.csm.bungee.player;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import ru.csm.api.network.Channels;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bungee.Skins;
import ru.csm.bungee.network.JsonMessage;
import ru.csm.bungee.network.PluginMessageService;

import java.lang.reflect.Field;
import java.util.UUID;

public class BungeeSkinPlayer implements SkinPlayer<ProxiedPlayer> {

    private final ProxiedPlayer player;
    private Skin defaultSkin;
    private Skin customSkin;

    public BungeeSkinPlayer(ProxiedPlayer player){
        this.player = player;
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return player;
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    @Override
    public Skin getCustomSkin() {
        return customSkin;
    }

    @Override
    public void setDefaultSkin(Skin skin) {
        defaultSkin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        customSkin = skin;
    }

    @Override
    public void applySkin() {
        Skin skin = defaultSkin;

        if(hasCustomSkin()){
            skin = customSkin;
        }

        try{
            InitialHandler handler = (InitialHandler) player.getPendingConnection();
            LoginResult.Property texture = new LoginResult.Property("textures", skin.getValue(), skin.getSignature());
            LoginResult profile = new LoginResult(player.getUniqueId().toString(), "textures", new LoginResult.Property[] { texture });

            profile.getProperties()[0].setName("textures");
            profile.getProperties()[0].setName("textures");
            profile.getProperties()[0].setValue(skin.getValue());
            profile.getProperties()[0].setSignature(skin.getSignature());

            Field field = InitialHandler.class.getDeclaredField("loginProfile");
            field.setAccessible(true);
            field.set(handler, profile);
        } catch (Exception e){
            System.out.println("Error while apply skin: " + e.getMessage());
        }
    }

    @Override
    public void refreshSkin() {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getUniqueId().toString());
        JsonMessage message = new JsonMessage(Channels.SKINS_REFRESH, player, json);
        PluginMessageService.sendMessage(message);
    }

    @Override
    public void resetSkin() {
        customSkin = null;
    }

    @Override
    public void sendMessage(String... message) {
        for (String line : message){
            player.sendMessage(TextComponent.fromLegacyText(line));
        }
    }

    @Override
    public boolean isOnline() {
        return player.isConnected();
    }

    @Override
    public boolean hasCustomSkin() {
        return customSkin != null && customSkin.getValue() != null && customSkin.getSignature() != null;
    }

    @Override
    public boolean hasDefaultSkin() {
        return defaultSkin != null && defaultSkin.getValue() != null && defaultSkin.getSignature() != null;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
