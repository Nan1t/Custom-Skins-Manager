/*
 * Custom Skins Manager
 * Copyright (C) 2020  Nanit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
