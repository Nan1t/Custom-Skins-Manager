package ru.csm.bukkit.protocol.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {

    int getEntityID();

    Location getLocation();

    String getText();

    void setText(String text);

    void show(Player player);

    void hide(Player player);

    void destroy();
}
