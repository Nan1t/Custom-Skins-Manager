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

package ru.csm.bukkit.hologram.nms;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.csm.bukkit.hologram.AbstractHologram;
import ru.csm.bukkit.hologram.HoloLine;

public class Hologram_v1_14_R1 extends AbstractHologram {

    @Override
    public void spawn(Player p) {
        EntityPlayer player = ((CraftPlayer)p).getHandle();
        World world = ((CraftWorld)getLocation().getWorld()).getHandle();

        for (HoloLine line : getLines()){
            EntityLiving armorStand = new EntityArmorStand(world,
                    line.getLocation().getX(),
                    line.getLocation().getY(),
                    line.getLocation().getZ());

            armorStand.setCustomName(new ChatComponentText(line.getText()));
            armorStand.setCustomNameVisible(true);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);

            player.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
            player.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), false));
            line.setId(armorStand.getId());
        }
    }

    @Override
    public void destroy(Player player) {
        int[] ids = new int[getLines().size()];
        int index = 0;

        for (HoloLine line : getLines()){
            ids[index] = line.getId();
            index++;
        }

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutEntityDestroy(ids));
    }
}
