package ru.csm.api.services;

import ru.csm.api.player.*;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.api.upload.*;
import ru.csm.api.utils.UuidUtil;

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

    /**
     * Get player head with her current skin (custom or default)
     * @param player Object of SkinPlayer
     * @return Head object if player exist or null otherwise
     */
    Head getPlayerHead(SkinPlayer<Player> player);

    /**
     * Get player head by name with her current skin (custom or default)
     * @param playerName Name of the player
     * @return Head object if player exist or null otherwise
     */
    default Head getPlayerHead(String playerName){
        SkinPlayer<Player> player = getPlayer(playerName);

        if(player != null){
            Skin skin = player.getCustomSkin();

            if(skin == null){
                skin = player.getDefaultSkin();
            }

            return new Head(player.getUUID(), player.getName(), skin);
        }

        Row row = getDatabase().getRow(Tables.SKINS, "name", playerName);

        if(row != null){
            UUID uuid = UuidUtil.getUUID(row.getField("uuid").toString());
            Skin skin = new Skin();

            if(row.getField("custom_value") != null){
                skin.setValue(row.getField("custom_value").toString());
                skin.setSignature(row.getField("custom_signature").toString());
            } else{
                skin.setValue(row.getField("default_value").toString());
                skin.setSignature(row.getField("default_signature").toString());
            }

            return new Head(uuid, playerName, skin);
        }

        return null;
    }

    /**
     * Get pages count of the skins menu
     * @return Count of the menu pages
     */
    default int getMenuSize(){
        Row[] rows = getDatabase().getRowsWithRequest("SELECT * FROM " + Tables.SKINS + " WHERE custom_value IS NOT NULL");
        return rows.length;
    }

    /**
     * Get list of heads with player skins for menu
     * @param page Number of page in menu. Current menu size you can get use getMenuSize()
     * @return List of players heads or empty list if required page not exist. Maximum list size - 44
     */
    default Map<UUID, Head> getHeads(int menuSize, int page){
        int count = 21;

        int remain = menuSize%count;
        int pages = menuSize/count;
        int startPoint = (page-1)*count;

        if(menuSize <= count){
            remain = 0;
            pages = 1;
            startPoint = 0;
        }

        if(remain > 0){
            pages += 1;
            remain = 0;
        }

        if(page > pages && remain != 0){
            return null;
        }

        Row[] rows = getDatabase().getRowsWithRequest("SELECT * FROM " + Tables.SKINS + " WHERE custom_value IS NOT NULL ORDER BY -id LIMIT " + startPoint + "," + count);
        Map<UUID, Head> heads = new HashMap<>();

        for(Row row : rows){
            UUID uuid = UUID.fromString(row.getField("uuid").toString());
            String name = row.getField("name").toString();
            String value = row.getField("custom_value").toString();
            String signature = row.getField("custom_signature").toString();
            Skin skin = new Skin(value, signature);
            heads.put(uuid, new Head(uuid, name, skin));
        }

        return heads;
    }
    
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

            if(player.hasCustomSkin()){
                row.addField("custom_value", player.getCustomSkin().getValue());
                row.addField("custom_signature", player.getCustomSkin().getSignature());
            }

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