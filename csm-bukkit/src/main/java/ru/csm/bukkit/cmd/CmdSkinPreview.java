package ru.csm.bukkit.cmd;

import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;

public class CmdSkinPreview implements CommandExecutor {

    private final SkinsAPI<Player> api;

    public CmdSkinPreview(SkinsAPI<Player> api){
        this.api = api;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        Player player = ctx.get("player", null);

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
