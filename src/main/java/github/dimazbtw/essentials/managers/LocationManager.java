package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LocationManager {
    private final Main plugin;
    private FileConfiguration locsConfig;
    private File locsFile;

    public LocationManager(Main plugin) {
        this.plugin = plugin;
        loadLocsFile();
    }

    private void loadLocsFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        locsFile = new File(plugin.getDataFolder(), "locs.yml");
        if (!locsFile.exists()) {
            try {
                locsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        locsConfig = YamlConfiguration.loadConfiguration(locsFile);
    }

    public void saveLocation(String name, Location location) {
        locsConfig.set(name + ".world", location.getWorld().getName());
        locsConfig.set(name + ".x", location.getX());
        locsConfig.set(name + ".y", location.getY());
        locsConfig.set(name + ".z", location.getZ());
        locsConfig.set(name + ".yaw", location.getYaw());
        locsConfig.set(name + ".pitch", location.getPitch());

        try {
            locsConfig.save(locsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String name) {
        if (!locsConfig.contains(name)) {
            return null;
        }

        String world = locsConfig.getString(name + ".world");
        double x = locsConfig.getDouble(name + ".x");
        double y = locsConfig.getDouble(name + ".y");
        double z = locsConfig.getDouble(name + ".z");
        float yaw = (float) locsConfig.getDouble(name + ".yaw");
        float pitch = (float) locsConfig.getDouble(name + ".pitch");

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public void reload() {
        loadLocsFile();
    }
}
