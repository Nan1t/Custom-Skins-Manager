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

package ru.csm.bukkit.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;

import java.util.Collections;
import java.util.List;

public class CommandSkinPlayer extends Command {

    private final SkinsAPI<Player> api;
    private final String usage;

    public CommandSkinPlayer(SkinsAPI<Player> api){
        this.api = api;
        this.usage = String.format(api.getLang().of("command.usage"), "/skin player <player>");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length == 1){
            SkinPlayer player = api.getPlayer(sender.getName());
            if(player != null){
                api.setSkinFromName(player, args[0]);
            }
            return;
        }

        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Collections.singletonList("<player>");
    }
}
