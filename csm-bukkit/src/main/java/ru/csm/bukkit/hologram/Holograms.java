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

package ru.csm.bukkit.hologram;

import ru.csm.api.logging.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public final class Holograms {

    private static final String HOLOGRAM_CLASS = "ru.csm.bukkit.hologram.hologram.Hologram_%s";
    private static MethodHandle constructor;

    private Holograms(){}

    public static Hologram create(){
        try{
            return (Hologram)constructor.invoke();
        } catch (Throwable t){
            Logger.severe("Cannot create new hologram: %s", t.getMessage());
        }
        return null;
    }

    public static void init(String version){
        try{
            Class<?> holoClass = Class.forName(String.format(HOLOGRAM_CLASS, version));
            Constructor<?> holoConstructor = holoClass.getConstructor();
            constructor = MethodHandles.lookup().unreflectConstructor(holoConstructor);
        } catch (Exception e){
            Logger.severe("Cannot initialize hologram class for spigot version %s: %s", version, e.getMessage());
        }
    }

}
