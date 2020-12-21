package ru.csm.bukkit.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinUrl implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinUrl(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof Player)) return;

        SkinPlayer player = api.getPlayer(sender.getName());

        if(player != null){
            String url = ctx.getString("url");
            SkinModel model = ctx.has("slim") ? SkinModel.ALEX : SkinModel.STEVE;

            api.setSkinFromImage(player, url, model);
        }
    }

}
