package github.dimazbtw.essentials.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class MobListener implements Listener {

    private final Main plugin;

    public MobListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobCombust(EntityCombustEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-mobs-pegarem-fogo", false)) {
            if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (plugin.getConfig().getBoolean("mundo.desativar-mobs-naturais", false)) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
                event.setCancelled(true);
            }
        }
    }
}
