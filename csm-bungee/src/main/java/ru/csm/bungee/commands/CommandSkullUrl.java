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

package ru.csm.bungee.commands;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.network.Channels;
import ru.csm.api.network.MessageSender;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkullUrl extends SubCommand {

    private final String usage;

    private final MessageSender<ProxiedPlayer> sender;

    public CommandSkullUrl(SkinsAPI<ProxiedPlayer> api, MessageSender<ProxiedPlayer> sender){
        this.sender = sender;
        this.usage = String.format(api.getLang().of("command.usage"), "/skull url <url>");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (args.length == 1){
            if (sender instanceof ProxiedPlayer){
                ProxiedPlayer player = (ProxiedPlayer) sender;
                JsonObject message = new JsonObject();
                message.addProperty("player", player.getName());
                message.addProperty("url", args[0]);
                this.sender.sendMessage(player, Channels.SKULLS, message);
            }
            return;
        }

        sender.sendMessage(usage);
    }
}
