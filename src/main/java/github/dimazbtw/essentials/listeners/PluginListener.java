package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

import java.util.List;

public class PluginListener implements Listener {

    private final Main plugin;

    public PluginListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-desabilitar-plugins", false)) {
            List<String> pluginsProtegidos = plugin.getConfig().getStringList("protecao.plugins-protegidos");
            String pluginName = event.getPlugin().getName();

            for (String protegido : pluginsProtegidos) {
                if (pluginName.equalsIgnoreCase(protegido)) {
                    plugin.getLogger().warning("Tentativa de desabilitar plugin protegido: " + pluginName);
                }
            }
        }
    }
}
