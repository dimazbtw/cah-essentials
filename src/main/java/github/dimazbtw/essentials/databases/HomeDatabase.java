package github.dimazbtw.essentials.databases;

import java.sql.*;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeDatabase {
    private final Connection connection;
    private final FileConfiguration config;

    public HomeDatabase(String path, FileConfiguration config) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.config = config;
        createDefaultConfig();
        createTable();
    }

    private void createDefaultConfig() {
        config.addDefault("homes.default", 3);
        config.addDefault("homes.vip", 5);
        config.addDefault("homes.mvp", 10);
        config.options().copyDefaults(true);
    }

    public int getHomeLimit(Player player) {
        int limit = config.getInt("homes.default");

        for (String key : config.getConfigurationSection("homes").getKeys(false)) {
            if (player.hasPermission("homes." + key)) {
                int permLimit = config.getInt("homes." + key);
                if (permLimit > limit) limit = permLimit;
            }
        }

        return limit;
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS homes (" +
                    "player_uuid TEXT," +
                    "home_name TEXT," +
                    "world TEXT," +
                    "x DOUBLE," +
                    "y DOUBLE," +
                    "z DOUBLE," +
                    "yaw FLOAT," +
                    "pitch FLOAT," +
                    "PRIMARY KEY (player_uuid, home_name)" +
                    ")");
        }
    }

    public void setHome(UUID playerUuid, String homeName, Location location) throws SQLException {
        String sql = "REPLACE INTO homes (player_uuid, home_name, world, x, y, z, yaw, pitch) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, homeName.toLowerCase());
            pstmt.setString(3, location.getWorld().getName());
            pstmt.setDouble(4, location.getX());
            pstmt.setDouble(5, location.getY());
            pstmt.setDouble(6, location.getZ());
            pstmt.setFloat(7, location.getYaw());
            pstmt.setFloat(8, location.getPitch());
            pstmt.executeUpdate();
        }
    }

    public Location getHome(UUID playerUuid, String homeName) throws SQLException {
        String sql = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, homeName.toLowerCase());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                );
            }
        }
        return null;
    }

    public void deleteHome(UUID playerUuid, String homeName) throws SQLException {
        String sql = "DELETE FROM homes WHERE player_uuid = ? AND home_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, homeName.toLowerCase());
            pstmt.executeUpdate();
        }
    }

    public Map<String, Location> getHomes(UUID playerUuid) throws SQLException {
        Map<String, Location> homes = new HashMap<>();
        String sql = "SELECT * FROM homes WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                homes.put(rs.getString("home_name"), new Location(
                        Bukkit.getWorld(rs.getString("world")),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z"),
                        rs.getFloat("yaw"),
                        rs.getFloat("pitch")
                ));
            }
        }
        return homes;
    }

    public int getHomeCount(UUID playerUuid) throws SQLException {
        String sql = "SELECT COUNT(*) FROM homes WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUuid.toString());
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1);
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

