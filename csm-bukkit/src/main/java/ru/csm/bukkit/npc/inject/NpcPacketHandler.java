package ru.csm.bukkit.npc.inject;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import napi.reflect.BukkitReflect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.csm.api.logging.Logger;
import ru.csm.bukkit.event.NpcClickEvent;
import ru.csm.bukkit.npc.ClickAction;
import ru.csm.bukkit.npc.NPC;
import ru.csm.bukkit.services.NpcManager;
import ru.csm.bukkit.util.BukkitTasks;

import java.lang.reflect.Field;

public class NpcPacketHandler extends ChannelDuplexHandler {

    private static Class<?> packetClass;
    private static Field idField;
    private static Field actionField;
    private static Field handField;

    private final Player player;

    public NpcPacketHandler(Player player){
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet.getClass().equals(packetClass)){
            if (handField != null){
                Object hand = handField.get(packet);
                if (hand != null && !hand.toString().equals("MAIN_HAND")) return;
            }

            int id = (Integer) idField.get(packet);
            NPC npc = NpcManager.getPlayerNPC(player.getUniqueId());

            if (npc != null && npc.getId() == id){
                ClickAction clickAction = ClickAction.valueOf(actionField.get(packet).toString());
                BukkitTasks.runTask(()->{
                    Bukkit.getServer().getPluginManager().callEvent(new NpcClickEvent(player, npc, clickAction));
                });
            }
        }
        super.channelRead(ctx, packet);
    }

    public static void initClasses(){
        try {
            packetClass = BukkitReflect.ofNms("PacketPlayInUseEntity").type();
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
        } catch (Exception e){
            Logger.severe("Cannot fetch packet handler classes: ", e);
        }
    }
}
