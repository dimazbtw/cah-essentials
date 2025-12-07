package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.managers.LangManager;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import github.dimazbtw.essentials.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UtilityCommands {
    private final Main plugin;
    private final Map<UUID, UUID> clearConfirmations = new HashMap<>();

    public UtilityCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "online",
            description = "Mostra o número de jogadores online",
            target = CommandTarget.PLAYER
    )
    public void online(Context<Player> context) {
        Player player = context.getSender();
        int onlineCount = Bukkit.getOnlinePlayers().size();
        player.sendMessage("§6Jogadores online: §f" + onlineCount);
    }

    @Command(
            name = "lista",
            description = "Lista todos os jogadores online",
            target = CommandTarget.PLAYER
    )
    public void lista(Context<Player> context) {
        Player player = context.getSender();
        String players = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));
        player.sendMessage("§6Jogadores online: §f" + players);
    }

    @Command(
            name = "ping",
            description = "Mostra o ping de um jogador",
            target = CommandTarget.PLAYER
    )
    public void ping(Context<Player> context, @Optional Player target) {
        Player player = context.getSender();
        if (target == null) target = player;

        int ping = target.getPing();

        String targetName = target.equals(player) ? "" : "de " + target.getName();
        player.sendMessage("§aPing " + targetName + ": §f" + ping + "ms");
    }

    @Command(
            name = "lixo",
            permission = "essentials.lixo",
            description = "Abre uma lixeira virtual",
            target = CommandTarget.PLAYER
    )
    public void lixo(Context<Player> context) {
        Player player = context.getSender();
        Inventory trash = Bukkit.createInventory(null, 54, "Lixeira");
        player.openInventory(trash);
    }

    @Command(
            name = "reparar",
            permission = "essentials.reparar",
            description = "Repara o item na mão",
            target = CommandTarget.PLAYER
    )
    public void reparar(Context<Player> context) {
        Player player = context.getSender();
        ItemStack item = player.getInventory().getItemInHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage("§cPrecisas de ter um item na mão!");
            return;
        }

        if (!(item.getType().getMaxDurability() > 0)) {
            player.sendMessage("§cEste item não pode ser reparado!");
            return;
        }

        item.setDurability((short) 0);
        player.sendMessage("§aItem reparado com sucesso!");
    }

    @Command(
            name = "craft",
            permission = "essentials.craft",
            description = "Abre uma mesa de craft virtual",
            target = CommandTarget.PLAYER
    )
    public void craft(Context<Player> context) {
        Player player = context.getSender();
        player.openWorkbench(null, true);
    }

    @Command(
            name = "clear",
            permission = "essentials.clear",
            description = "Limpa o inventário de um jogador",
            target = CommandTarget.PLAYER
    )
    public void clear(Context<Player> context, @Optional Player target) {
        Player player = context.getSender();
        if (target == null) target = player;

        Player finalTarget = target;

        if (target == player) {
            player.sendMessage("");
            player.sendMessage("§c⚠ §7Tens a certeza que queres limpar o teu inventário?");

            BaseComponent[] buttons = new ComponentBuilder("")
                    .append("§a[CONFIRMAR] ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clearconfirm"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent("§aClique para confirmar") }))
                    .append("§c[CANCELAR]")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clearcancel"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent("§cClique para cancelar") }))
                    .create();
            player.spigot().sendMessage(buttons);
            player.sendMessage("");

            clearConfirmations.put(player.getUniqueId(), player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (clearConfirmations.remove(player.getUniqueId()) != null) {
                    player.sendMessage("§cTempo de confirmação expirado!");
                }
            }, 20 * 30);
        } else if (player.hasPermission("admin")) {
            finalTarget.getInventory().clear();
            player.sendMessage("§aVocê limpou o inventário de " + finalTarget.getName());
            finalTarget.sendMessage("§aSeu inventário foi limpo por " + player.getName());
        }
    }

    @Command(
            name = "clearconfirm",
            target = CommandTarget.PLAYER
    )
    public void clearConfirm(Context<Player> context) {
        Player player = context.getSender();

        if (clearConfirmations.remove(player.getUniqueId()) != null) {
            player.getInventory().clear();
            player.sendMessage("§aTeu inventário foi limpo!");
        } else {
            player.sendMessage("§cNão tens nenhuma confirmação pendente!");
        }
    }

    @Command(
            name = "clearcancel",
            target = CommandTarget.PLAYER
    )
    public void clearCancel(Context<Player> context) {
        Player player = context.getSender();

        if (clearConfirmations.remove(player.getUniqueId()) != null) {
            player.sendMessage("§cLimpeza de inventário cancelada!");
        } else {
            player.sendMessage("§cNão tens nenhuma confirmação pendente!");
        }
    }

    @Command(
            name = "inv",
            description = "Abre e edita o inventário de outro jogador",
            target = CommandTarget.PLAYER,
            permission = "admin"
    )
    public void inv(Context<Player> context, Player target) {
        Player player = context.getSender();

        if (target == null) {
            player.sendMessage("§cJogador não encontrado!");
            return;
        }

        player.openInventory(target.getInventory());
        player.sendMessage("§aVocê abriu o inventário de " + target.getName());
    }

    @Command(
            name = "hat",
            permission = "essentials.hat",
            description = "Coloca o item na mão como chapéu",
            target = CommandTarget.PLAYER
    )
    public void hat(Context<Player> context) {
        Player player = context.getSender();
        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage("§cPrecisas de ter um item na mão!");
            return;
        }

        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(itemInHand);
        player.getInventory().setItemInHand(helmet);
        player.sendMessage("§aChapéu equipado!");
    }

    @Command(
            name = "sudo",
            permission = "essentials.sudo",
            description = "Força um jogador a executar um comando",
            target = CommandTarget.PLAYER
    )
    public void sudo(Context<Player> context, Player target, String[] args) {
        Player player = context.getSender();

        if (target == null) {
            player.sendMessage("§cJogador não encontrado!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUso: /sudo <jogador> <comando>");
            return;
        }

        String command = String.join(" ", args);

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        target.performCommand(command);
        player.sendMessage("§aComando executado com sucesso!");
    }

    @Command(
            name = "rename",
            permission = "essentials.rename",
            description = "Renomeia o item na mão",
            target = CommandTarget.PLAYER
    )
    public void rename(Context<Player> context, String[] args) {
        Player player = context.getSender();
        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cPrecisas de ter um item na mão!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUso: /rename <nome>");
            return;
        }

        String name = String.join(" ", args);
        name = ChatColor.translateAlternateColorCodes('&', name);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            player.sendMessage("§aItem renomeado para: " + name);
        }
    }

    @Command(
            name = "lang",
            aliases = "idioma",
            description = "Altera o idioma do jogador",
            target = CommandTarget.PLAYER
    )
    public void handleLang(Context<Player> context, @Optional String langCode) {
        Player player = context.getSender();
        LangManager langManager = plugin.getLangManager();

        if (langCode == null) {
            // Mostrar lista de idiomas
            player.sendMessage(langManager.getMessage(player, "language.list-header"));

            String currentLang = langManager.getPlayerLanguage(player);

            for (String code : langManager.getAvailableLanguages()) {
                String name = langManager.getLanguageName(code);
                String flag = langManager.getLanguageFlag(code);

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("code", code);
                placeholders.put("name", name);
                placeholders.put("flag", flag);

                String line = langManager.getMessage(player, "language.list-item", placeholders);

                if (code.equals(currentLang)) {
                    line += " " + langManager.getMessage(player, "language.list-current");
                }

                player.sendMessage(line);
            }
            return;
        }

        // Alterar idioma
        if (!langManager.languageExists(langCode)) {
            player.sendMessage(langManager.getMessage(player, "language.not-found"));
            return;
        }

        langManager.setPlayerLanguage(player, langCode);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("language", langManager.getLanguageName(langCode) + " " + langManager.getLanguageFlag(langCode));

        player.sendMessage(langManager.getMessage(player, "language.changed", placeholders));
    }
}