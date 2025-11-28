package github.dimazbtw.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import github.dimazbtw.essentials.Main;

public class WorldListener implements Listener {

    private final Main plugin;

    public WorldListener(Main plugin) {
        this.plugin = plugin;
        iniciarTarefas();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        configurarMundo(event.getWorld());
    }

    private void iniciarTarefas() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    configurarMundo(world);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void configurarMundo(World world) {
        if (plugin.getConfig().getBoolean("mundo.desativar-ciclo-dia", false)) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(6000); // Define para meio-dia (sempre de dia)
        }
    }
}
