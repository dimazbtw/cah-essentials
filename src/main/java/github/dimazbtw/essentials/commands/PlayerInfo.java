package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerInfo {
    private final Main plugin;
    private final SimpleDateFormat dateFormat;

    public PlayerInfo(Main plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    @Command(
            name = "playerinfo",
            aliases = "pinfo",
            description = "Mostra informações detalhadas sobre um jogador",
            target = CommandTarget.ALL,
            permission = "admin",
            async = true
    )
    public void playerInfo(Context<CommandSender> context, @Optional String playerName) throws ExecutionException, InterruptedException {
        Player targetPlayer = null;
        OfflinePlayer offlinePlayer = null;

        if (playerName == null) {
            if (!(context.getSender() instanceof Player)) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("usage", "/playerinfo <jogador>");
                context.sendMessage(plugin.getLangManager().getMessage("invalid-syntax", placeholders));
                return;
            }
            targetPlayer = (Player) context.getSender();
        }
        else {
            targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) {
                offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                if (!offlinePlayer.hasPlayedBefore()) {
                    context.sendMessage(plugin.getLangManager().getMessage("player-not-found"));
                    return;
                }
            }
        }

        Map<String, String> placeholders = new HashMap<>();

        if (targetPlayer != null) {
            // Jogador online
            placeholders.put("nick", targetPlayer.getName());
            placeholders.put("uuid", targetPlayer.getUniqueId().toString());
            placeholders.put("ip", targetPlayer.getAddress().getAddress().getHostAddress());
            placeholders.put("date", dateFormat.format(new Date(targetPlayer.getFirstPlayed())));
            placeholders.put("world", targetPlayer.getWorld().getName());
            placeholders.put("health", String.format("%.1f", targetPlayer.getHealth()));
            placeholders.put("max_health", String.format("%.1f", targetPlayer.getMaxHealth()));
            placeholders.put("food", String.valueOf(targetPlayer.getFoodLevel()));
            placeholders.put("gamemode", targetPlayer.getGameMode().toString());
            placeholders.put("status", "&a&lONLINE");

        } else {
            // Jogador offline
            placeholders.put("nick", offlinePlayer.getName());
            placeholders.put("uuid", offlinePlayer.getUniqueId().toString());
            placeholders.put("date", dateFormat.format(new Date(offlinePlayer.getLastPlayed())));
            placeholders.put("status", "&c&lOFFLINE");

            // Placeholders vazios para informações indisponíveis
            placeholders.put("ip", "N/A");
            placeholders.put("world", "N/A");
            placeholders.put("health", "N/A");
            placeholders.put("max_health", "N/A");
            placeholders.put("food", "N/A");
            placeholders.put("gamemode", "N/A");
        }

        List<String> infoFormat = plugin.getLangManager().getStringList("playerinfo.format", placeholders);
        StringBuilder info = new StringBuilder();

        for (String line : infoFormat) {
            info.append(line).append("\n");
        }

        context.sendMessage(info.toString());
    }
}
