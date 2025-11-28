package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

import java.util.List;

public class CommandListener implements Listener {

    private final Main plugin;

    public CommandListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-comandos", false)) {
            if (event.getPlayer().hasPermission("protecao.bypass.comandos")) {
                return;
            }

            List<String> comandosBloqueados = plugin.getConfig().getStringList("protecao.comandos-bloqueados");
            String comando = event.getMessage().toLowerCase().split(" ")[0];

            for (String comandoBloqueado : comandosBloqueados) {
                if (comando.equalsIgnoreCase(comandoBloqueado)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cVocê não tem permissão para usar este comando!");
                    return;
                }
            }
        }
    }
}
