package ru.csm.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.MojangAPI;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.storage.Tables;
import ru.csm.api.storage.database.Database;
import ru.csm.api.storage.database.Row;
import ru.csm.bungee.player.BungeeSkinPlayer;

import java.util.UUID;

public class PostLoginListener implements Listener {

    private Database db;
    private SkinsAPI api;

    public PostLoginListener(Database db, SkinsAPI api){
        this.db = db;
        this.api = api;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        SkinPlayer<ProxiedPlayer> player = api.getPlayer(e.getPlayer().getUniqueId());

        if(player != null){
            player.setPlayer(e.getPlayer());
            player.applySkin();
            return;
        }

        Row row = db.getRow(Tables.SKINS, "uuid", e.getPlayer().getUniqueId().toString());

        if(row != null){
            getPlayerFromRow(e.getPlayer(), row);
            return;
        }

        createPlayer(e.getPlayer());
    }

    private void createPlayer(ProxiedPlayer template){
        SkinPlayer player = new BungeeSkinPlayer(template);

        UUID uuid = MojangAPI.getUUID(template.getName());

        if(uuid != null){
            Skin skin = MojangAPI.getPremiumSkin(uuid);

            if(skin != null){
                player.setDefaultSkin(skin);
                player.applySkin();

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

    private void getPlayerFromRow(ProxiedPlayer template, Row row){
        SkinPlayer player = new BungeeSkinPlayer(template);
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
