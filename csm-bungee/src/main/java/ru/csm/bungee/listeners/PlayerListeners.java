package ru.csm.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class PlayerListeners implements Listener {

    private final SkinsAPI<ProxiedPlayer> api;

    public PlayerListeners(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        SkinPlayer<ProxiedPlayer> player = api.loadPlayer(e.getPlayer(), e.getPlayer().getUniqueId());

        if (player != null){
            api.addPlayer(player);
            updateSkin(player);
            return;
        }

        player = api.buildPlayer(e.getPlayer());
        api.createNewPlayer(player);
        api.addPlayer(player);
        updateSkin(player);
    }

    private void updateSkin(SkinPlayer<?> player){
        player.applySkin();
        player.refreshSkin();
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }
}
