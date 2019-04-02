package ru.csm.bukkit.player;

import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;

import java.util.UUID;

public class TargetSkinPlayer implements SkinPlayer<Player> {
    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Skin getDefaultSkin() {
        return null;
    }

    @Override
    public Skin getCustomSkin() {
        return null;
    }

    @Override
    public void setPlayer(Player player) {

    }

    @Override
    public void setDefaultSkin(Skin skin) {

    }

    @Override
    public void setCustomSkin(Skin skin) {

    }

    @Override
    public void applySkin() {

    }

    @Override
    public void refreshSkin() {

    }

    @Override
    public void resetSkin() {

    }

    @Override
    public void sendMessage(String... message) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean hasCustomSkin() {
        return false;
    }

    @Override
    public boolean hasDefaultSkin() {
        return false;
    }
}
