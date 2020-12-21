package ru.csm.velocity.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkullUrl implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkullUrl(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof Player)) return;

        SkinPlayer player = api.getPlayer(sender.getName());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            api.setSkinFromImage(player, ctx.getString("url"), model);
        }
    }

}
