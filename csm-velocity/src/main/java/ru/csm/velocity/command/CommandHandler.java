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

package ru.csm.velocity.command;

import com.velocitypowered.api.command.CommandSource;

import java.util.Set;

public interface CommandHandler {

    Set<String> getRegisteredSubKeys();

    String getPermission();

    void setPermission(String permission);

    void addSub(SubCommand command, String arg, String... aliases);

    CommandHandler getSub(String arg);

    void exec(CommandSource sender, String[] args);

    default boolean checkPermission(CommandSource sender, CommandHandler command){
        return (command.getPermission() == null) || sender.hasPermission(command.getPermission());
    }

}
