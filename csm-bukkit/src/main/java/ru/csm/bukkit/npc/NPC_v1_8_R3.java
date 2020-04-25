package ru.csm.bukkit.npc;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NPC_v1_8_R3 extends AbstractNPC {

    @Override
    public void spawn(Player player) {

    }

    @Override
    public void destroy(Player player) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroy);
        hologram.destroy(player);
    }
}
