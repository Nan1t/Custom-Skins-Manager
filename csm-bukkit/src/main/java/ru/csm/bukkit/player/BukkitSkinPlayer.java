package ru.csm.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.handler.SkinHandler;
import ru.csm.bukkit.handler.SkinHandlers;

import java.util.UUID;

public class BukkitSkinPlayer implements SkinPlayer {

    private final UUID uuid;
    private final String name;
    private final SkinHandler handler;

    private Skin defaultSkin;
    private Skin customSkin;

    public BukkitSkinPlayer(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.handler = SkinHandlers.getHandler();
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(uuid);
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
        this.defaultSkin = skin;
    }

    @Override
    public void setCustomSkin(Skin skin) {
        this.customSkin = skin;
    }

    @Override
    public void applySkin() {
        handler.applySkin(getPlayer(), hasCustomSkin() ? customSkin : defaultSkin);
    }

    @Override
    public void refreshSkin() {
        handler.updateSkin(getPlayer());
    }

    @Override
    public void resetSkin() {
        this.customSkin = null;
    }

    @Override
    public void sendMessage(String... message){
        getPlayer().sendMessage(message);
    }

    @Override
    public boolean isOnline(){
        return getPlayer().isOnline();
    }

    @Override
    public boolean hasCustomSkin() {
        return customSkin != null;
    }

    @Override
    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }
}
