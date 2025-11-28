package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class ItemListener implements Listener {

    private final Main plugin;

    public ItemListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-explodir-itens", false)) {
            if (event.getEntity() instanceof Item) {
                if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
