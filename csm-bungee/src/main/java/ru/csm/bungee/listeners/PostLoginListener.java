package ru.csm.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class PostLoginListener implements Listener {

    private final SkinsAPI<ProxiedPlayer> api;

    public PostLoginListener(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        SkinPlayer<ProxiedPlayer> player = api.getPlayer(e.getPlayer().getUniqueId());

        if(player != null){
            player.applySkin();
            return;
        }
        // Load player

        player = api.loadPlayer(e.getPlayer(), e.getPlayer().getUniqueId());

        if (player != null){
            api.addPlayer(player);
            updateSkin(player);
            return;
        }

        // Create player

        player = api.buildPlayer(e.getPlayer());
        api.createNewPlayer(player);
    }

    private void updateSkin(SkinPlayer<?> player){
        player.applySkin();
        player.refreshSkin();
    }
}
