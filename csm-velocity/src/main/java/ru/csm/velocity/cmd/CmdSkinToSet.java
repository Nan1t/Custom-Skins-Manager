package ru.csm.velocity.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinToSet implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinToSet(SkinsAPI<Player> api){
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

        String texture = ctx.getString("texture");
        String signature = ctx.getString("signature");

        api.setCustomSkin(target, Skin.of(texture, signature));
    }

}
