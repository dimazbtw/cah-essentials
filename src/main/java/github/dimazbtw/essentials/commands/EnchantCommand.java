package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import github.dimazbtw.essentials.Main;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnchantCommand {
    private final Main plugin;

    public EnchantCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "enchant",
            description = "Encanta o item na mão sem limite de nível",
            usage = "/enchant <encantamento> <nível>",
            target = CommandTarget.PLAYER,
            permission = "essentials.enchant"
    )
    public void enchant(Context<Player> context, String[] args) {
        Player player = context.getSender();

        if (args.length < 2) {
            player.sendMessage("§cUso: /enchant <encantamento> <nível>");
            player.sendMessage("§7Exemplo: /enchant DAMAGE_ALL 10");
            player.sendMessage("§7Use /enchant list para ver todos os encantamentos");
            return;
        }

        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cVocê precisa ter um item na mão!");
            return;
        }

        String enchantName = args[0].toUpperCase();

        Enchantment enchantment = getEnchantmentByName(enchantName);

        if (enchantment == null) {
            player.sendMessage("§cEncantamento não encontrado: §f" + enchantName);
            player.sendMessage("§7Use /enchant list para ver todos os encantamentos");
            return;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cNível inválido! Use um número inteiro.");
            return;
        }

        if (level < 1) {
            player.sendMessage("§cO nível deve ser maior que 0!");
            return;
        }

        if (level > 32767) {
            player.sendMessage("§cO nível máximo é 32767!");
            return;
        }

        // Aplicar encantamento sem verificar compatibilidade
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);

            player.sendMessage("§aEncantamento aplicado com sucesso!");
            player.sendMessage("§7" + getEnchantmentDisplayName(enchantment) + " §f" + level);
        } else {
            player.sendMessage("§cErro ao aplicar encantamento!");
        }
    }

    @Command(
            name = "enchant.lista",
            description = "Encanta o item na mão sem limite de nível",
            usage = "/enchant <encantamento> <nível>",
            target = CommandTarget.PLAYER,
            permission = "essentials.enchant"
    )
    public void  listEnchantments(Context<Player> context) {
        Player player = context.getSender();

        player.sendMessage("");
        player.sendMessage("§6§l⚡ Lista de Encantamentos:");
        player.sendMessage("");

        StringBuilder armas = new StringBuilder("§e§lArmas: §7");
        StringBuilder armaduras = new StringBuilder("§b§lArmaduras: §7");
        StringBuilder ferramentas = new StringBuilder("§a§lFerramentas: §7");
        StringBuilder arco = new StringBuilder("§c§lArco: §f");
        StringBuilder outros = new StringBuilder("§7§lOutros: §f");

        for (Enchantment ench : Enchantment.values()) {
            String name = ench.getName();

            if (name.contains("DAMAGE") || name.contains("KNOCKBACK") || name.contains("FIRE_ASPECT") || name.contains("LOOT_BONUS_MOBS")) {
                armas.append(name).append(", ");
            } else if (name.contains("PROTECTION") || name.contains("THORNS") || name.contains("OXYGEN") || name.contains("WATER_WORKER")) {
                armaduras.append(name).append(", ");
            } else if (name.contains("DIG") || name.contains("LOOT_BONUS_BLOCKS") || name.contains("SILK_TOUCH")) {
                ferramentas.append(name).append(", ");
            } else if (name.contains("ARROW") || name.contains("PUNCH") || name.contains("FLAME") || name.equals("INFINITY")) {
                arco.append(name).append(", ");
            } else {
                outros.append(name).append("§f,§7 ");
            }
        }

        player.sendMessage(armas.substring(0, armas.length() - 2));
        player.sendMessage(armaduras.substring(0, armaduras.length() - 2));
        player.sendMessage(ferramentas.substring(0, ferramentas.length() - 2));
        player.sendMessage(arco.substring(0, arco.length() - 2));
        player.sendMessage(outros.substring(0, outros.length() - 2));
        player.sendMessage("");
        player.sendMessage("§7Use: §f/enchant <nome> <nível>");
        player.sendMessage("");
    }

    private Enchantment getEnchantmentByName(String name) {
        name = name.toUpperCase().replace(" ", "_");

        // Tentar encontrar pelo nome exato
        for (Enchantment ench : Enchantment.values()) {
            if (ench.getName().equals(name)) {
                return ench;
            }
        }

        // Tentar encontrar por nome alternativo (compatibilidade)
        try {
            return Enchantment.getByName(name);
        } catch (Exception e) {
            return null;
        }
    }

    private String getEnchantmentDisplayName(Enchantment enchantment) {
        String name = enchantment.getName();

        // Converter nome para formato legível
        return Arrays.stream(name.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}