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
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import ru.csm.api.player.Skin;
import ru.csm.api.services.SkinsAPI;
import ru.csm.velocity.command.SubCommand;

import java.util.Optional;

public class CommandSkinPreview extends SubCommand {

    private final SkinsAPI<Player> api;
    private final ProxyServer server;
    private final TextComponent usage;

    public CommandSkinPreview(SkinsAPI<Player> api, ProxyServer server){
        this.api = api;
        this.server = server;
        this.usage = TextComponent.of(String.format(api.getLang().of("command.usage"),
                "/skin preview <player> <texture> <signature> [permission]"));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        if (args.length >= 3){
            Optional<Player> player = server.getPlayer(args[0]);
            String texture = args[1];
            String signature = args[2];
            String permission = null;

            if (!player.isPresent() || !player.get().isActive()){
                sender.sendMessage(TextComponent.of("Player is offline"));
                return;
            }

            if (args.length == 4) permission = args[3];

            api.showPreview(player.get(), new Skin(texture, signature), false, permission);
            return;
        }

        sender.sendMessage(usage);
    }
}
