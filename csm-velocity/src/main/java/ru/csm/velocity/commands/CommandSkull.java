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
import net.kyori.text.TextComponent;
import ru.csm.api.storage.Language;
import ru.csm.velocity.command.CommandExecutor;

public class CommandSkull extends CommandExecutor {

    private final TextComponent usage;

    public CommandSkull(Language lang){
        this.usage = TextComponent.of(String.join("\n", lang.ofArray("help")));
    }

    @Override
    public void exec(CommandSource sender, String[] args) {
        sender.sendMessage(usage);
    }
}
