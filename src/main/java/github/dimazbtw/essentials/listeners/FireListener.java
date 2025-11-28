package github.dimazbtw.essentials.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class FireListener implements Listener {

    private final Main plugin;

    public FireListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (plugin.getConfig().getBoolean("mundo.desativar-fogo-espalhar", false)) {
            if (event.getSource().getType() == Material.FIRE) {
                event.setCancelled(true);
            }
        }
    }
}
