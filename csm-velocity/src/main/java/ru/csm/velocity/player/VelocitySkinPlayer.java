package ru.csm.velocity.player;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import net.kyori.text.TextComponent;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

import java.util.UUID;

public class VelocitySkinPlayer implements SkinPlayer {

    private final ProxyServer server;
    private final UUID uuid;
    private final String name;
    private final MessageSender<Player> messageSender;

    private Player player;
    private Skin defaultSkin;
    private Skin customSkin;

    public VelocitySkinPlayer(UUID uuid, String name, MessageSender<Player> messageSender, ProxyServer server){
        this.uuid = uuid;
        this.name = name;
        this.messageSender = messageSender;
        this.server = server;
    }

    private Player getPlayer(){
        if (player == null){
            player = server.getPlayer(uuid).orElse(null);
            return player;
        }
        return player;
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
        Player player = getPlayer();

        if(player != null){
            Skin skin = hasCustomSkin() ? customSkin : defaultSkin;
            GameProfile.Property property = new GameProfile.Property("textures", skin.getValue(), skin.getSignature());
            player.getGameProfile().addProperty(property);
        }
    }

    @Override
    public void refreshSkin() {
        Player player = getPlayer();

        if (player != null){
            Skin skin = hasCustomSkin() ? customSkin : defaultSkin;
            JsonObject message = new JsonObject();

            message.addProperty("player", getPlayer().getUsername());
            message.addProperty("skin_value", skin.getValue());
            message.addProperty("skin_signature", skin.getSignature());

            messageSender.sendMessage(player, Channels.SKINS, message);
        }
    }

    @Override
    public void resetSkin() {
        customSkin = null;
    }

    @Override
    public void sendMessage(String... message) {
        Player player = getPlayer();
        for (String line : message){
            player.sendMessage(TextComponent.of(line));
        }
    }

    @Override
    public boolean isOnline() {
        return getPlayer().isActive();
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
