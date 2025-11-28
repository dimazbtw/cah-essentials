package github.dimazbtw.essentials.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class PlayerListener implements Listener {

    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("mensagens.desativar-mensagem-entrada", false)) {
            event.setJoinMessage(null);
        }

        Location spawn = plugin.getLocationManager().getLocation("spawn");
        event.getPlayer().teleport(spawn);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getConfig().getBoolean("mensagens.desativar-mensagem-saida", false)) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.getConfig().getBoolean("mensagens.desativar-mensagem-morte", false)) {
            event.setDeathMessage(null);
        }
        Location spawn = plugin.getLocationManager().getLocation("spawn");
        event.getEntity().teleport(spawn);
    }
}
