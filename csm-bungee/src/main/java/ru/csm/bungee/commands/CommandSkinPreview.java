package ru.csm.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinPreview extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkinPreview(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin preview <player> <texture> <signature> [permission]");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length >= 3){
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            String texture = args[1];
            String signature = args[2];
            String permission = null;

            if (player == null || !player.isConnected()){
                sender.sendMessage("Player is offline");
                return;
            }

            if (args.length == 4) permission = args[3];

            api.showPreview(player, new Skin(texture, signature), false, permission);
            return;
        }

        sender.sendMessage(usage);
    }
}
