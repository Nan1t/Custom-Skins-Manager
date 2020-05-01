package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinReset extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkinReset(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin reset");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length > 0){
            sender.sendMessage(usage);
            return;
        }

        SkinPlayer<ProxiedPlayer> player = api.getPlayer(sender.getName());

        if(player != null){
            api.resetSkin(player);
        }
    }
}
