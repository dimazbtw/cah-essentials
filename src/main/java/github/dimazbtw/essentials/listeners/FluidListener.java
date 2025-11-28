package github.dimazbtw.essentials.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class FluidListener implements Listener {

    private final Main plugin;

    public FluidListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFluidFlow(BlockFromToEvent event) {
        Material type = event.getBlock().getType();

        if (plugin.getConfig().getBoolean("mundo.desativar-agua-escorrer", false)) {
            if (type == Material.WATER) {
                event.setCancelled(true);
            }
        }

        if (plugin.getConfig().getBoolean("mundo.desativar-lava-escorrer", false)) {
            if (type == Material.LAVA) {
                event.setCancelled(true);
            }
        }
    }
}
