package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SpawnCommands {
    private final Main plugin;

    public SpawnCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "setspawn",
            description = "Define o local de spawn do servidor",
            target = CommandTarget.PLAYER,
            permission = "admin",
            async = true
    )
    public void setSpawn(Context<CommandSender> context) throws ExecutionException, InterruptedException {
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(plugin.getLangManager().getMessage("player-only-command"));
            return;
        }

        Player player = (Player) context.getSender();
        Location location = player.getLocation();

        // Salva a localização no locs.yml
        plugin.getLocationManager().saveLocation("spawn", location);

        // Prepara as placeholders para a mensagem
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("world", location.getWorld().getName());
        placeholders.put("x", String.format("%.2f", location.getX()));
        placeholders.put("y", String.format("%.2f", location.getY()));
        placeholders.put("z", String.format("%.2f", location.getZ()));

        // Envia a mensagem de confirmação
        context.sendMessage(plugin.getLangManager().getMessage("spawn.set", placeholders));
    }

    @Command(
            name = "spawn",
            description = "Teleporta para o spawn do servidor",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void spawn(Context<CommandSender> context) {
        if (!(context.getSender() instanceof Player)) {
            context.sendMessage(plugin.getLangManager().getMessage("player-only-command"));
            return;
        }

        Player player = (Player) context.getSender();
        Location spawn = plugin.getLocationManager().getLocation("spawn");

        if (spawn == null) {
            context.sendMessage(plugin.getLangManager().getMessage("spawn.not-set"));
            return;
        }

        // Teleporta o jogador com um pequeno delay para garantir que o chunk esteja carregado
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.teleport(spawn);
            player.sendMessage(plugin.getLangManager().getMessage("spawn.teleported"));
        }, 5L); // 5 ticks = 0.25 segundos
    }
}
