package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Broadcast {
    private final Main plugin;

    public Broadcast(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "broadcast",
            aliases = {"bc", "alerta"},
            description = "Envia uma mensagem para todos os jogadores",
            target = CommandTarget.ALL,
            permission = "admin",
            async = true
    )
    public void broadcast(Context<CommandSender> context, String[] args) throws ExecutionException, InterruptedException {
        if (args.length == 0) {
            context.sendMessage(plugin.getLangManager().getMessage("broadcast.no-message"));
            return;
        }

        String message = String.join(" ", args);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("message", message);

        String broadcastMessage = plugin.getLangManager().getMessage("broadcast.format", placeholders);
        Bukkit.broadcastMessage(broadcastMessage);

        // Log no console
        String senderName = context.getSender() instanceof Player ?
                ((Player) context.getSender()).getName() : "CONSOLE";
        plugin.getLogger().info(senderName + " enviou um broadcast: " + message);
    }
}