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

package ru.csm.velocity.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import napi.configurate.Language;
import net.kyori.text.TextComponent;
import ru.csm.api.event.EventSkinChanged;
import ru.csm.api.event.EventSkinReset;
import ru.csm.api.event.Events;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinHash;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.SkinsConfig;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.Database;
import ru.csm.api.storage.Row;
import ru.csm.api.upload.*;
import ru.csm.api.utils.Validator;
import ru.csm.velocity.player.VelocitySkinPlayer;
import ru.csm.velocity.util.VelocityTasks;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VelocitySkinsAPI implements SkinsAPI<Player> {

    private final SkinsConfig conf;
    private final Database database;
    private final Language lang;
    private final ProxyServer server;

    private final Skin[] defaultSkins;
    private final Map<UUID, SkinPlayer> playersByUUID = new TreeMap<>();
    private final Map<String, SkinPlayer> playersByName = new TreeMap<>();

    private Map<String, String> blacklist;
    private Map<String, String> whitelist;

    private NameQueue nameQueue;
    private ImageQueue imageQueue;

    private final MessageSender<Player> messageSender;

    public VelocitySkinsAPI(Database database, SkinsConfig conf, MessageSender<Player> messageSender, ProxyServer server) {
        this.database = database;
        this.conf = conf;
        this.lang = conf.getLanguage();
        this.messageSender = messageSender;
        this.server = server;
        this.defaultSkins = conf.getDefaultSkins().toArray(new Skin[0]);

        loadBlacklist();
        loadWhitelist();
        loadQueues();
    }

    @Override
    public SkinsConfig getConfig() {
        return conf;
    }

    @Override
    public Language getLang(){
        return lang;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    @Override
    public NameQueue getNameQueue() {
        return nameQueue;
    }

    @Override
    public ImageQueue getImageQueue() {
        return imageQueue;
    }

    @Override
    public boolean isBlackList(String nickname, SkinPlayer player){
        if (blacklist == null) return false;

        if (blacklist.containsKey(nickname.toLowerCase())){
            String perm = blacklist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    @Override
    public boolean isWhitelist(String nickname, SkinPlayer player){
        if (whitelist == null) return true;

        if (whitelist.containsKey(nickname.toLowerCase())){
            String perm = whitelist.get(nickname.toLowerCase());
            return perm == null || player.hasPermission(perm);
        }

        return false;
    }

    @Override
    public boolean isEnabledSkinRestoring() {
        return conf.isRestoreSkins();
    }

    @Override
    public boolean isUpdateDefaultSkin() {
        return conf.isUpdateDefaultSkin();
    }

    @Override
    public Skin getDefaultSkin(){
        return defaultSkins[ThreadLocalRandom.current().nextInt(defaultSkins.length)];
    }

    @Override
    public SkinPlayer getPlayer(UUID uuid){
        return playersByUUID.get(uuid);
    }

    @Override
    public SkinPlayer getPlayer(String name){
        return playersByName.get(name.toLowerCase());
    }

    @Override
    public Head getPlayerHead(String name) {
        Row row = database.getRow(Tables.SKINS, "name", name);

        if (row != null){
            String owner = row.getField("name").toString();
            Skin skin = new Skin();

            if (row.hasField("custom_value")){
                skin.setValue(row.getField("custom_value").toString());
            } else {
                skin.setValue(row.getField("default_value").toString());
            }

            return new Head(owner, skin.getURL());
        }

        return null;
    }

    @Override
    public void showPreview(Player player, Skin skin, boolean openMenu, String permission) {
        JsonObject message = new JsonObject();
        message.addProperty("player", player.getUsername());
        message.addProperty("skin_value", skin.getValue());
        message.addProperty("skin_signature", skin.getSignature());
        message.addProperty("open_menu", openMenu);
        message.addProperty("permission", permission);
        messageSender.sendMessage(player, Channels.PREVIEW, message);
    }

    @Override
    public void setCustomSkin(SkinPlayer player, Skin skin){
        player.setCustomSkin(skin);
        player.applySkin();
        player.refreshSkin();
        savePlayer(player);
        player.sendMessage(lang.of("skin.success"));
        Events.fireSkinChanged(new EventSkinChanged(player, skin));
    }

    @Override
    public void setCustomSkin(Player p, Skin skin) {
        SkinPlayer player = getPlayer(p.getUniqueId());
        if (player != null) setCustomSkin(player, skin);
    }

    @Override
    public void setSkinFromImage(SkinPlayer player, String link, SkinModel model) {
        if(!Validator.validateURL(link)){
            player.sendMessage(lang.of("skin.image.invalid"));
            return;
        }

        Optional<Skin> hashed = SkinHash.get(link);

        if (hashed.isPresent()){
            setCustomSkin(player, hashed.get());
            return;
        }

        imageQueue.push(player, link, model);
        long seconds = imageQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    @Override
    public void setSkinFromName(SkinPlayer player, String name) {
        if (!Validator.validateName(name)){
            player.sendMessage(lang.of("skin.name.invalid"));
            return;
        }

        if (isBlackList(name, player) || !isWhitelist(name, player)){
            player.sendMessage(lang.of("skin.unallowed"));
            return;
        }

        Optional<Skin> hashed = SkinHash.get(name);

        if (hashed.isPresent()){
            setCustomSkin(player, hashed.get());
            return;
        }

        nameQueue.push(player, name);
        long seconds = nameQueue.getWaitSeconds();
        player.sendMessage(String.format(lang.of("skin.process"), seconds));
    }

    @Override
    public void resetSkin(SkinPlayer player) {
        if(!player.hasCustomSkin()){
            player.sendMessage(lang.of("skin.reset.empty"));
            return;
        }

        Events.fireSkinReset(new EventSkinReset(player, player.getCurrentSkin()), (event)->{
            if (!event.isCancelled()){
                player.resetSkin();
                player.applySkin();
                player.refreshSkin();
                savePlayer(player);
                player.sendMessage(lang.of("skin.reset.success"));
            }
        });
    }

    @Override
    public void openSkinsMenu(Player player, int page) {
        if (page < 1) return;

        int range = 45;
        int offset = (page-1) * range;

        String sql = "SELECT name,custom_value,custom_signature FROM %s WHERE custom_value IS NOT NULL LIMIT %s OFFSET %s";
        Row[] rows = database.getRowsWithRequest(String.format(sql, Tables.SKINS, range, offset));

        if (rows.length == 0) {
            if (page == 1) player.sendMessage(TextComponent.of(lang.of("menu.empty")));
            return;
        }

        JsonObject message = new JsonObject();
        JsonArray heads = new JsonArray();

        for (Row row : rows){
            JsonObject head = new JsonObject();

            String name = row.getField("name").toString();
            String texture = row.getField("custom_value").toString();
            String signature = row.getField("custom_signature").toString();

            head.addProperty("name", name);
            head.addProperty("texture", texture);
            head.addProperty("signature", signature);

            heads.add(head);
        }

        message.addProperty("player", player.getUsername());
        message.addProperty("page", page);
        message.add("heads", heads);

        messageSender.sendMessage(player, Channels.MENU, message);
    }

    @Override
    public SkinPlayer buildPlayer(UUID uuid, String name) {
        return new VelocitySkinPlayer(uuid, name, messageSender, server);
    }

    @Override
    public void addPlayer(SkinPlayer player) {
        playersByName.put(player.getName().toLowerCase(), player);
        playersByUUID.put(player.getUUID(), player);
    }

    @Override
    public void removePlayer(UUID uuid) {
        SkinPlayer player = getPlayer(uuid);
        if (player != null){
            playersByName.remove(player.getName().toLowerCase());
            playersByUUID.remove(uuid);
        }
    }

    private void loadBlacklist(){
        if (conf.isEnableBlackList()){
            this.blacklist = new HashMap<>();
            List<String> list = conf.getSkinsBlackList();

            for (String elem : list){
                String[] arr = elem.split(":");
                blacklist.put(arr[0].toLowerCase(), (arr.length == 2) ? arr[1] : null);
            }
        }
    }

    private void loadWhitelist(){
        if (conf.isEnableWhitelist()){
            this.blacklist = null;
            this.whitelist = new HashMap<>();

            List<String> list = conf.getSkinsWhiteList();

            for (String elem : list){
                String[] arr = elem.split(":");
                whitelist.put(arr[0].toLowerCase(), (arr.length == 2) ? arr[1] : null);
            }
        }
    }

    private void loadQueues() {
        nameQueue = new NameQueue(this, 1);
        int imagePeriod = 1;

        if (conf.isEnableMojangAccounts()){
            imagePeriod = conf.getMojangQueryPeriod();
            imageQueue = new MojangQueue(this, conf.getMojangProfiles(), imagePeriod);
        } else {
            imageQueue = new MineskinQueue(this, imagePeriod);
        }

        VelocityTasks.runRepeat(nameQueue, 1000);
        VelocityTasks.runRepeat(imageQueue, imagePeriod * 1000);
    }

    @Override
    public void savePlayer(SkinPlayer player) {
        VelocityTasks.run(()->savePlayerBlocking(player));
    }
}
