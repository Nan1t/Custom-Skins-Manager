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

package ru.csm.bukkit.handler;

import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;
import ru.csm.bukkit.util.BukkitTasks;

public interface SkinHandler {

    Skin getSkin(Player player);

    void applySkin(Player player, Skin skin);

    void updateSkin(Player player);

    default void updateData(Player player){
        BukkitTasks.runTask(()->{
            player.updateInventory();
            player.setExp(player.getExp());
            player.setLevel(player.getLevel());
            player.setHealth(player.getHealth());
            player.setFlying(player.isFlying());
            player.setPlayerListName(player.getPlayerListName());
        });
    }
}
