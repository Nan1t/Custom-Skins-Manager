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

public class PlayerJoinListener implements Listener {

    private final SkinsAPI<Player> api;

    public PlayerJoinListener(SkinsAPI<Player> api) {
        this.api = api;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        NpcPacketHandler.inject(e.getPlayer());

        BukkitTasks.runTaskAsync(()->{
            SkinPlayer<Player> player = api.loadPlayer(e.getPlayer(), e.getPlayer().getUniqueId());

            if (player != null){
                api.addPlayer(player);
                updateSkin(player);
                return;
            }

            player = api.buildPlayer(e.getPlayer());
            api.createNewPlayer(player);
            api.addPlayer(player);
            updateSkin(player);
        });
    }

    private void updateSkin(SkinPlayer<?> player){
        player.applySkin();
        player.refreshSkin();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        api.removePlayer(event.getPlayer().getUniqueId());
    }
}
