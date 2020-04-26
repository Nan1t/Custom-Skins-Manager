package ru.csm.bukkit.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import ru.csm.bukkit.menu.item.Item;

import java.util.Collection;
import java.util.List;

public interface SkinsMenu extends InventoryHolder, Cloneable {

    Item getItem(int slot);

    Collection<Item> getItems();

    void setItems(List<Item> items);

    void setItem(int slot, Item item);

    void open(Player player);

}
