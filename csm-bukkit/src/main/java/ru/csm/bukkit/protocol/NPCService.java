package ru.csm.bukkit.protocol;

import ru.csm.bukkit.protocol.npc.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class NPCService {

    private Map<UUID, NPC> npcs = new TreeMap<>();

    public NPCService(){

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
        /*switch (version){
            default:
                return new NPC_1_10(uuid, name);
            case 8:
                return new NPC_1_8(uuid, name);
            case 9:
                return new NPC_1_9(uuid, name);
            case 13:
                return new NPC_1_13(uuid, name);
            case 14:
                return new NPC_1_14(uuid, name);
            case 15:
                return new NPC_1_15(uuid, name);
        }*/

        //TODO
        return null;
    }
}
