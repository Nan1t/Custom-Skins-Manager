package ru.csm.bukkit.protocol;

import ru.csm.bukkit.protocol.npc.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class NPCService {

    private Map<UUID, NPC> npcs = new TreeMap<>();
    private int version;

    public NPCService(int version){
        this.version = version;
    }

    public void addNPC(UUID viewer, NPC npc){
        npcs.put(viewer, npc);
    }

    public NPC getNPC(UUID viewer){
        return npcs.get(viewer);
    }

    public NPC getNPC(int entityId){
        for(NPC npc : npcs.values()){
            if(npc.getEntityId() == entityId){
                return npc;
            }
        }

        return null;
    }

    public NPC createNPC(UUID uuid, String name){
        NPC npc;
        if(version == 8){
            npc = new NPC_1_8(uuid, name);
        }
        else if(version == 9){
            npc = new NPC_1_9(uuid, name);
        }
        else if (version == 13){
            npc = new NPC_1_13(uuid, name);
        }
        else {
            npc = new NPC_1_10(uuid, name);
        }

        return npc;
    }
}
