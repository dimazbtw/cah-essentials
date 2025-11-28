package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.utils.basics.ColorUtils;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class KitCommands {
    private final Main plugin;

    public KitCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "kits",
            description = "Abre o menu de kits",
            target = CommandTarget.PLAYER
    )
    public void kits(Context<Player> context) {
        Player player = context.getSender();
        plugin.getKitsView().openMenu(player, "principal");
    }

    @Command(
            name = "kits.reload",
            description = "Recarrega o menu de kits",
            permission = "admin"
    )
    public void reload(Context<Player> context) {
        Player player = context.getSender();
        plugin.getKitsView().reload();
        player.sendMessage("§aMenu de kits recarregado com sucesso!");
    }

    @Command(
            name = "kit.criar",
            description = "Cria um novo kit com base no inventário",
            usage = "/kit criar <nome>",
            target = CommandTarget.PLAYER,
            permission = "admin",
            async = true
    )
    public void kitCriar(Context<CommandSender> context) {
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(plugin.getLangManager().getMessage("player-only-command"));
            return;
        }

        String[] args = context.getArgs();
        if (args.length < 1) {
            context.sendMessage("§cUso: /kit criar <nome>");
            return;
        }

        Player player = (Player) context.getSender();
        String kitId = args[0].toLowerCase();

        if (plugin.getKitManager().getKit(kitId).isPresent()) {
            context.sendMessage("§cJá existe um kit com esse nome!");
            return;
        }

        // Captura o inventário do jogador
        ItemStack[] items = player.getInventory().getContents();

        // Cria o kit com valores padrão
        plugin.getKitManager().createKit(
                kitId,
                kitId,
                "&7" + kitId,
                "kit." + kitId,
                86400, // 1 dia de delay padrão
                items
        );

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("kit", kitId);
        context.sendMessage(plugin.getLangManager().getMessage("kit.created", placeholders));
    }

    @Command(
            name = "kit.editar",
            description = "Edita um kit existente",
            usage = "/kit editar <nome> <argumento> <valor>",
            permission = "admin",
            async = true
    )
    public void kitEditar(Context<CommandSender> context) {
        String[] args = context.getArgs();
        if (args.length < 3) {
            context.sendMessage("§cUso: /kit editar <nome> <argumento> <valor>");
            context.sendMessage("§7Argumentos: nome, display, permissao, delay, itens");
            return;
        }

        String kitId = args[0].toLowerCase();
        String argumento = args[1].toLowerCase();
        String valor = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            context.sendMessage("§cKit não encontrado!");
            return;
        }

        KitModel kit = optKit.get();

        switch (argumento) {
            case "nome":
                kit.setNome(valor);
                context.sendMessage("§aNome do kit alterado para: " + valor);
                break;
            case "display":
                kit.setDisplay(ChatColor.translateAlternateColorCodes('&', valor));
                context.sendMessage("§aDisplay do kit alterado para: " + ChatColor.translateAlternateColorCodes('&', valor));
                break;
            case "permissao":
                kit.setPermissao(valor);
                context.sendMessage("§aPermissão do kit alterada para: " + valor);
                break;
            case "delay":
                try {
                    long delay = Long.parseLong(valor);
                    kit.setDelay(delay);
                    context.sendMessage("§aDelay do kit alterado para: " + delay + " segundos");
                } catch (NumberFormatException e) {
                    context.sendMessage("§cValor inválido! Use um número em segundos.");
                    return;
                }
                break;
            case "itens":
                if (!(context.getSender() instanceof Player)) {
                    context.sendMessage("§cApenas jogadores podem editar os itens!");
                    return;
                }
                Player player = (Player) context.getSender();
                kit.setItems(player.getInventory().getContents());
                context.sendMessage("§aItens do kit atualizados com base no seu inventário!");
                break;
            default:
                context.sendMessage("§cArgumento inválido! Use: nome, display, permissao, delay, itens");
                return;
        }

        plugin.getKitManager().saveKit(kit);
    }

    @Command(
            name = "kit.deletar",
            description = "Deleta um kit",
            usage = "/kit deletar <nome>",
            permission = "admin",
            async = true
    )
    public void kitDeletar(Context<CommandSender> context) {
        String[] args = context.getArgs();
        if (args.length < 1) {
            context.sendMessage("§cUso: /kit deletar <nome>");
            return;
        }

        String kitId = args[0].toLowerCase();

        if (!plugin.getKitManager().getKit(kitId).isPresent()) {
            context.sendMessage("§cKit não encontrado!");
            return;
        }

        plugin.getKitManager().deleteKit(kitId);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("kit", kitId);
        context.sendMessage(plugin.getLangManager().getMessage("kit.deleted", placeholders));
    }

    @Command(
            name = "kit.ver",
            description = "Visualiza os itens de um kit",
            usage = "/kit ver <nome>",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void kitVer(Context<CommandSender> context) {
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(plugin.getLangManager().getMessage("player-only-command"));
            return;
        }

        String[] args = context.getArgs();
        if (args.length < 1) {
            context.sendMessage("§cUso: /kit ver <nome>");
            return;
        }

        Player player = (Player) context.getSender();
        String kitId = args[0].toLowerCase();

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            context.sendMessage("§cKit não encontrado!");
            return;
        }

        // Abrir preview usando o método centralizado da KitsView
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getKitsView().openKitPreview(player, kitId, null);
        });
    }

    @Command(
            name = "kit",
            description = "Pega um kit",
            usage = "/kit <nome>",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void kit(Context<CommandSender> context) {
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(plugin.getLangManager().getMessage("player-only-command"));
            return;
        }

        String[] args = context.getArgs();
        if (args.length < 1) {
            context.sendMessage("§cUso: /kit <nome>");
            context.sendMessage("§7Use §f/kit ver <nome> §7para visualizar os itens");
            listKits(context);
            return;
        }

        Player player = (Player) context.getSender();
        String kitId = args[0].toLowerCase();

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            context.sendMessage("§cKit não encontrado!");
            return;
        }

        KitModel kit = optKit.get();

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            context.sendMessage(plugin.getLangManager().getMessage("kit.no-permission"));
            return;
        }

        // Verificar cooldown
        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitId);
        if (remaining > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", formatTime(remaining));
            context.sendMessage(plugin.getLangManager().getMessage("kit.cooldown", placeholders));
            return;
        }

        // Dar o kit
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (plugin.getKitManager().giveKit(player, kitId, false)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("kit", kit.getDisplay());
                player.sendMessage(plugin.getLangManager().getMessage("kit.received", placeholders));
            }
        });
    }

    @Command(
            name = "darkit",
            description = "Dá um kit para um jogador ignorando delay",
            usage = "/darkit <nome> <jogador>",
            permission = "admin",
            async = true
    )
    public void darKit(Context<CommandSender> context) {
        String[] args = context.getArgs();
        if (args.length < 2) {
            context.sendMessage("§cUso: /darkit <nome> <jogador>");
            return;
        }

        String kitId = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            context.sendMessage("§cJogador não encontrado!");
            return;
        }

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            context.sendMessage("§cKit não encontrado!");
            return;
        }

        KitModel kit = optKit.get();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (plugin.getKitManager().giveKit(target, kitId, true)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("kit", kit.getDisplay());
                placeholders.put("player", target.getName());

                context.sendMessage(plugin.getLangManager().getMessage("kit.given", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage("kit.received", placeholders));
            }
        });
    }

    private void listKits(Context<CommandSender> context) {
        if (!(context.getSender() instanceof Player)) return;

        Player player = (Player) context.getSender();
        context.sendMessage("");
        context.sendMessage("§6§lKits Disponíveis:");

        for (KitModel kit : plugin.getKitManager().getKits().values()) {
            if (kit.getPermissao().isEmpty() || player.hasPermission(kit.getPermissao())) {
                long remaining = plugin.getKitManager().getRemainingCooldown(player, kit.getId());
                String status = remaining > 0 ? "§c(Cooldown: " + formatTime(remaining) + ")" : "§a(Disponível)";
                context.sendMessage("§f- " + ColorUtils.colorize(kit.getDisplay()) + " " + status);
            }
        }
        context.sendMessage("");
    }

    private String formatTime(long seconds) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (secs > 0 || sb.length() == 0) sb.append(secs).append("s");

        return sb.toString().trim();
    }
}