package ru.csm.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.csm.bukkit.menu.SkinsMenu;
import ru.csm.bukkit.menu.item.Item;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getSlot() < 0) return;

        if (event.getWhoClicked() instanceof Player
                && event.getInventory().getHolder() instanceof SkinsMenu){
            SkinsMenu menu = (SkinsMenu) event.getInventory().getHolder();
            Item clicked = menu.getItem(event.getSlot());

            if (clicked != null){
                clicked.doClick((Player) event.getWhoClicked());
            }

            event.setCancelled(true);
        }
    }

}
