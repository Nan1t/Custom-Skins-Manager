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
import ru.csm.api.storage.Language;

import java.util.List;

public class CommandSkin extends Command {

    private final String[] usage;

    public CommandSkin(Language lang){
        this.usage = lang.ofArray("help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(usage);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
