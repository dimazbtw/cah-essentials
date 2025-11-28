package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Fly {
    private final Main plugin;

    public Fly(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "fly",
            description = "Ativa ou desativa o modo de voo",
            target = CommandTarget.ALL,
            permission = "admin",
            async = true
    )
    public void fly(Context<CommandSender> context, @Optional Player target) throws ExecutionException, InterruptedException {
        if (!(context.getSender() instanceof Player) && target == null) {
            // Creating a HashMap for compatibility with Java 1.8
            Map<String, String> params = new HashMap<String, String>();
            params.put("usage", "/fly <jogador>");

            // Sending the message using the language manager
            context.sendMessage(plugin.getLangManager().getMessage("invalid-syntax", params));
            return;
        }


        Player playerToFly = target != null ? target : (Player) context.getSender();
        String worldName = playerToFly.getWorld().getName();

        // Verifica se o voo está desativado neste mundo
        List<String> disabledWorlds = plugin.getConfig().getStringList("player-protection.disabled-flight-worlds");
        if (disabledWorlds.contains(worldName) && !playerToFly.hasPermission("fly.bypass")) {
            context.sendMessage(plugin.getLangManager().getMessage("fly.world-disabled"));
            return;
        }

        // Alterna o modo de voo
        boolean newFlightState = !playerToFly.getAllowFlight();
        playerToFly.setAllowFlight(newFlightState);
        playerToFly.setFlying(newFlightState);

        // Prepara as placeholders para as mensagens
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("target", playerToFly.getName());
        placeholders.put("state", newFlightState ? "ativado" : "desativado");

        if (target != null) {
            if (context.getSender() instanceof Player) {
                Player sender = (Player) context.getSender();
                placeholders.put("sender", sender.getName());
                sender.sendMessage(plugin.getLangManager().getMessage("fly.toggled-other", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage("fly.target-toggled", placeholders));
            } else {
                placeholders.put("sender", "CONSOLE");
                context.sendMessage(plugin.getLangManager().getMessage("fly.toggled-other", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage("fly.target-toggled", placeholders));
            }
        } else {
            context.sendMessage(plugin.getLangManager().getMessage("fly.toggled-self", placeholders));
        }
    }
}
