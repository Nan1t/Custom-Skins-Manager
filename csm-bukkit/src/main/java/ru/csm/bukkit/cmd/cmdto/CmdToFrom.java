package ru.csm.bukkit.cmd.cmdto;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdToFrom implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdToFrom(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        Player player = ctx.get("target", null);

        if (player == null){
            sender.sendMessage("Player does not exist or offline");
            return;
        }

        SkinPlayer target = api.getPlayer(player.getName());

        if (target == null){
            sender.sendMessage("Player does not exist or offline");
            return;
        }

        api.setSkinFromName(target, ctx.getString("username"));
        sender.sendMessage(String.format("Processing skin from name for %s...", target.getName()));
    }
}
