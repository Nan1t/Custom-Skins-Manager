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

public class BukkitBungeeSkinsAPI implements SkinsAPI<Player> {

    private final Language lang;
    private final MessageSender<Player> messageSender;

    public BukkitBungeeSkinsAPI(Language lang, MessageSender<Player> messageSender){
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
    public boolean isBlackList(String nickname, SkinPlayer<Player> player) {
        return false;
    }

    @Override
    public boolean isWhitelist(String nickname, SkinPlayer<Player> player) {
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
    public SkinPlayer<Player> getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public SkinPlayer<Player> getPlayer(String name) {
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
    public void setCustomSkin(SkinPlayer<Player> player, Skin skin) {
        setCustomSkin(player.getPlayer(), skin);
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
    public void setSkinFromImage(SkinPlayer<Player> player, String link, SkinModel model) {
        JsonObject message = new JsonObject();

        message.addProperty("action", "image");
        message.addProperty("player", player.getName());
        message.addProperty("url", link);
        message.addProperty("model", model.toString());

        messageSender.sendMessage(player.getPlayer(), Channels.SKINS, message);
    }

    @Override
    public void setSkinFromName(SkinPlayer<Player> player, String name) {
        JsonObject message = new JsonObject();

        message.addProperty("action", "name");
        message.addProperty("player", player.getName());
        message.addProperty("name", name);

        messageSender.sendMessage(player.getPlayer(), Channels.SKINS, message);
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
    public void resetSkin(SkinPlayer<Player> player) {
        JsonObject message = new JsonObject();

        message.addProperty("action", "reset");
        message.addProperty("player", player.getName());

        messageSender.sendMessage(player.getPlayer(), Channels.SKINS, message);
    }

    @Override
    public void openSkinsMenu(Player player, int page) {
        JsonObject json = new JsonObject();
        json.addProperty("player", player.getName());
        json.addProperty("page", page);
        messageSender.sendMessage(player, Channels.MENU, json);
    }

    @Override
    public SkinPlayer<Player> buildPlayer(Player player) {
        return null;
    }

    @Override
    public void addPlayer(SkinPlayer<Player> player) {

    }

    @Override
    public void removePlayer(UUID uuid) {

    }
}
