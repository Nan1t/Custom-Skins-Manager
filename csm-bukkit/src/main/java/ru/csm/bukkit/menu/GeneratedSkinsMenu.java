package ru.csm.bukkit.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.csm.bukkit.menu.item.Item;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GeneratedSkinsMenu implements SkinsMenu {

    private static final int SIZE = 54;

    private final Inventory inventory;
    private final Item[] items = new Item[SIZE];

    public GeneratedSkinsMenu(String title){
        inventory = Bukkit.createInventory(this, SIZE, title);
    }

    @Override
    public Item getItem(int slot) {
        return items[slot];
    }

    @Override
    public Collection<Item> getItems() {
        return Arrays.asList(items);
    }

    @Override
    public void setItems(List<Item> items) {
        Iterator<Item> iterator = items.iterator();
        int slot = 0;

        while (iterator.hasNext()){
            this.items[slot] = iterator.next();
            slot++;
        }
    }

    @Override
    public void setItem(int slot, Item item) {
        this.items[slot] = item;
    }

    @Override
    public void open(Player player) {
        for (int i = 0; i < items.length; i++){
            if (items[i] != null) inventory.setItem(i, items[i].toItemStack());
        }
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
