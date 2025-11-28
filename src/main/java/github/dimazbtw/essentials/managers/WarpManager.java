package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.WarpModel;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WarpManager {
    private final Main plugin;
    private final Map<String, WarpModel> warps;
    private final File warpsFolder;

    public WarpManager(Main plugin) {
        this.plugin = plugin;
        this.warps = new HashMap<>();
        this.warpsFolder = new File(plugin.getDataFolder(), "warps");
        if (!warpsFolder.exists()) warpsFolder.mkdirs();
        loadWarps();
    }

    private void loadWarps() {
        File[] files = warpsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String warpName = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            double x = config.getDouble("location.x");
            double y = config.getDouble("location.y");
            double z = config.getDouble("location.z");
            float yaw = (float) config.getDouble("location.yaw");
            float pitch = (float) config.getDouble("location.pitch");
            String worldName = config.getString("location.world");

            if (worldName != null) {
                Location loc = new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
                warps.put(warpName.toLowerCase(), new WarpModel(warpName, loc));
            }
        }
    }

    public void saveWarp(String name, Location location) {
        File warpFile = new File(warpsFolder, name + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("location", location);

        try {
            config.save(warpFile);
            warps.put(name.toLowerCase(), new WarpModel(name, location));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteWarp(String name) {
        File warpFile = new File(warpsFolder, name + ".yml");
        if (warpFile.exists()) {
            warpFile.delete();
            warps.remove(name.toLowerCase());
        }
    }

    public Optional<WarpModel> getWarp(String name) {
        return Optional.ofNullable(warps.get(name.toLowerCase()));
    }

    public Map<String, WarpModel> getWarps() {
        return new HashMap<>(warps);
    }
}

