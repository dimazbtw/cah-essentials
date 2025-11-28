package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import github.dimazbtw.essentials.Main;

import java.util.concurrent.ExecutionException;

public class Luz {
    private final Main plugin;

    public Luz(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "luz",
            description = "Ativa/Desativa visão noturna",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void toggleNightVision(Context<CommandSender> context) throws ExecutionException, InterruptedException {
        Player player = (Player) context.getSender();

        // Verifica se o jogador já tem o efeito
        boolean hasNightVision = player.hasPotionEffect(PotionEffectType.NIGHT_VISION);

        if (hasNightVision) {
            // Remove o efeito
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage("§cVisão noturna desativada!");
        } else {
            PotionEffect nightVision = new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    Integer.MAX_VALUE,
                    0,
                    true // ambient (menos partículas visuais)
            );


            player.addPotionEffect(nightVision);
            player.sendMessage("§aVisão noturna ativada!");
        }
    }
}
