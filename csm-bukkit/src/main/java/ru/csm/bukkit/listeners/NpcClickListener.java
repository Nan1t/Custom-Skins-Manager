package ru.csm.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.csm.bukkit.event.NpcClickEvent;

public class NpcClickListener implements Listener {

    @EventHandler
    public void onNpcClick(NpcClickEvent event){
        System.out.println("Player: " + event.getPlayer().getName());
        System.out.println("NPC: " + event.getNpc().getId());
        System.out.println("Action: " + event.getAction());
    }

}
