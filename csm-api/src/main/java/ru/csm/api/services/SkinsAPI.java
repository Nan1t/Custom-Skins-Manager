/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.services;

import napi.configurate.Language;
import ru.csm.api.player.*;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.Database;
import ru.csm.api.storage.Row;

import java.util.*;

public interface SkinsAPI<Player> {

    Language getLang();
    
    Database getDatabase();

    /**
     * Check is premium nickname exist in blacklist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    boolean isBlackList(String nickname, SkinPlayer player);

    /**
     * Check is premium nickname exist in whitelist
     * @param nickname Required premium nickname
     * @return true if exists and false otherwise
     */
    boolean isWhitelist(String nickname, SkinPlayer player);

    boolean isEnabledSkinRestoring();

    boolean isUpdateDefaultSkin();

    /**
     * @return Get random default skin, defined in config
     *
     */
    Skin getDefaultSkin();
    
    /**
     * Get skinned player by UUID
     * @param uuid - UUID of the player
     */
    SkinPlayer getPlayer(UUID uuid);

    /**
     * Get skinned player by name
     * @param name - Name of the player
     */
    SkinPlayer getPlayer(String name);

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
    void setCustomSkin(SkinPlayer player, Skin skin);

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
    void setSkinFromImage(SkinPlayer player, String link, SkinModel model);

    /**
     * Set skin from premium account
     * @param player SkinPlayer object
     * @param name Name of the target premium account
     */
    void setSkinFromName(SkinPlayer player, String name);

    /**
     * Reset player skin to default
     * @param player SkinPlayer object
     */
    void resetSkin(SkinPlayer player);

    default void resetSkin(UUID uuid){
        SkinPlayer p = getPlayer(uuid);
        if (p != null) resetSkin(p);
    }

    default void openSkinsMenu(Player player){
        openSkinsMenu(player, 1);
    }

    void openSkinsMenu(Player player, int page);

    SkinPlayer buildPlayer(UUID uuid, String name);

    void addPlayer(SkinPlayer player);

    void removePlayer(UUID uuid);

    default void createNewPlayer(SkinPlayer player){
        Skin defaultSkin = getDefaultSkin();

        if (isEnabledSkinRestoring()){
            Optional<Skin> skin = getDefaultSkin(player.getName());
            if (skin.isPresent()) defaultSkin = skin.get();
        }

        player.setDefaultSkin(defaultSkin);
        savePlayer(player);
    }

    default Optional<Skin> getDefaultSkin(String username){
        Skin skin = null;
        UUID uuid = MojangAPI.getUUID(username);
        if (uuid != null) skin = MojangAPI.getPremiumSkin(uuid);
        return Optional.ofNullable(skin);
    }

    default SkinPlayer loadPlayer(UUID uuid, String name){
        Row row = getDatabase().getRow(Tables.SKINS, "uuid", uuid.toString());

        if (row != null){
            SkinPlayer player = buildPlayer(uuid, name);
            Skin defaultSkin = Skin.of(row.getField("default_value").toString(),
                    row.getField("default_signature").toString());
            Skin customSkin = null;
            boolean savePlayer = false;

            if (isUpdateDefaultSkin()){
                Optional<Skin> skinOpt = getDefaultSkin(player.getName());

                if (skinOpt.isPresent() && !skinOpt.get().equals(defaultSkin)){
                    defaultSkin = skinOpt.get();
                    savePlayer = true;
                }
            }

            if (row.hasField("custom_value") && row.hasField("custom_signature")){
                customSkin = Skin.of(row.getField("custom_value").toString(),
                        row.getField("custom_signature").toString());
            }

            player.setDefaultSkin(defaultSkin);
            player.setCustomSkin(customSkin);

            if (savePlayer) savePlayer(player);

            return player;
        }

        return null;
    }

    /**
     * Save the player data into current storage (local or remote database)
     * @param player - Object of a player
     * */
    void savePlayer(SkinPlayer player);

    default void savePlayerBlocking(SkinPlayer player){
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
    }
}