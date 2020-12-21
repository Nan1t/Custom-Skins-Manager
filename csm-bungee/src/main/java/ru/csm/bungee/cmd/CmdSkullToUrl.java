package ru.csm.bungee.cmd;

import com.google.gson.JsonObject;
import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;

public class CmdSkullToUrl implements CommandExecutor {

    private final MessageSender<ProxiedPlayer> sender;

    public CmdSkullToUrl(MessageSender<ProxiedPlayer> sender){
        this.sender = sender;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        ProxiedPlayer player = ctx.<ProxiedPlayer>get("target").get();
        String url = ctx.getString("url");

        JsonObject message = new JsonObject();
        message.addProperty("player", player.getName());
        message.addProperty("url", url);
        this.sender.sendMessage(player, Channels.SKULLS, message);
    }

}
