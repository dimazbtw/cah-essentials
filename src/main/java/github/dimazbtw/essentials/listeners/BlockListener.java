package github.dimazbtw.essentials.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class BlockListener implements Listener {

    private final Main plugin;

    public BlockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Material type = event.getBlock().getType();

        if (plugin.getConfig().getBoolean("mundo.desativar-queda-areia", false)) {
            if (type == Material.SAND || type == Material.RED_SAND || type == Material.GRAVEL) {
                event.setCancelled(true);
            }
        }

        if (plugin.getConfig().getBoolean("mundo.desativar-queda-bigorna", false)) {
            if (type == Material.ANVIL || type == Material.CHIPPED_ANVIL || type == Material.DAMAGED_ANVIL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (plugin.getConfig().getBoolean("mundo.desativar-queda-folhas", false)) {
            event.setCancelled(true);
        }
    }
}
