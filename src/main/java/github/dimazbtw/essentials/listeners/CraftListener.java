package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class CraftListener implements Listener {

    private final Main plugin;

    public CraftListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-crafts", false)) {
            event.setCancelled(true);
        }
    }
}
