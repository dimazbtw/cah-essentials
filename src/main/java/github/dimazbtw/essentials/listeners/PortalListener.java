package github.dimazbtw.essentials.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class PortalListener implements Listener {

    private final Main plugin;

    public PortalListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-criar-portal", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo() != null) {
            World.Environment env = event.getTo().getWorld().getEnvironment();

            if (env == World.Environment.NETHER &&
                    plugin.getConfig().getBoolean("protecao.bloquear-teleport-nether", false)) {
                event.setCancelled(true);
            }

            if (env == World.Environment.THE_END &&
                    plugin.getConfig().getBoolean("protecao.bloquear-teleport-end", false)) {
                event.setCancelled(true);
            }
        }
    }
}
