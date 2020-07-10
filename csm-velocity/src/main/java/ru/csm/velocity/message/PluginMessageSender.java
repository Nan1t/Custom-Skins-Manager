package ru.csm.velocity.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.csm.api.network.MessageSender;

public class PluginMessageSender extends MessageSender<Player> {

    @Override
    public void send(Player player, String channel, byte[] data) {
        if (player.getCurrentServer().isPresent()){
            String[] arr = channel.split(":");
            ChannelIdentifier identifier = MinecraftChannelIdentifier.create(arr[0], arr[1]);
            player.getCurrentServer().get().sendPluginMessage(identifier, data);
        }
    }

}
