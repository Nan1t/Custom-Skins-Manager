package ru.csm.bukkit.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPC_v1_16_R1 extends AbstractNPC {

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