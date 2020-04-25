package ru.csm.bukkit.menu.item;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ClickAction {

    void onClick(Player clicker, Item clicked);

}
