package ru.csm.velocity.commands;

import com.google.gson.JsonObject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

import java.util.Optional;

public class CommandSkullTo extends SubCommand {

    private final SkinsAPI<Player> api;
    private final ProxyServer server;
    private final MessageSender<Player> sender;

    public CommandSkullTo(SkinsAPI<Player> api, ProxyServer server, MessageSender<Player> sender){
        this.api = api;
        this.server = server;
        this.sender = sender;
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (args.length == 3){
            Optional<Player> target = server.getPlayer(args[0]);

            if (!target.isPresent() || !target.get().isActive()){
                sender.sendMessage(TextComponent.of(String.format("Player %s does not exist or offline", args[0])));
                return;
            }

            if (args[1].equalsIgnoreCase("from")){
                Head head = api.getPlayerHead(args[2]);

                if (head != null){
                    JsonObject message = new JsonObject();
                    message.addProperty("player", target.get().getUsername());
                    message.addProperty("url", head.getUrl());
                    this.sender.sendMessage(target.get(), Channels.SKULLS, message);
                    return;
                }

                sender.sendMessage(TextComponent.of(String.format(api.getLang().of("player.missing"), args[0])));
                return;
            } else if (args[1].equalsIgnoreCase("url")){
                JsonObject message = new JsonObject();
                message.addProperty("player", target.get().getUsername());
                message.addProperty("url", args[2]);
                this.sender.sendMessage(target.get(), Channels.SKULLS, message);
                return;
            }
        }

        sender.sendMessage(TextComponent.of("Wrong admin command format! Check the manual on SpigotMC plugin page"));
    }
}
