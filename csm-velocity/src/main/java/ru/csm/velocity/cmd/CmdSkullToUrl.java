package ru.csm.velocity.cmd;

import com.google.gson.JsonObject;
import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;

public class CmdSkullToUrl implements CommandExecutor {

    private final MessageSender<Player> sender;

    public CmdSkullToUrl(MessageSender<Player> sender){
        this.sender = sender;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        Player player = ctx.<Player>get("target").get();
        String url = ctx.getString("url");

        JsonObject message = new JsonObject();
        message.addProperty("player", player.getUsername());
        message.addProperty("url", url);
        this.sender.sendMessage(player, Channels.SKULLS, message);
    }

}
