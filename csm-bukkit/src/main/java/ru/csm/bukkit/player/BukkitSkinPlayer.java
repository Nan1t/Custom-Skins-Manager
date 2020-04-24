package ru.csm.bukkit.player;

import org.bukkit.entity.Player;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.handler.SkinHandler;
import ru.csm.bukkit.handler.SkinHandlers;

import java.util.UUID;

public class BukkitSkinPlayer implements SkinPlayer<Player> {

    private final Player player;
    private final SkinHandler handler;

    private Skin defaultSkin;
    private Skin customSkin;

    public BukkitSkinPlayer(Player player){
        this.player = player;
        this.handler = SkinHandlers.getHandler();
    }

    @Override
    public Player getPlayer() {
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
        this.defaultSkin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        this.customSkin = skin;
    }

    @Override
    public void applySkin() {
        handler.applySkin(player, hasCustomSkin() ? customSkin : defaultSkin);
    }

    @Override
    public void refreshSkin() {
        handler.updateSkin(player);
    }

    @Override
    public void resetSkin() {
        this.customSkin = null;
    }

    @Override
    public void sendMessage(String... message){
        player.sendMessage(message);
    }

    @Override
    public boolean isOnline(){
        return this.player.isOnline();
    }

    @Override
    public boolean hasCustomSkin() {
        return customSkin != null;
    }

    @Override
    public boolean hasDefaultSkin() {
        if(defaultSkin != null) return defaultSkin.getValue() != null && defaultSkin.getSignature() != null;
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
