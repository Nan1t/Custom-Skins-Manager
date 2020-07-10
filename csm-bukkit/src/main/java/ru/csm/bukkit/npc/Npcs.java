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

package ru.csm.bukkit.npc;

import ru.csm.api.logging.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public final class Npcs {

    private static final String NPC_CLASS = "ru.csm.bukkit.npc.NPC_%s";
    private static MethodHandle constructor;

    private Npcs(){}

    public static void init(String version){
        try{
            Class<?> npcClass = Class.forName(String.format(NPC_CLASS, version));
            Constructor<?> npcConstructor = npcClass.getConstructor();
            constructor = MethodHandles.lookup().unreflectConstructor(npcConstructor);
        } catch (Exception e){
            Logger.severe("Cannot load npc for spigot version %s: %s", version, e.getMessage());
        }
    }

    public static NPC create(){
        try{
            return (NPC) constructor.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create NPC: %s", t.getMessage());
        }
        return null;
    }
}
