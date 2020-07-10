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
        NPC npc = NPC_MAP.remove(player.getUniqueId());
        if (npc != null) npc.destroy(player);
    }
}
