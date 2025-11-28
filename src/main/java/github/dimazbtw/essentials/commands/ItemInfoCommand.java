package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

import java.util.Map;

public class ItemInfoCommand {

    @Command(
            name = "iteminfo",
            description = "Mostra informações detalhadas do item",
            target = CommandTarget.PLAYER
    )
    public void itemInfo(Context<Player> context) {
        Player player = context.getSender();
        ItemStack item = player.getInventory().getItemInHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Segure um item para ver suas informações!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "=== Informações do Item ===");
        player.sendMessage(ChatColor.GOLD + "Material: " + ChatColor.WHITE + item.getType().name());
        player.sendMessage(ChatColor.GOLD + "Quantidade: " + ChatColor.WHITE + item.getAmount());
        player.sendMessage(ChatColor.GOLD + "Durabilidade: " + ChatColor.WHITE + item.getDurability());

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                player.sendMessage(ChatColor.GOLD + "Nome: " + ChatColor.WHITE + meta.getDisplayName());
            }

            if (meta.hasLore()) {
                player.sendMessage(ChatColor.GOLD + "Lore:");
                for (String line : meta.getLore()) {
                    player.sendMessage(ChatColor.WHITE + "  " + line);
                }
            }

            if (!meta.getEnchants().isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "Encantamentos:");
            }


            if (!meta.getItemFlags().isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "Item Flags: " + ChatColor.WHITE + meta.getItemFlags());
            }
        }

        // NBT Tags se disponível
        try {
            Object nmsItem = getNMSCopy(item);
            Object tag = getNBTTag(nmsItem);
            if (tag != null) {
                player.sendMessage(ChatColor.GOLD + "NBT Tags: " + ChatColor.WHITE + tag.toString());
            }
        } catch (Exception ignored) {}
    }

    private Object getNMSCopy(ItemStack item) throws Exception {
        Class<?> craftItemStack = Class.forName(
                "org.bukkit.craftbukkit." + getServerVersion() + ".inventory.CraftItemStack"
        );
        return craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
    }

    private Object getNBTTag(Object nmsItem) throws Exception {
        // Replace this method with the appropriate reflection method for older versions.
        return nmsItem.getClass().getMethod("getTag").invoke(nmsItem);
    }

    private String getServerVersion() {
        return org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}

