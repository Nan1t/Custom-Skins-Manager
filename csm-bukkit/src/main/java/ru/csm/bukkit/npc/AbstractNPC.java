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
import ru.csm.bukkit.hologram.Hologram;
import ru.csm.bukkit.hologram.Holograms;
import ru.csm.bukkit.services.NpcManager;

import java.util.List;

public abstract class AbstractNPC implements NPC {

    protected int id;
    protected String name;
    protected Location location;
    protected Skin skin;
    protected Hologram hologram;

    private String permission;
    private boolean openMenu;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;

        if (hologram != null){
            hologram.setLocation(location.clone().subtract(0,0.35,0));
        }
    }

    @Override
    public Skin getSkin() {
        return skin;
    }

    @Override
    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean isOpenMenu() {
        return openMenu;
    }

    @Override
    public void setOpenMenu(boolean openMenu) {
        this.openMenu = openMenu;
    }

    @Override
    public void setDisplayName(List<String> name) {
        hologram = Holograms.create();

        if (hologram != null){
            hologram.setLocation(location.clone().subtract(0,0.35,0));
            hologram.setLines(name);
        }
    }

    protected byte getFixRotation(float yawPitch){
        return (byte) ((int) (yawPitch * 256.0F / 360.0F));
    }

    @Override
    public void spawn(Player player) {
        NpcManager.addNpc(player, this);
    }
}
