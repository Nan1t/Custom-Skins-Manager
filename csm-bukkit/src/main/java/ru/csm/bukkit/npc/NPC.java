package ru.csm.bukkit.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.services.NpcManager;

import java.util.List;

public interface NPC {

    int getId();

    String getName();

    void setName(String name);

    Location getLocation();

    void setLocation(Location location);

    Skin getSkin();

    void setSkin(Skin skin);

    String getPermission();

    void setPermission(String permission);

    boolean isOpenMenu();

    void setOpenMenu(boolean openMenu);

    void setDisplayName(List<String> name);

    void spawn(Player player);

    void destroy(Player player);

}
