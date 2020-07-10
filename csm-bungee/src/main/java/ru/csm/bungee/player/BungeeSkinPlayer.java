package ru.csm.bungee.player;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.logging.Logger;

import java.lang.reflect.Field;
import java.util.UUID;

public class BungeeSkinPlayer implements SkinPlayer {

    private final UUID uuid;
    private final String name;

    private Skin defaultSkin;
    private Skin customSkin;

    private final MessageSender<ProxiedPlayer> messageSender;

    private static Field loginProfileField;

    static {
        try{
            loginProfileField = InitialHandler.class.getDeclaredField("loginProfile");
            loginProfileField.setAccessible(true);
        } catch (ReflectiveOperationException e){
            Logger.severe("Cannot find loginProfile field in InitialHandler class");
        }
    }

    public BungeeSkinPlayer(UUID uuid, String name, MessageSender<ProxiedPlayer> messageSender){
        this.uuid = uuid;
        this.name = name;
        this.messageSender = messageSender;
    }

    private ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
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
        Skin skin = hasCustomSkin() ? customSkin : defaultSkin;

        try{
            InitialHandler handler = (InitialHandler) getPlayer().getPendingConnection();
            LoginResult.Property texture = new LoginResult.Property("textures", skin.getValue(), skin.getSignature());
            LoginResult profile = new LoginResult(uuid.toString(), "textures", new LoginResult.Property[] { texture });

            profile.getProperties()[0].setName("textures");
            profile.getProperties()[0].setValue(skin.getValue());
            profile.getProperties()[0].setSignature(skin.getSignature());

            loginProfileField.set(handler, profile);
        } catch (Exception e){
            Logger.severe("Error while apply skin: %s", e.getMessage());
        }
    }

    @Override
    public void refreshSkin() {
        Skin skin = hasCustomSkin() ? customSkin : defaultSkin;
        JsonObject message = new JsonObject();

        message.addProperty("player", name);
        message.addProperty("skin_value", skin.getValue());
        message.addProperty("skin_signature", skin.getSignature());

        messageSender.sendMessage(getPlayer(), Channels.SKINS, message);
    }

    @Override
    public void resetSkin() {
        customSkin = null;
    }

    @Override
    public void sendMessage(String... message) {
        ProxiedPlayer player = getPlayer();
        for (String line : message){
            player.sendMessage(TextComponent.fromLegacyText(line));
        }
    }

    @Override
    public boolean isOnline() {
        return getPlayer().isConnected();
    }

    @Override
    public boolean hasCustomSkin() {
        return customSkin != null && customSkin.getValue() != null && customSkin.getSignature() != null;
    }

    @Override
    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }
}
