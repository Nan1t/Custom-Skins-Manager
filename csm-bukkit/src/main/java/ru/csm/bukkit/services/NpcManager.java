package ru.csm.bukkit.services;

import org.bukkit.entity.Player;
import ru.csm.bukkit.npc.NPC;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NpcManager {

    private static final Map<UUID, NPC> NPC_MAP = new HashMap<>();

    private NpcManager(){}

    public static Collection<NPC> getAllNPC(){
        return NPC_MAP.values();
    }

    public static NPC getPlayerNPC(UUID uuid){
        return NPC_MAP.get(uuid);
    }

    public static void addNpc(Player player, NPC npc){
        NPC_MAP.put(player.getUniqueId(), npc);
    }

    public static void removeNpc(Player player){
        NPC_MAP.remove(player.getUniqueId());
    }
}
