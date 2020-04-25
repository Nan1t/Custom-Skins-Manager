package ru.csm.bukkit.handler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;

import java.util.Collections;
import java.util.Iterator;

public final class Handler_v1_10_R1 implements SkinHandler {

    @Override
    public Skin getSkin(Player player) {
        GameProfile profile = ((CraftPlayer)player).getProfile();
        Iterator<Property> iterator = profile.getProperties().get("textures").iterator();

        if (iterator.hasNext()){
            Property property = iterator.next();
            return new Skin(property.getValue(), property.getSignature());
        }

        return null;
    }

    @Override
    public void applySkin(Player player, Skin skin) {
        PropertyMap propertyMap = ((CraftPlayer)player).getProfile().getProperties();
        propertyMap.removeAll("textures");
        propertyMap.put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
    }

    @Override
    public void updateSkin(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        EntityPlayer ep = cp.getHandle();

        PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        PacketPlayOutEntityDestroy entityDestroy = new PacketPlayOutEntityDestroy(cp.getEntityId());
        PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());

        WorldServer worldServer = (WorldServer)ep.getWorld();
        int dimension = worldServer.worldProvider.getDimensionManager().getDimensionID();
        EnumDifficulty difficulty = EnumDifficulty.getById(cp.getWorld().getDifficulty().getValue());
        WorldType worldType = worldServer.getWorldData().getType();

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dimension, difficulty, worldType, ep.playerInteractManager.getGameMode());
        PacketPlayOutPosition position = new PacketPlayOutPosition(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch(),
                Collections.emptySet(),
                0
        );
        PacketPlayOutHeldItemSlot slot = new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot());

        ep.playerConnection.sendPacket(removeInfo);
        ep.playerConnection.sendPacket(addInfo);
        ep.playerConnection.sendPacket(respawn);
        ep.playerConnection.sendPacket(position);
        ep.playerConnection.sendPacket(slot);

        updateData(player);

        for (Player p : Bukkit.getOnlinePlayers()){
            if (!p.equals(player)){
                PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;

                if (player.getWorld().equals(p.getWorld()) && p.canSee(player)){
                    connection.sendPacket(entityDestroy);
                    connection.sendPacket(removeInfo);
                    connection.sendPacket(addInfo);
                    connection.sendPacket(entitySpawn);
                    continue;
                }

                connection.sendPacket(removeInfo);
                connection.sendPacket(addInfo);
            }
        }
    }
}
