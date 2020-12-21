package ru.csm.bungee.cmd;

import com.google.gson.JsonObject;
import napi.commands.CommandExecutor;
import napi.commands.exception.CommandException;
import napi.commands.parsed.CommandContext;
import napi.commands.parsed.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;

public class CmdSkullPlayer implements CommandExecutor {

    private final SkinsAPI<ProxiedPlayer> api;
    private final MessageSender<ProxiedPlayer> sender;

    public CmdSkullPlayer(SkinsAPI<ProxiedPlayer> api, MessageSender<ProxiedPlayer> sender){
        this.api = api;
        this.sender = sender;
    }

    @Override
    public void execute(CommandSender sender, CommandContext ctx) throws CommandException {
        if (!(sender.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = sender.getSender();
        ProxiedPlayer targetPlayer = ctx.<ProxiedPlayer>get("player").get();
        Head head = api.getPlayerHead(targetPlayer.getName());

        if (head != null){
            JsonObject message = new JsonObject();
            message.addProperty("player", player.getName());
            message.addProperty("url", head.getUrl());
            this.sender.sendMessage(player, Channels.SKULLS, message);
            return;
        }

        player.sendMessage(String.format(api.getLang().of("player.missing"), targetPlayer.getName()));
    }

}
