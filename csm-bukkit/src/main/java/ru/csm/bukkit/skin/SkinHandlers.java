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

package ru.csm.bukkit.skin;

import ru.csm.api.logging.Logger;
import ru.csm.bukkit.skin.nms.*;

public final class SkinHandlers {

    private static SkinHandler handler;

    private SkinHandlers(){}

    public static SkinHandler getHandler(){
        return handler;
    }

    public static void init(String version){
        switch (version){
            default:
                Logger.severe("Unsupported server version: %s. Skin handler not loaded!", version);
                break;
            case "v1_8_R3":
                handler = new Handler_v1_8_R3();
                break;
            case "v1_9_R2":
                handler = new Handler_v1_9_R2();
                break;
            case "v1_10_R1":
                handler = new Handler_v1_10_R1();
                break;
            case "v1_11_R1":
                handler = new Handler_v1_11_R1();
                break;
            case "v1_12_R1":
                handler = new Handler_v1_12_R1();
                break;
            case "v1_13_R2":
                handler = new Handler_v1_13_R2();
                break;
            case "v1_14_R1":
                handler = new Handler_v1_14_R1();
                break;
            case "v1_15_R1":
                handler = new Handler_v1_15_R1();
                break;
            case "v1_16_R1":
                handler = new Handler_v1_16_R1();
                break;
            case "v1_16_R2":
                handler = new Handler_v1_16_R2();
                break;
            case "v1_16_R3":
                handler = new Handler_v1_16_R3();
                break;
        }
    }
}
