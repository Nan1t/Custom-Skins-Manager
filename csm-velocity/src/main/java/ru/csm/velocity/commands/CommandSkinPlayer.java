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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.text.TextComponent;
import ru.csm.api.player.SkinPlayer;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

public class CommandSkinPlayer extends SubCommand {

    private final SkinsAPI<Player> api;
    private final TextComponent usage;

    public CommandSkinPlayer(SkinsAPI<Player> api){
        this.api = api;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skin player <player>"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length == 1){
            SkinPlayer player = api.getPlayer(((Player)sender).getUsername());
            if(player != null){
                api.setSkinFromName(player, args[0]);
            }
            return;
        }

        sender.sendMessage(usage);
    }
}
