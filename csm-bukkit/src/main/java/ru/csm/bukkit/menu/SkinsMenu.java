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
