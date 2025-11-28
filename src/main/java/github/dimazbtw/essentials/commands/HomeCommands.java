package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.sql.SQLException;
import java.util.Map;

public class HomeCommands {
    private final Main plugin;

    public HomeCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "sethome",
            description = "Define uma home na sua localização atual",
            target = CommandTarget.PLAYER
    )
    public void setHome(Context<Player> context, String homeName) {
        Player player = context.getSender();
        try {
            int homeCount = plugin.getHomeDatabase().getHomeCount(player.getUniqueId());
            int homeLimit = plugin.getHomeDatabase().getHomeLimit(player);

            if (homeCount >= homeLimit) {
                player.sendMessage(ChatColor.RED + "Você atingiu o limite de " + homeLimit + " homes!");
                return;
            }

            plugin.getHomeDatabase().setHome(player.getUniqueId(), homeName, player.getLocation());
            player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' definida com sucesso!");

        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Erro ao definir home!");
            e.printStackTrace();
        }
    }

    @Command(
            name = "home",
            description = "Teleporta para uma home",
            target = CommandTarget.PLAYER
    )
    public void home(Context<Player> context, String homeName) {
        Player player = context.getSender();
        try {
            Location location = plugin.getHomeDatabase().getHome(player.getUniqueId(), homeName);
            if (location == null) {
                player.sendMessage(ChatColor.RED + "Home não encontrada!");
                return;
            }
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "Teleportado para a home '" + homeName + "'!");
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Erro ao teleportar!");
            e.printStackTrace();
        }
    }

    @Command(
            name = "delhome",
            description = "Remove uma home",
            target = CommandTarget.PLAYER
    )
    public void delHome(Context<Player> context, String homeName) {
        Player player = context.getSender();
        try {
            Location home = plugin.getHomeDatabase().getHome(player.getUniqueId(), homeName);
            if (home == null) {
                player.sendMessage(ChatColor.RED + "Home não encontrada!");
                return;
            }
            plugin.getHomeDatabase().deleteHome(player.getUniqueId(), homeName);
            player.sendMessage(ChatColor.GREEN + "Home '" + homeName + "' removida com sucesso!");
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Erro ao remover home!");
            e.printStackTrace();
        }
    }

    @Command(
            name = "homes",
            description = "Lista todas as suas homes",
            target = CommandTarget.PLAYER
    )
    public void listHomes(Context<Player> context) {
        Player player = context.getSender();
        try {
            Map<String, Location> homes = plugin.getHomeDatabase().getHomes(player.getUniqueId());
            if (homes.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Você não possui homes!");
                return;
            }
            player.sendMessage(ChatColor.YELLOW + "Homes:");
            homes.forEach((name, loc) ->
                    player.sendMessage(ChatColor.GREEN + "- " + name));
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + "Erro ao listar homes!");
            e.printStackTrace();
        }
    }
}
