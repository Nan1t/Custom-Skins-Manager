package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.event.player.PlayerQuitEvent;
import ru.csm.api.services.SkinsAPI;
import ru.csm.api.player.SkinPlayer;
import ru.csm.bukkit.npc.NpcPacketHandler;
import ru.csm.bukkit.util.BukkitTasks;

public class PlayerListener implements Listener {

    private final SkinsAPI<Player> api;

    public PlayerListener(SkinsAPI<Player> api) {
        this.api = api;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        NpcPacketHandler.inject(event.getPlayer());
        BukkitTasks.runTaskLater(()->{
            SkinPlayer<Player> player = api.loadPlayer(event.getPlayer(), event.getPlayer().getUniqueId());

            if (player == null){
                player = api.buildPlayer(event.getPlayer());
                api.createNewPlayer(player);
            }

            api.addPlayer(player);
            player.applySkin();
            player.refreshSkin();
        }, 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }
}
