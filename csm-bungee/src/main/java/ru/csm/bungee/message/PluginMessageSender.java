package ru.csm.bungee.message;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.MessageSender;

public class PluginMessageSender extends MessageSender<ProxiedPlayer> {

    @Override
    public void send(ProxiedPlayer player, String channel, byte[] data) {
        if (player.getServer() != null){
            player.getServer().getInfo().sendData(channel, data);
        }
    }

}
