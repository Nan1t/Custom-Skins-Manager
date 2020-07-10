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

package ru.csm.velocity.message;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import ru.csm.api.network.MessageSender;

public class PluginMessageSender extends MessageSender<Player> {

    @Override
    public void send(Player player, String channel, byte[] data) {
        if (player.getCurrentServer().isPresent()){
            String[] arr = channel.split(":");
            ChannelIdentifier identifier = MinecraftChannelIdentifier.create(arr[0], arr[1]);
            player.getCurrentServer().get().sendPluginMessage(identifier, data);
        }
    }

}
