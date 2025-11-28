package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

import java.util.List;

public class NickListener implements Listener {

    private final Main plugin;

    public NickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-nicks-improprios", false)) {
            List<String> nicksProibidos = plugin.getConfig().getStringList("protecao.nicks-proibidos");
            String playerName = event.getPlayer().getName().toLowerCase();

            for (String nickProibido : nicksProibidos) {
                if (playerName.contains(nickProibido.toLowerCase())) {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                            "§cSeu nick contém palavras proibidas!");
                    return;
                }
            }
        }
    }
}
