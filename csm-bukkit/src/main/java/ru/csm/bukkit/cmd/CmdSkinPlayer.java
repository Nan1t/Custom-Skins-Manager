package ru.csm.bukkit.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinPlayer implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinPlayer(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof Player)) return;

        String username = ctx.getString("username");
        SkinPlayer player = api.getPlayer(sender.getName());

        if(player != null){
            api.setSkinFromName(player, username);
        }
    }

}
