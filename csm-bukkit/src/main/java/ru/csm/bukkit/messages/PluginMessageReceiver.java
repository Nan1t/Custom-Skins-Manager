package ru.csm.bukkit.messages;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.csm.api.network.MessageReceiver;

public class PluginMessageReceiver extends MessageReceiver implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        receive(channel, data);
    }

}
