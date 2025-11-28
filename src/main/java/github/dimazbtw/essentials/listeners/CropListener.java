package github.dimazbtw.essentials.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class CropListener implements Listener {

    private final Main plugin;

    public CropListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-quebrar-plantacoes-pulando", false)) {
            Material type = event.getBlock().getType();

            if (type == Material.WHEAT || type == Material.CARROTS ||
                    type == Material.POTATOES || type == Material.BEETROOTS ||
                    type == Material.FARMLAND) {

                if (!event.getPlayer().isSneaking()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onCropTrample(PlayerInteractEvent event) {
        if (plugin.getConfig().getBoolean("protecao.bloquear-quebrar-plantacoes-pulando", false)) {
            if (event.getAction() == Action.PHYSICAL) {
                if (event.getClickedBlock() != null &&
                        event.getClickedBlock().getType() == Material.FARMLAND) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
