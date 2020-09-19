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

package ru.csm.bukkit.util;

import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;

public final class ProxyUtil {

    private ProxyUtil(){}

    public static boolean isUseProxy(){
        return isUseBungee() || isUseVelocity();
    }

    private static boolean isUseVelocity(){
        try {
            Class<?> paperConfigClass = Class.forName("com.destroystokyo.paper.PaperConfig");
            Field velocitySupportField = paperConfigClass.getField("velocitySupport");
            return velocitySupportField.getBoolean(null);
        } catch (Exception e){
            return false;
        }
    }

    private static boolean isUseBungee(){
        return SpigotConfig.bungee;
    }

}
