package github.dimazbtw.essentials.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

import java.util.List;

public class HungerListener implements Listener {

    private final Main plugin;

    public HungerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (plugin.getConfig().getBoolean("mundo.desativar-fome", false)) {
                List<String> mundosSemFome = plugin.getConfig().getStringList("mundo.mundos-sem-fome");

                if (mundosSemFome.contains(player.getWorld().getName())) {
                    event.setCancelled(true);
                    player.setFoodLevel(20);
                }
            }
        }
    }
}
