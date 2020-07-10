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

package ru.csm.api.player;

import java.util.UUID;

public interface SkinPlayer {

    /**
     * Get player UUID
     * @return UUID object
     * */
    UUID getUUID();

    /**
     * Get player name
     * @return String name
     * */
    String getName();

    /**
     * Set default (instead lisense) skin for player. Real license skin will not change
     * @return Object of player default (license or custom default) skin.
     * */
    Skin getDefaultSkin();

    /**
     * Set default (instead license) skin for player. Real license skin will not change
     * @return Object of player custom skin, or null if player not have custom skin
     * */
    Skin getCustomSkin();

    /**
     * Set default (instead license) skin for player. Real license skin will not change
     * @param skin - Skin object with value and signature
     * */
    void setDefaultSkin(Skin skin);

    /**
     * Set custom skin for player
     * @param skin - Skin object with value and signature
     * */
    void setCustomSkin(Skin skin);

    /**
     * Apply skin data to player. Calling before refreshing
     * */
    void applySkin();

    /**
     * Realtime refreshing player skin
     * */
    void refreshSkin();

    /**
     * Return player default (license or custom default) skin
     * */
    void resetSkin();

    /**
    * Send message to player
    * @param message - String message
    * */
    void sendMessage(String... message);

    /**
     * @return true if player online
     * */
    boolean isOnline();

    /**
     * @return true if player have custom skin
     * */
    boolean hasCustomSkin();

    /**
     * @return true is player has specified permission
     */
    boolean hasPermission(String permission);
}
