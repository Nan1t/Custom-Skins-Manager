package ru.csm.bukkit.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Hologram {

    Location getLocation();

    void setLocation(Location location);

    void setLines(List<String> lines);

    void spawn(Player player);

    void destroy(Player player);

}
