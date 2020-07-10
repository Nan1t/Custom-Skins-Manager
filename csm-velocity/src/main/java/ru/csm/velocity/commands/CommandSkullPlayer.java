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
import net.kyori.text.TextComponent;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.player.Head;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkullPlayer extends SubCommand {

    private final SkinsAPI<Player> api;
    private final MessageSender<Player> sender;
    private final TextComponent usage;

    public CommandSkullPlayer(SkinsAPI<Player> api, MessageSender<Player> sender){
        this.api = api;
        this.sender = sender;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skull player <player>"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof Player){
                Player player = (Player) sender;
                Head head = api.getPlayerHead(args[0]);

                if (head != null){
                    JsonObject message = new JsonObject();
                    message.addProperty("player", player.getUsername());
                    message.addProperty("url", head.getUrl());
                    this.sender.sendMessage(player, Channels.SKULLS, message);
                    return;
                }

                sender.sendMessage(TextComponent.of(String.format(api.getLang().of("player.missing"), args[0])));
            }

            return;
        }

        sender.sendMessage(usage);
    }
}
