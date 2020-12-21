package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinToReset implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkinToReset(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        String targetName = ctx.<ProxiedPlayer>get("target").get().getName();
        SkinPlayer target = api.getPlayer(targetName);

        if (target == null){
            sender.sendMessage("Player does not exist or offline");
            return;
        }

        api.resetSkin(target);
    }

}
