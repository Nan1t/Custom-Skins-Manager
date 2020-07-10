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

package ru.csm.bukkit.services;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Configuration;
import ru.csm.api.storage.Language;
import ru.csm.api.storage.database.Database;
import ru.csm.api.upload.ImageQueue;
import ru.csm.api.upload.NameQueue;
import ru.csm.bukkit.npc.NPC;
import ru.csm.bukkit.npc.Npcs;

import java.util.UUID;

public class ProxySkinsAPI implements SkinsAPI<Player> {

    private final Language lang;
    private final MessageSender<Player> messageSender;

    public ProxySkinsAPI(Language lang, MessageSender<Player> messageSender){
        this.lang = lang;
        this.messageSender = messageSender;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public Language getLang() {
        return lang;
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    @Override
    public NameQueue getNameQueue() {
        return null;
    }

    @Override
    public ImageQueue getImageQueue() {
        return null;
    }

    @Override
    public boolean isBlackList(String nickname, SkinPlayer player) {
        return false;
    }

    @Override
    public boolean isWhitelist(String nickname, SkinPlayer player) {
        return false;
    }

    @Override
    public boolean isEnabledSkinRestoring() {
        return false;
    }

    @Override
    public boolean isUpdateDefaultSkin() {
        return false;
    }

    @Override
    public Skin getDefaultSkin() {
        return null;
    }

    @Override
    public SkinPlayer getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public SkinPlayer getPlayer(String name) {
        return null;
    }

    @Override
    public Head getPlayerHead(String name) {
        return null;
    }

    @Override
    public void showPreview(Player player, Skin skin, boolean openMenu, String permission) {
        NpcManager.removeNpc(player);

        NPC npc = Npcs.create();

        if (npc != null){
            Location loc = player.getLocation().clone();
            Vector modify = player.getLocation().getDirection().normalize().multiply(2);

            loc.add(modify);
            loc.setY(player.getLocation().getY());
            loc.setPitch(0);
            loc.setYaw(player.getLocation().getYaw()+180);

            npc.setLocation(loc);
            npc.setName("");
            npc.setDisplayName(lang.ofList("npc.name"));
            npc.setSkin(skin);
            npc.setOpenMenu(openMenu);
            npc.setPermission(permission);
            npc.spawn(player);
        }
    }

    @Override
    public void setCustomSkin(SkinPlayer player, Skin skin) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());
        if (bukkitPlayer != null) setCustomSkin(bukkitPlayer, skin);
    }

    @Override
    public void setCustomSkin(Player player, Skin skin) {
        JsonObject message = new JsonObject();

        message.addProperty("action", "set");
        message.addProperty("player", player.getName());
        message.addProperty("skin_value", skin.getValue());
        message.addProperty("skin_signature", skin.getSignature());

        messageSender.sendMessage(player.getPlayer(), Channels.SKINS, message);
    }

    @Override
    public void setSkinFromImage(SkinPlayer player, String link, SkinModel model) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());

        if (bukkitPlayer != null){
            JsonObject message = new JsonObject();

            message.addProperty("action", "image");
            message.addProperty("player", player.getName());
            message.addProperty("url", link);
            message.addProperty("model", model.toString());

            messageSender.sendMessage(bukkitPlayer, Channels.SKINS, message);
        }
    }

    @Override
    public void setSkinFromName(SkinPlayer player, String name) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());

        if (bukkitPlayer != null){
            JsonObject message = new JsonObject();

            message.addProperty("action", "name");
            message.addProperty("player", player.getName());
            message.addProperty("name", name);

            messageSender.sendMessage(bukkitPlayer, Channels.SKINS, message);
        }
    }

    @Override
    public void resetSkin(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        if (player != null){
            JsonObject message = new JsonObject();

            message.addProperty("action", "reset");
            message.addProperty("player", player.getName());

            messageSender.sendMessage(player.getPlayer(), Channels.SKINS, message);
        }
    }

    @Override
    public void resetSkin(SkinPlayer player) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getUUID());

        if (bukkitPlayer != null){
            JsonObject message = new JsonObject();

            message.addProperty("action", "reset");
            message.addProperty("player", player.getName());

            messageSender.sendMessage(bukkitPlayer, Channels.SKINS, message);
        }
    }

    @Override
    public void openSkinsMenu(Player player, int page) {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getName());
        json.addProperty("page", page);
        messageSender.sendMessage(player, Channels.MENU, json);
    }

    @Override
    public SkinPlayer buildPlayer(UUID uuid, String name) {
        return null;
    }

    @Override
    public void addPlayer(SkinPlayer player) {

    }

    @Override
    public void removePlayer(UUID uuid) {

    }
}
