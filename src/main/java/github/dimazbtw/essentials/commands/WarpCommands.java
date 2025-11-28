package github.dimazbtw.essentials.commands;


import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.WarpModel;

import java.util.Map;
import java.util.Optional;

public class WarpCommands {
    private final Main plugin;

    public WarpCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "setwarp",
            permission = "utils.admin",
            description = "Define uma nova warp na sua localização atual",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void setWarp(Context<Player> context, String warpName) {
        Player player = context.getSender();

        if (warpName.length() > 16) {
            player.sendMessage(ChatColor.RED + "O nome da warp não pode ter mais de 16 caracteres!");
            return;
        }

        plugin.getWarpManager().saveWarp(warpName, player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Warp '" + warpName + "' criada com sucesso!");
    }

    @Command(
            name = "delwarp",
            permission = "utils.admin",
            description = "Remove uma warp existente",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void deleteWarp(Context<Player> context, String warpName) {
        Player player = context.getSender();

        if (!plugin.getWarpManager().getWarp(warpName).isPresent()) {
            player.sendMessage(ChatColor.RED + "Esta warp não existe!");
            return;
        }

        plugin.getWarpManager().deleteWarp(warpName);
        player.sendMessage(ChatColor.GREEN + "Warp '" + warpName + "' removida com sucesso!");
    }

    @Command(
            name = "warp",
            description = "Teleporta para uma warp",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void warp(Context<Player> context, String warpName) {
        Player player = context.getSender();

        Optional<WarpModel> warpOptional = plugin.getWarpManager().getWarp(warpName);
        if (warpOptional.isPresent()) {
            WarpModel warp = warpOptional.get();
            player.teleport(warp.getLocation());
        } else {
            player.sendMessage(ChatColor.RED + "Esta warp não existe!");
        }
    }

    @Command(
            name = "warps",
            description = "Lista todas as warps disponíveis",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void listWarps(Context<Player> context) {
        Player player = context.getSender();
        Map<String, WarpModel> warps = plugin.getWarpManager().getWarps();

        if (warps.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Não há warps definidas!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Warps disponíveis: " +
                String.join(", ", warps.keySet()));
    }
}
