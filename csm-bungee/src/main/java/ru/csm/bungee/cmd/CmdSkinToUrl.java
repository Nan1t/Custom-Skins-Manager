package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinToUrl implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkinToUrl(SkinsAPI<ProxiedPlayer> api){
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

        String url = ctx.getString("url");
        SkinModel model = ctx.has("slim") ? SkinModel.ALEX : SkinModel.STEVE;

        api.setSkinFromImage(target, url, model);
        sender.sendMessage(String.format("Processing skin from image (model %s) for %s...",
                model.toString(), target.getName()));
    }

}
