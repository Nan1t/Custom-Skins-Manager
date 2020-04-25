package ru.csm.api.player;

import java.util.UUID;

public interface SkinPlayer<T> {

    /**
     * Get native platform player object (Bungee or Spigot)
     * @return Player object
     * */
    T getPlayer();

    /**
     * Get player UUID
     * @return UUID object
     * */
    UUID getUUID();

    /**
     * Get player name
     * @return String name
     * */
    String getName();

    /**
     * Set default (instead lisense) skin for player. Real license skin will not change
     * @return Object of player default (license or custom default) skin.
     * */
    Skin getDefaultSkin();

    /**
     * Set default (instead license) skin for player. Real license skin will not change
     * @return Object of player custom skin, or null if player not have custom skin
     * */
    Skin getCustomSkin();

    /**
     * Set default (instead license) skin for player. Real license skin will not change
     * @param skin - Skin object with value and signature
     * */
    void setDefaultSkin(Skin skin);

    /**
     * Set custom skin for player
     * @param skin - Skin object with value and signature
     * */
    void setCustomSkin(Skin skin);

    /**
     * Apply skin data to player. Calling before refreshing
     * */
    void applySkin();

    /**
     * Realtime refreshing player skin
     * */
    void refreshSkin();

    /**
     * Return player default (license or custom default) skin
     * */
    void resetSkin();

    /**
    * Send message to player
    * @param message - String message
    * */
    void sendMessage(String... message);

    /**
     * @return true if player online
     * */
    boolean isOnline();

    /**
     * @return true if player have custom skin
     * */
    boolean hasCustomSkin();

    /**
     * @return true is player has specified permission
     */
    boolean hasPermission(String permission);
}
