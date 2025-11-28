package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import github.dimazbtw.essentials.Main;

import java.util.HashMap;
import java.util.Map;

public class Gamemode {
    private final Main plugin;

    public Gamemode(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "gamemode",
            aliases = "gm",
            description = "Altere o modo de jogo",
            target = CommandTarget.ALL,
            permission = "admin",
            async = true
    )
    public void changeGameMode(Context<CommandSender> context, String mode, @Optional Player target) {
        if (!(context.getSender() instanceof Player) && target == null) {
            Map<String, String> params = new HashMap<>();
            params.put("usage", "/gamemode <modo> <jogador>");
            context.sendMessage(plugin.getLangManager().getMessage("invalid-syntax", params));
            return;
        }

        Player player = target != null ? target : (Player) context.getSender();
        GameMode gameMode;

        switch (mode.toLowerCase()) {
            case "0":
            case "s":
            case "survival":
                gameMode = GameMode.SURVIVAL;
                break;
            case "1":
            case "c":
            case "creative":
                gameMode = GameMode.CREATIVE;
                break;
            case "2":
            case "a":
            case "adventure":
                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
            case "sp":
            case "spectator":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                context.sendMessage(plugin.getLangManager().getMessage("gamemode.invalid-mode"));
                return;
        }

        player.setGameMode(gameMode);

        // ✅ CORRETO - Pega tradução do arquivo de idioma
        String gamemodeKey = "gamemodes." + gameMode.name().toLowerCase();

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("target", player.getName());

        if (target != null) {
            // Enviar mensagem para quem executou o comando
            if (context.getSender() instanceof Player) {
                Player sender = (Player) context.getSender();

                // Traduz gamemode no idioma do SENDER
                String senderGamemodeTranslated = plugin.getLangManager().getMessage(sender, gamemodeKey);
                placeholders.put("gamemode", senderGamemodeTranslated);
                placeholders.put("sender", sender.getName());

                sender.sendMessage(plugin.getLangManager().getMessage(sender, "gamemode.changed-other", placeholders));

                // Traduz gamemode no idioma do TARGET
                String targetGamemodeTranslated = plugin.getLangManager().getMessage(target, gamemodeKey);
                placeholders.put("gamemode", targetGamemodeTranslated);

                target.sendMessage(plugin.getLangManager().getMessage(target, "gamemode.target-changed", placeholders));
            } else {
                // Console executou
                String targetGamemodeTranslated = plugin.getLangManager().getMessage(target, gamemodeKey);
                placeholders.put("gamemode", targetGamemodeTranslated);
                placeholders.put("sender", "CONSOLE");

                context.sendMessage(plugin.getLangManager().getMessage("gamemode.changed-other", placeholders));
                target.sendMessage(plugin.getLangManager().getMessage(target, "gamemode.target-changed", placeholders));
            }
        } else {
            // Mudou o próprio gamemode
            Player sender = (Player) context.getSender();
            String senderGamemodeTranslated = plugin.getLangManager().getMessage(sender, gamemodeKey);
            placeholders.put("gamemode", senderGamemodeTranslated);

            sender.sendMessage(plugin.getLangManager().getMessage(sender, "gamemode.changed-self", placeholders));
        }
    }
}