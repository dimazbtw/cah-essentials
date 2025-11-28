package github.dimazbtw.essentials.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;

public class VoidListener implements Listener {

    private final Main plugin;

    public VoidListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-cair-void", false)) {
            if (event.getEntity() instanceof Player &&
                    event.getCause() == EntityDamageEvent.DamageCause.VOID) {

                event.setCancelled(true);
                Player player = (Player) event.getEntity();

                Location spawn = plugin.getLocationManager().getLocation("spawn");

                if (spawn != null) {
                    // Teleporta para o spawn na thread principal
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.teleport(spawn);
                        player.sendMessage(plugin.getLangManager().getMessage("spawn.teleported-void"));
                    });
                } else {
                    // Se não houver spawn definido, teleporta para o spawn do mundo
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Location worldSpawn = player.getWorld().getSpawnLocation();
                        player.teleport(worldSpawn);
                        player.sendMessage(plugin.getLangManager().getMessage("spawn.teleported-void"));
                    });
                }
            }
        }
    }
}