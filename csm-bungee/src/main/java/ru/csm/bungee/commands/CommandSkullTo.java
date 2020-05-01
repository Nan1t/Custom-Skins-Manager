package ru.csm.bungee.commands;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkullTo extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final MessageSender<ProxiedPlayer> sender;

    public CommandSkullTo(SkinsAPI<ProxiedPlayer> api, MessageSender<ProxiedPlayer> sender){
        this.api = api;
        this.sender = sender;
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length == 3){
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

            if (target == null || !target.isConnected()){
                sender.sendMessage(String.format("Player %s does not exist or offline", args[0]));
                return;
            }

            if (args[1].equalsIgnoreCase("from")){
                Head head = api.getPlayerHead(args[2]);

                if (head != null){
                    JsonObject message = new JsonObject();
                    message.addProperty("player", target.getName());
                    message.addProperty("url", head.getUrl());
                    this.sender.sendMessage(target, Channels.SKULLS, message);
                    return;
                }

                sender.sendMessage(String.format(api.getLang().of("player.missing"), args[0]));
                return;
            } else if (args[1].equalsIgnoreCase("url")){
                JsonObject message = new JsonObject();
                message.addProperty("player", target.getName());
                message.addProperty("url", args[2]);
                this.sender.sendMessage(target, Channels.SKULLS, message);
                return;
            }
        }

        sender.sendMessage("Wrong admin command format! Check the manual on SpigotMC plugin page");
    }
}
