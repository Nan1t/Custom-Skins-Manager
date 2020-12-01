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

package ru.csm.bukkit.nms.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPC_v1_15_R1 extends AbstractNPC {

    private EntityPlayer npcEntity;

    @Override
    public void spawn(Player p) {
        EntityPlayer player = ((CraftPlayer) p).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        MinecraftServer server = player.server;
        WorldServer worldServer = (WorldServer) player.world;
        PlayerInteractManager manager = new PlayerInteractManager(worldServer);

        profile.getProperties().put("textures",
                new Property("textures", skin.getValue(), skin.getSignature()));

        npcEntity = new EntityPlayer(server, worldServer, profile, manager);
        id = npcEntity.getId();

        npcEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        DataWatcher watcher = npcEntity.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(16), (byte) 127);

        PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(npcEntity);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(id, watcher, false);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                npcEntity
        );
        PacketPlayOutEntityHeadRotation rotation = new PacketPlayOutEntityHeadRotation(npcEntity, getFixRotation(location.getYaw()));

        player.playerConnection.sendPacket(addInfo);
        player.playerConnection.sendPacket(entitySpawn);
        player.playerConnection.sendPacket(metadata);
        player.playerConnection.sendPacket(rotation);

        hologram.spawn(p);
        super.spawn(p);
    }

    @Override
    public void destroy(Player player) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(id);
        PacketPlayOutPlayerInfo remInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                npcEntity
        );

        connection.sendPacket(destroy);
        connection.sendPacket(remInfo);

        hologram.destroy(player);
    }
}
