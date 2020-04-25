package ru.csm.bukkit.npc;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.csm.api.utils.Logger;
import ru.csm.bukkit.event.NpcClickEvent;
import ru.csm.bukkit.services.NpcManager;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class NpcPacketHandler extends ChannelDuplexHandler {

    private static Class<?> packetClass;
    private static Field idField;
    private static Field actionField;
    private static Field handField;

    private static MethodHandle getHandlerMethod;

    private static Field playerConnectionField;
    private static Field networkManagerField;
    private static Field channelField;

    private final Player player;

    public NpcPacketHandler(Player player){
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        if (packet.getClass().equals(packetClass)){
            if (handField != null){
                Object hand = handField.get(packet);
                if (hand != null && !hand.toString().equals("MAIN_HAND")) return;
            }

            NPC npc = NpcManager.getPlayerNPC(player.getUniqueId());

            if (npc != null){
                ClickAction clickAction = ClickAction.valueOf(actionField.get(packet).toString());
                Bukkit.getServer().getPluginManager().callEvent(new NpcClickEvent(player, npc, clickAction));
            }
        }
        super.channelRead(context, packet);
    }

    public static void inject(Player player){
        NpcPacketHandler handler = new NpcPacketHandler(player);

        try{
            Object entityPlayer = getHandlerMethod.invoke(player);
            Object playerConnection = playerConnectionField.get(entityPlayer);
            Object networkManager = networkManagerField.get(playerConnection);
            Channel channel = (Channel) channelField.get(networkManager);
            channel.pipeline().addBefore("packet_handler", player.getName(), handler);
        } catch (Throwable t){
            t.printStackTrace();
            Logger.severe("Cannot inject packet listener to player: %s", t.getMessage());
        }
    }

    public static void init(String version){
        try{
            packetClass = Class.forName(String.format("net.minecraft.server.%s.PacketPlayInUseEntity", version));
            idField = packetClass.getDeclaredField("a");
            idField.setAccessible(true);
            actionField = packetClass.getDeclaredField("action");
            actionField.setAccessible(true);

            try{
                handField = packetClass.getDeclaredField("d");
                handField.setAccessible(true);
            } catch (Exception e){
                handField = null;
            }

            Class<?> cpClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.entity.CraftPlayer", version));
            Class<?> epClass = Class.forName(String.format("net.minecraft.server.%s.EntityPlayer", version));
            Class<?> playerConnectionClass = Class.forName(String.format("net.minecraft.server.%s.PlayerConnection", version));
            Class<?> networkManagerClass = Class.forName(String.format("net.minecraft.server.%s.NetworkManager", version));

            getHandlerMethod = MethodHandles.lookup().unreflect(cpClass.getDeclaredMethod("getHandle"));
            playerConnectionField = epClass.getDeclaredField("playerConnection");
            networkManagerField = playerConnectionClass.getDeclaredField("networkManager");
            channelField = networkManagerClass.getDeclaredField("channel");
        } catch (Exception e){
            e.printStackTrace();
            Logger.severe("Cannot initialize NPC packet listener: %s", e.getMessage());
        }
    }
}
