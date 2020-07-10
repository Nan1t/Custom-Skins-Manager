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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.csm.api.player.SkinModel;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.bungee.command.SubCommand;

public class CommandSkinUrl extends SubCommand {

    private final SkinsAPI<ProxiedPlayer> api;
    private final String usage;

    public CommandSkinUrl(SkinsAPI<ProxiedPlayer> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin url <url> [slim]");
    }

    @Override
    public void exec(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;

        SkinPlayer player = api.getPlayer(sender.getName());
        SkinModel model = SkinModel.STEVE;

        if(player != null){
            if (args.length > 0){
                if (args.length == 2 && args[1].equals("slim")) model = SkinModel.ALEX;
                api.setSkinFromImage(player, args[0], model);
                return;
            }
        }

        sender.sendMessage(usage);
    }
}
