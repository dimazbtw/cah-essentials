package github.dimazbtw.essentials.listeners;

import github.dimazbtw.essentials.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import github.dimazbtw.essentials.Main;
import org.bukkit.inventory.Inventory;

public class ContainerListener implements Listener {

    private final Main plugin;

    public ContainerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onContainerOpen(InventoryOpenEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-abrir-containers", false)) {
            // Não bloquear inventários customizados (menus)
            Inventory inv = event.getInventory();

            // Se for um inventário de holder null, provavelmente é um menu customizado
            if (inv.getHolder() == null) {
                return; // Permite abrir
            }

            // Verifica se o título do inventário contém cores (indicativo de menu customizado)
            String title = event.getView().getTitle();
            if (title.contains("§") || title.contains("&")) {
                return; // Permite abrir menus coloridos
            }

            // Bloqueia containers normais
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onContainerShiftClick(InventoryClickEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-shift-containers", false)) {
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
            }
        }
    }
}
