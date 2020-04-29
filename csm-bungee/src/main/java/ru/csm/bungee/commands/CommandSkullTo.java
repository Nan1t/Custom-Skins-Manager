package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkullTo extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;

    public CommandSkullTo(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3){
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

            if (target == null || !target.isConnected()){
                sender.sendMessage(String.format("Player %s does not exist or offline", args[0]));
                return;
            }

            if (args[1].equalsIgnoreCase("from")){
                Head head = api.getPlayerHead(args[2]);

                if (head != null){
                    // TODO
                }
                return;
            } else if (args[1].equalsIgnoreCase("url")){
                // TODO
                return;
            }
        }

        sender.sendMessage("Wrong admin command format! Check the manual on SpigotMC plugin page");
    }
}
