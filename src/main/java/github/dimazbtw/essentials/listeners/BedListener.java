package github.dimazbtw.essentials.listeners;

import github.dimazbtw.essentials.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BedListener implements Listener {

    private final Main plugin;

    public BedListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-deitar-camas", false)) {
            event.setCancelled(true);
        }
    }
}
