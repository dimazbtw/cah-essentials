package github.dimazbtw.essentials.models;

import org.bukkit.Location;

public class WarpModel {
    private String name;
    private Location location;

    public WarpModel(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() { return name; }
    public Location getLocation() { return location; }
}
