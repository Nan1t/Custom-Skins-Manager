package ru.csm.bukkit.protocol.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;

import java.util.List;
import java.util.UUID;

public interface NPC {

    Location getLocation();

    String getName();

    UUID getUUID();

    Skin getSkin();

    int getEntityId();

    void setSkin(Skin skin);

    void setLocation(Location location);

    void setCustomName(List<String> lines);

    void spawn(Player player);

    void destroy();

}