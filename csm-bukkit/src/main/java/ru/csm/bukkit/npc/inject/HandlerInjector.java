package ru.csm.bukkit.npc.inject;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.joor.Reflect;

public class HandlerInjector {

    private static final String HANDLER_NAME = "csm_packet_handler";

    private HandlerInjector(){ }

    public static void inject(Player player){
        Channel channel = Reflect.on(player)
                .call("getHandle")
                .field("playerConnection")
                .field("networkManager")
                .field("channel")
                .get();

        channel.pipeline().addBefore("packet_handler", HANDLER_NAME, new NpcPacketHandler(player));
    }

}
