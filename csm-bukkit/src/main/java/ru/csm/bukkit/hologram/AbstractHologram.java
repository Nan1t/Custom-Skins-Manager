package ru.csm.bukkit.hologram;

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
