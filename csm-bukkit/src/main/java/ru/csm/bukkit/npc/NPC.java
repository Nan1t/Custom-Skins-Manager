package ru.csm.bukkit.npc;

import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;

import java.util.List;

public interface NPC {

    int getId();

    Skin getSkin();

    void setSkin(Skin skin);

    String getPermission();

    void setPermission(String permission);

    void setDisplayName(List<String> name);

    void spawn(Player player);

    void destroy(Player player);

}
