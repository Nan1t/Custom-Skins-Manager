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

package ru.csm.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;

public abstract class CommandExecutor extends Command implements CommandHandler {

    private String permission;
    private final Map<String, CommandHandler> subCommands = new HashMap<>();

    public CommandExecutor(String name, String permission, String... aliases){
        super(name, permission, aliases);
    }

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
    public void execute(CommandSender sender, String[] args) {
        executeSubCommands(sender, this, args);
    }

    private void executeSubCommands(CommandSender sender, CommandHandler parent, String[] args){
        for (String arg : args){
            CommandHandler cmd = parent.getSub(arg);
            if (cmd != null){
                executeSubCommands(sender, cmd, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        if(!checkPermission(sender, parent)){
            sender.sendMessage(TextComponent.fromLegacyText("You don't have permission to that"));
            return;
        }

        parent.exec(sender, args);
    }
}
