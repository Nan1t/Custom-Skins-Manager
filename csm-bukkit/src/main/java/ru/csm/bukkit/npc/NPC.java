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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.csm.api.player.Skin;

import java.util.List;

public interface NPC {

    int getId();

    String getName();

    void setName(String name);

    Location getLocation();

    void setLocation(Location location);

    Skin getSkin();

    void setSkin(Skin skin);

    String getPermission();

    void setPermission(String permission);

    boolean isOpenMenu();

    void setOpenMenu(boolean openMenu);

    void setDisplayName(List<String> name);

    void spawn(Player player);

    void destroy(Player player);

}
