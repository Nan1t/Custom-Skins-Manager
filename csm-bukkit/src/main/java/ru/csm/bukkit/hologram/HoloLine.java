package ru.csm.bukkit.hologram;

import org.bukkit.Location;

public class HoloLine {

    private int id;
    private Location location;
    private final String text;

    public HoloLine(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }
}
