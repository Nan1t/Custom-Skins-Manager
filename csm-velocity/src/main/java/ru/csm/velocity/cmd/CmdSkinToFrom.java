package ru.csm.velocity.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinToFrom implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinToFrom(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        String targetName = ctx.<Player>get("target").get().getUsername();
        SkinPlayer target = api.getPlayer(targetName);

        if (target == null){
            sender.sendMessage("Player does not exist or offline");
            return;
        }

        api.setSkinFromName(target, ctx.getString("username"));
        sender.sendMessage(String.format("Processing skin from name for %s...", target.getName()));
    }
}
