/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        try{
            return items[slot];
        } catch (IndexOutOfBoundsException e){
            return null;
        }
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
            if (slot >= SIZE) break;
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
