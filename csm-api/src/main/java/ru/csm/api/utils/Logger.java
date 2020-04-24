/*
 * SafeLogin
 *     Copyright (C) 2020  Nanit
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.csm.api.utils;

public final class Logger {

    private static java.util.logging.Logger logger;

    private Logger(){}

    public static void set(java.util.logging.Logger log){
        logger = log;
    }

    public static void info(String message){
        logger.info(message);
    }

    public static void info(String message, Object... args){
        logger.info(String.format(message, args));
    }

    public static void warning(String message){
        logger.warning(message);
    }

    public static void warning(String message, Object... args){
        logger.warning(String.format(message, args));
    }

    public static void severe(String message){
        logger.severe(message);
    }

    public static void severe(String message, Object... args){
        logger.severe(String.format(message, args));
    }

}
