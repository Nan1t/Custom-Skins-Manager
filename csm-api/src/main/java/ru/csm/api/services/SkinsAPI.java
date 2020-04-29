package ru.csm.api.services;

import ru.csm.api.player.*;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.api.upload.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface SkinsAPI<Player> {

    Configuration getConfiguration();

    Language getLang();
    
    Database getDatabase();
    
    NameQueue getNameQueue();

    ImageQueue getImageQueue();

    /**
     * Check is premium nickname exist in blacklist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    boolean isBlackList(String nickname, SkinPlayer<Player> player);

    /**
     * Check is premium nickname exist in whitelist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    boolean isWhitelist(String nickname, SkinPlayer<Player> player);

    /**
     * @return Get random default skin, defined in config
     *
     */
    Skin getDefaultSkin();
    
    /**
     * Get skinned player by UUID
     * @param uuid - UUID of the player
     */
    SkinPlayer<Player> getPlayer(UUID uuid);

    /**
     * Get skinned player by name
     * @param name - Name of the player
     */
    SkinPlayer<Player> getPlayer(String name);

    Head getPlayerHead(String name);

    default void showPreview(Player player, Skin skin){
        showPreview(player, skin, true, null);
    }

    default void showPreview(Player player, Skin skin, boolean openMenu){
        showPreview(player, skin, openMenu, null);
    }

    void showPreview(Player player, Skin skin, boolean openMenu, String permission);

    /**
     * Set custom skin for player
     * @param player SkinPlayer object
     * @param skin Skin object
     */
    void setCustomSkin(SkinPlayer<Player> player, Skin skin);

    /**
     * Set custom skin for player
     * @param player Native (Bukkit or BungeeCord) player object
     * @param skin Skin object
     */
    void setCustomSkin(Player player, Skin skin);

    /**
     * Set skin from image link
     * @param player SkinPlayer object
     * @param link Link to *.png image
     * @param model Model of the skin
     */
    void setSkinFromImage(SkinPlayer<Player> player, String link, SkinModel model);

    /**
     * Set skin from premium account
     * @param player SkinPlayer object
     * @param name Name of the target premium account
     */
    void setSkinFromName(SkinPlayer<Player> player, String name);

    /**
     * Reset player skin to default
     * @param player SkinPlayer object
     */
    void resetSkin(SkinPlayer<Player> player);

    default void resetSkin(UUID uuid){
        SkinPlayer<Player> p = getPlayer(uuid);
        if (p != null) resetSkin(p);
    }

    default void openSkinsMenu(Player player){
        openSkinsMenu(player, 1);
    }

    void openSkinsMenu(Player player, int page);
    
    SkinPlayer<Player> buildPlayer(Player player);

    void addPlayer(SkinPlayer<Player> player);

    void removePlayer(UUID uuid);

    default void createNewPlayer(SkinPlayer<Player> player){
        Skin defaultSkin = getDefaultSkin();
        UUID uuid =  MojangAPI.getUUID(player.getName());

        if (uuid != null){
            Skin skin = MojangAPI.getPremiumSkin(uuid);
            if (skin != null) defaultSkin = skin;
        }

        player.setDefaultSkin(defaultSkin);
        savePlayer(player);
    }

    default SkinPlayer<Player> loadPlayer(Player p, UUID uuid){
        Row row = getDatabase().getRow(Tables.SKINS, "uuid", uuid.toString());

        if (row != null){
            SkinPlayer<Player> player = buildPlayer(p);
            Skin defaultSkin = new Skin();
            Skin customSkin = null;

            defaultSkin.setValue(row.getField("default_value").toString());
            defaultSkin.setSignature(row.getField("default_signature").toString());

            if (row.hasField("custom_value") && row.hasField("custom_signature")){
                customSkin = new Skin();
                customSkin.setValue(row.getField("custom_value").toString());
                customSkin.setSignature(row.getField("custom_signature").toString());
            }

            player.setDefaultSkin(defaultSkin);
            player.setCustomSkin(customSkin);

            return player;
        }

        return null;
    }

    /**
     * Save the player data into current storage (local or remote database)
     * @param player - Object of a player
     * */
    default void savePlayer(SkinPlayer<Player> player){
        CompletableFuture.runAsync(()->{
            Row row = new Row();

            row.addField("name", player.getName());
            row.addField("default_value", player.getDefaultSkin().getValue());
            row.addField("default_signature", player.getDefaultSkin().getSignature());
            row.addField("custom_value", player.hasCustomSkin() ? player.getCustomSkin().getValue() : null);
            row.addField("custom_signature", player.hasCustomSkin() ? player.getCustomSkin().getSignature() : null);

            boolean exists = getDatabase().existsRow(Tables.SKINS, "uuid", player.getUUID().toString());

            if (exists){
                getDatabase().updateRow(Tables.SKINS, "uuid", player.getUUID().toString(), row);
            } else {
                row.addField("uuid", player.getUUID().toString());
                getDatabase().createRow(Tables.SKINS, row);
            }
        });
    }
}