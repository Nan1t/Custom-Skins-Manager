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

package ru.csm.bukkit.nms.hologram;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHologram implements Hologram {

    private static final double LINE_HEIGHT = 0.25;

    private Location location;
    private final List<HoloLine> lines = new ArrayList<>();

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    protected List<HoloLine> getLines(){
        return lines;
    }

    @Override
    public void setLines(List<String> lines) {
        for (String text : lines){
            HoloLine line = new HoloLine(text);
            this.lines.add(line);
        }

        int index = this.lines.size();
        for (HoloLine line : this.lines){
            line.setLocation(location.clone().add(0,LINE_HEIGHT * index,0));
            index--;
        }
    }
}
