package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkullUrl implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkullUrl(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof ProxiedPlayer)) return;

        SkinPlayer player = api.getPlayer(sender.getName());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            api.setSkinFromImage(player, ctx.getString("url"), model);
        }
    }

}
