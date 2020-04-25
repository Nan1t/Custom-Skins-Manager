package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bukkit.event.NpcClickEvent;
import ru.csm.bukkit.npc.ClickAction;

public class NpcClickListener implements Listener {

    private final SkinsAPI<Player> skinsAPI;

    public NpcClickListener(SkinsAPI<Player> skinsAPI){
        this.skinsAPI = skinsAPI;
    }

    @EventHandler
    public void onNpcClick(NpcClickEvent event){
        if (event.getAction().equals(ClickAction.ATTACK)){
            // TODO open skins menu
        }

        if (event.getAction().equals(ClickAction.INTERACT)){
            skinsAPI.setCustomSkin(event.getPlayer(), event.getNpc().getSkin());
            event.getNpc().destroy(event.getPlayer());
        }
    }

}
