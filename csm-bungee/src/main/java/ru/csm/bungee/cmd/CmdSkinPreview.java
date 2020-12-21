package ru.csm.bungee.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinPreview implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;

    public CmdSkinPreview(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        ProxiedPlayer player = ctx.get("player", null);

        if (player != null){
            String texture = ctx.getString("texture");
            String signature = ctx.getString("signature");
            String permission = ctx.getString("permission", null);

            api.showPreview(player, new Skin(texture, signature), false, permission);
        } else {
            sender.sendMessage("Player is offline");
        }
    }

}
