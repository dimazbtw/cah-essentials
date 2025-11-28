package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Heal {
    private final Main plugin;

    public Heal(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "heal",
            aliases = "vida",
            description = "Cura um jogador",
            target = CommandTarget.ALL,
            permission = "admin",
            async = true
    )
    public void heal(Context<CommandSender> context, @Optional Player target) throws ExecutionException, InterruptedException {
        if (!(context.getSender() instanceof Player) && target == null) {
            // Creating a HashMap for compatibility with Java 1.8
            Map<String, String> params = new HashMap<String, String>();
            params.put("usage", "/heal <jogador>");

            // Sending the message using the language manager
            context.sendMessage(plugin.getLangManager().getMessage("invalid-syntax", params));
            return;
        }


        Player playerToHeal = target != null ? target : (Player) context.getSender();

        playerToHeal.setHealth(playerToHeal.getMaxHealth());
        playerToHeal.setFoodLevel(20);
        playerToHeal.setFireTicks(0);
        playerToHeal.setSaturation(20);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("target", playerToHeal.getName());

        if (target != null) {
            if (context.getSender() instanceof Player) {
                Player sender = (Player) context.getSender();
                placeholders.put("sender", sender.getName());
                sender.sendMessage(plugin.getLangManager().getMessage("heal.healed-other", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage("heal.target-healed", placeholders));
            } else {
                placeholders.put("sender", "CONSOLE");
                context.sendMessage(plugin.getLangManager().getMessage("heal.healed-other", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage("heal.target-healed", placeholders));
            }
        } else {
            context.sendMessage(plugin.getLangManager().getMessage("heal.healed-self"));
        }
    }
}