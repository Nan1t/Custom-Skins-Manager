package ru.csm.velocity.cmd;

import com.google.gson.JsonObject;
import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import com.velocitypowered.api.proxy.Player;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;

public class CmdSkullToFrom implements CommandExecutor {

    private final SkinsAPI<Player> api;
    private final MessageSender<Player> sender;

    public CmdSkullToFrom(SkinsAPI<Player> api, MessageSender<Player> sender){
        this.api = api;
        this.sender = sender;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        Player player = ctx.<Player>get("target").get();
        Player targetPlayer = ctx.<Player>get("username").get();

        Head head = api.getPlayerHead(targetPlayer.getName());

        if (head != null){
            JsonObject message = new JsonObject();
            message.addProperty("player", player.getName());
            message.addProperty("url", head.getUrl());
            this.sender.sendMessage(player, Channels.SKULLS, message);
            return;
        }

        sender.sendMessage(String.format(api.getLang().of("player.missing"), targetPlayer.getName()));
    }

}
