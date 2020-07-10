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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class SubCommand implements CommandHandler {

    private String permission;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

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
    public SubCommand getSub(String arg){
        return subCommands.get(arg);
    }
}
