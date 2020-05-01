package ru.csm.bungee.commands;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkullUrl extends SubCommand {

    private final String usage;

    private final MessageSender<ProxiedPlayer> sender;

    public CommandSkullUrl(SkinsAPI<ProxiedPlayer> api, MessageSender<ProxiedPlayer> sender){
        this.sender = sender;
        this.usage = String.format(api.getLang().of("command.usage"), "/skull url <url>");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof ProxiedPlayer){
                ProxiedPlayer player = (ProxiedPlayer) sender;
                JsonObject message = new JsonObject();
                message.addProperty("player", player.getName());
                message.addProperty("url", args[0]);
                this.sender.sendMessage(player, Channels.SKULLS, message);
            }
            return;
        }

        sender.sendMessage(usage);
    }
}
