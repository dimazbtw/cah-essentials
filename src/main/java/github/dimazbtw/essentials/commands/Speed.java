package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Speed {
    private final Main plugin;
    private final float DEFAULT_WALK_SPEED = 0.2f;
    private final float DEFAULT_FLY_SPEED = 0.1f;

    public Speed(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "speed",
            description = "Altera a velocidade de movimento do jogador",
            target = CommandTarget.PLAYER,
            permission = "admin",
            async = true
    )
    public void speed(Context<CommandSender> context, String speedArg) throws ExecutionException, InterruptedException {
        Player player = (Player) context.getSender();

        if (speedArg.equalsIgnoreCase("clear")) {
            resetSpeed(player);
            player.sendMessage(plugin.getLangManager().getMessage("speed.reset"));
            return;
        }

        float speedValue;
        try {
            speedValue = Float.parseFloat(speedArg);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getLangManager().getMessage("speed.invalid-input"));
            return;
        }

        if (speedValue < 1 || speedValue > 10) {
            player.sendMessage(plugin.getLangManager().getMessage("speed.invalid-speed"));
            return;
        }

        float convertedSpeed = speedValue / 10;
        player.setWalkSpeed(convertedSpeed);
        player.setFlySpeed(convertedSpeed);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("speed", String.valueOf(speedValue));
        player.sendMessage(plugin.getLangManager().getMessage("speed.changed", placeholders));
    }

    private void resetSpeed(Player player) {
        player.setWalkSpeed(DEFAULT_WALK_SPEED);
        player.setFlySpeed(DEFAULT_FLY_SPEED);
    }
}