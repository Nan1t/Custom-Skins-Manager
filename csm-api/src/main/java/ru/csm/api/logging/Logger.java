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

package ru.csm.api.logging;

public final class Logger {

    private static LogHandler handler;

    private Logger(){}

    public static void set(LogHandler h){
        handler = h;
    }

    public static void info(String message, Object... args){
        handler.info(String.format(message, args));
    }

    public static void warning(String message, Object... args){
        handler.warning(String.format(message, args));
    }

    public static void severe(String message, Object... args){
        handler.severe(String.format(message, args));
    }

}
