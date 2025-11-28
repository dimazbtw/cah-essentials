package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

import java.util.List;

public class SignListener implements Listener {

    private final Main plugin;

    public SignListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-palavras-placas", false)) {
            List<String> palavrasProibidas = plugin.getConfig().getStringList("protecao.palavras-proibidas");

            for (String linha : event.getLines()) {
                for (String palavraProibida : palavrasProibidas) {
                    if (linha.toLowerCase().contains(palavraProibida.toLowerCase())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("§cVocê não pode usar essa palavra em placas!");
                        return;
                    }
                }
            }
        }
    }
}
