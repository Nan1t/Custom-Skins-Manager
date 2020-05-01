package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.event.NpcClickEvent;
import ru.csm.bukkit.npc.ClickAction;
import ru.csm.bukkit.npc.NPC;
import ru.csm.bukkit.services.MenuManager;
import ru.csm.bukkit.services.NpcManager;

public class NpcClickListener implements Listener {

    private final SkinsAPI<Player> skinsAPI;
    private final MenuManager menuManager;

    public NpcClickListener(SkinsAPI<Player> skinsAPI, MenuManager menuManager){
        this.skinsAPI = skinsAPI;
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onNpcClick(NpcClickEvent event){
        NPC npc = event.getNpc();

        if (event.getAction().equals(ClickAction.ATTACK)){
            NpcManager.removeNpc(event.getPlayer());
            if (npc.isOpenMenu()){
                menuManager.getOpenedMenu(event.getPlayer()).ifPresent((menu)->{
                    menu.open(event.getPlayer());
                });
                return;
            }
        }

        if (event.getAction().equals(ClickAction.INTERACT)){
            if (npc.getPermission() != null && !event.getPlayer().hasPermission(npc.getPermission())) return;
            skinsAPI.setCustomSkin(event.getPlayer(), npc.getSkin());
            npc.destroy(event.getPlayer());
        }
    }

}
