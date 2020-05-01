package ru.csm.bukkit.messages;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.csm.api.network.MessageSender;

public class PluginMessageSender extends MessageSender<Player> {

    private final Plugin plugin;

    public PluginMessageSender(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void send(Player player, String channel, byte[] data) {
        player.sendPluginMessage(plugin, channel, data);
    }
}
