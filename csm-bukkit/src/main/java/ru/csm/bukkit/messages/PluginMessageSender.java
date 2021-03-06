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

package ru.csm.bukkit.messages;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.csm.api.network.MessageSender;

public class PluginMessageSender extends MessageSender<Player> {

    private final Plugin plugin;

    public PluginMessageSender(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void send(Player player, String channel, byte[] data) {
        player.sendPluginMessage(plugin, channel, data);
    }
}
