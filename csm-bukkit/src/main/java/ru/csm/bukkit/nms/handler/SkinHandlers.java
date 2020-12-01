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

package ru.csm.bukkit.nms.handler;

import ru.csm.api.logging.Logger;

import java.lang.reflect.Constructor;

public final class SkinHandlers {

    private static final String HANDLER_TEMPLATE = "ru.csm.bukkit.nms.handler.Handler_%s";
    private static SkinHandler handler;

    private SkinHandlers(){}

    public static SkinHandler getHandler(){
        return handler;
    }

    public static void init(String version){
        try{
            Class<?> handlerClass = Class.forName(String.format(HANDLER_TEMPLATE, version));
            Constructor<?> handlerConstructor = handlerClass.getConstructor();
            handler = (SkinHandler) handlerConstructor.newInstance();
            Logger.info("Loaded skin handler for spigot version %s", version);
        } catch (Exception e){
            Logger.severe("Cannot load skin handler for spigot version " + version);
        }
    }
}
