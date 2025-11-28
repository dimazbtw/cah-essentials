package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeleportCommands {
    private final Main plugin;

    public TeleportCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "tp",
            description = "Teleporta para um jogador, coordenadas ou mundo",
            target = CommandTarget.PLAYER,
            permission = "essentials.tp",
            usage = "/tp <jogador> | /tp <x> <y> <z> | /tp <x> <y> <z> <mundo>"
    )
    public void teleport(Context<CommandSender> context, String[] args) {
        Player player = (Player) context.getSender();

        if (args.length == 1) {
            // Teleportar para um jogador: /tp <jogador>
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                context.sendMessage(plugin.getLangManager().getMessage("player-not-found"));
                return;
            }

            if (target.equals(player)) {
                context.sendMessage(plugin.getLangManager().getMessage("tp.self-teleport"));
                return;
            }

            player.teleport(target);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("target", target.getName());
            context.sendMessage(plugin.getLangManager().getMessage("tp.teleported-to-player", placeholders));

        } else if (args.length == 3) {
            // Teleportar para coordenadas no mundo atual: /tp <x> <y> <z>
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);

                Location location = new Location(player.getWorld(), x, y, z);
                player.teleport(location);

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("x", String.format("%.2f", x));
                placeholders.put("y", String.format("%.2f", y));
                placeholders.put("z", String.format("%.2f", z));
                placeholders.put("world", player.getWorld().getName());

                context.sendMessage(plugin.getLangManager().getMessage("tp.teleported-to-coords", placeholders));

            } catch (NumberFormatException e) {
                context.sendMessage(plugin.getLangManager().getMessage("tp.invalid-coordinates"));
            }

        } else if (args.length == 4) {
            // Teleportar para coordenadas em mundo específico: /tp <x> <y> <z> <mundo>
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                String worldName = args[3];

                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    context.sendMessage(plugin.getLangManager().getMessage("world-not-found"));
                    return;
                }

                Location location = new Location(world, x, y, z);
                player.teleport(location);

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("x", String.format("%.2f", x));
                placeholders.put("y", String.format("%.2f", y));
                placeholders.put("z", String.format("%.2f", z));
                placeholders.put("world", world.getName());

                context.sendMessage(plugin.getLangManager().getMessage("tp.teleported-to-coords", placeholders));

            } catch (NumberFormatException e) {
                context.sendMessage(plugin.getLangManager().getMessage("tp.invalid-coordinates"));
            }
        } else {
            context.sendMessage(plugin.getLangManager().getMessage("tp.usage"));
        }
    }

    @Command(
            name = "tphere",
            description = "Teleporta um jogador para sua localização",
            target = CommandTarget.PLAYER,
            permission = "essentials.tphere",
            usage = "/tphere <jogador>"
    )
    public void teleportHere(Context<CommandSender> context, String targetName) {
        Player player = (Player) context.getSender();
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            context.sendMessage(plugin.getLangManager().getMessage("player-not-found"));
            return;
        }

        if (target.equals(player)) {
            context.sendMessage(plugin.getLangManager().getMessage("tp.self-teleport"));
            return;
        }

        target.teleport(player);

        Map<String, String> placeholdersToTarget = new HashMap<>();
        placeholdersToTarget.put("player", player.getName());
        target.sendMessage(plugin.getLangManager().getMessage("tp.teleported-to-player", placeholdersToTarget));

        Map<String, String> placeholdersToSender = new HashMap<>();
        placeholdersToSender.put("target", target.getName());
        context.sendMessage(plugin.getLangManager().getMessage("tp.teleported-player-to-you", placeholdersToSender));
    }

    @Command(
            name = "tpall",
            description = "Teleporta todos os jogadores para sua localização",
            target = CommandTarget.PLAYER,
            permission = "essentials.tpall"
    )
    public void teleportAll(Context<CommandSender> context) {
        Player player = (Player) context.getSender();
        Location location = player.getLocation();

        int teleportedCount = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.equals(player)) {
                target.teleport(location);
                teleportedCount++;

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player", player.getName());
                target.sendMessage(plugin.getLangManager().getMessage("tpall.teleported", placeholders));
            }
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("count", String.valueOf(teleportedCount));
        context.sendMessage(plugin.getLangManager().getMessage("tpall.teleported-all", placeholders));
    }
}
