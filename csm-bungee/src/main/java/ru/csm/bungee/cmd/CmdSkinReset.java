package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinReset implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkinReset(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof ProxiedPlayer)) return;

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player != null){
            api.resetSkin(player);
        }
    }

}
