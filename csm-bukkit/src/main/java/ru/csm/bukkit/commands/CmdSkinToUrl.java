package ru.csm.bukkit.commands;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinToUrl implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinToUrl(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        String targetName = ctx.<Player>get("target").get().getName();
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
