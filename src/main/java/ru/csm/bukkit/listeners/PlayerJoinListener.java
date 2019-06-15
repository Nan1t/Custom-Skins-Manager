package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.bukkit.Skins;
import ru.csm.bukkit.player.BukkitSkinPlayer;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private Plugin plugin;
    private Database db;
    private SkinsAPI api;

    public PlayerJoinListener(Plugin plugin, Database db, SkinsAPI api) {
        this.plugin = plugin;
        this.db = db;
        this.api = api;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        SkinPlayer<Player> player = api.getPlayer(e.getPlayer().getUniqueId());

        if(player != null){
            player.setPlayer(e.getPlayer());
            new BukkitRunnable(){
                public void run(){
                    player.applySkin();
                    player.refreshSkin();
                }
            }.runTaskLater(plugin, 20);
            return;
        }

        Row row = db.getRow(Tables.SKINS, "uuid", e.getPlayer().getUniqueId().toString());

        if(row != null){
            getPlayerFromRow(e.getPlayer(), row);
            return;
        }

        createPlayer(e.getPlayer());
    }

    private void createPlayer(Player template){
        SkinPlayer player = new BukkitSkinPlayer(template);

        UUID uuid = MojangAPI.getUUID(template.getName());

        if(uuid != null){
            Skin skin = MojangAPI.getPremiumSkin(uuid);

            if(skin != null){
                player.setDefaultSkin(skin);
                player.applySkin();
                player.refreshSkin();

                api.addPlayer(player);
                api.createPlayer(player);
                return;
            }
        }

        player.setDefaultSkin(api.getDefaultSkin());
        player.applySkin();

        api.addPlayer(player);
        api.createPlayer(player);
    }

    private void getPlayerFromRow(Player template, Row row){
        SkinPlayer player = new BukkitSkinPlayer(template);
        Skin defaultSkin, customSkin;

        String defaultValue = row.getField("default_value").toString();
        String defaultSignature = row.getField("default_signature").toString();
        defaultSkin = new Skin(defaultValue, defaultSignature);

        Object customValue = row.getField("custom_value");
        Object customSignature = row.getField("custom_signature");

        if(customValue != null && customSignature != null){
            customSkin = new Skin(customValue.toString(), customSignature.toString());
            player.setCustomSkin(customSkin);
        }

        player.setDefaultSkin(defaultSkin);
        player.applySkin();

        api.addPlayer(player);
    }
}
