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

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class CommandExecutor implements Command, CommandHandler {

    private String permission;
    private final Map<String, CommandHandler> subCommands = new HashMap<>();

    @Override
    public Set<String> getRegisteredSubKeys(){
        return subCommands.keySet();
    }

    @Override
    public String getPermission(){
        return permission;
    }

    @Override
    public void setPermission(String permission){
        this.permission = permission;
    }

    @Override
    public void addSub(SubCommand command, String arg, String... aliases){
        subCommands.put(arg, command);
        for (String a : aliases){
            subCommands.put(a, command);
        }
    }

    @Override
    public CommandHandler getSub(String arg){
        return subCommands.get(arg);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        executeSubCommands(sender, this, args);
    }

    private void executeSubCommands(CommandSource sender, CommandHandler parent, String[] args){
        if(!checkPermission(sender, parent)){
            sender.sendMessage(TextComponent.of("You don't have permission to that"));
            return;
        }

        for (String arg : args) {
            CommandHandler cmd = parent.getSub(arg);
            if (cmd != null) {
                executeSubCommands(sender, cmd, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        parent.exec(sender, args);
    }
}
