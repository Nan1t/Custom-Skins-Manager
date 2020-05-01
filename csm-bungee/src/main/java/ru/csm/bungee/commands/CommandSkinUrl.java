package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinUrl extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkinUrl(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin url <url> [slim]");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        SkinPlayer<ProxiedPlayer> player = api.getPlayer(sender.getName());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            if (args.length > 0){
                if (args.length == 2 && args[1].equals("slim")) model = SkinModel.ALEX;
                api.setSkinFromImage(player, args[0], model);
                return;
            }
        }

        sender.sendMessage(usage);
    }
}
