package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinPlayer extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkinPlayer(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin player <player>");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        if (args.length == 1){
            SkinPlayer<ProxiedPlayer> player = api.getPlayer(sender.getName());
            if(player != null){
                api.setSkinFromName(player, args[0]);
            }
            return;
        }

        sender.sendMessage(usage);
    }
}
