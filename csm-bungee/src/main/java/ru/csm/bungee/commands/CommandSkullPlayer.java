package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkullPlayer extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkullPlayer(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skull player <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof ProxiedPlayer){
                ProxiedPlayer player = (ProxiedPlayer) sender;
                Head head = api.getPlayerHead(args[0]);

                if (head != null){
                    // TODO send message to give item
                }
            }

            return;
        }

        sender.sendMessage(usage);
    }
}
