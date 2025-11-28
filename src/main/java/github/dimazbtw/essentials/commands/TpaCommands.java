package github.dimazbtw.essentials.commands;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.*;
import github.dimazbtw.essentials.Main;

public class TpaCommands {
    private final Main plugin;

    public TpaCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "tpa",
            description = "Envia um pedido de teleporte para outro jogador",
            target = CommandTarget.PLAYER
    )
    public void tpa(Context<Player> context, Player target) {
        Player sender = context.getSender();

        if (sender.equals(target)) {
            sender.sendMessage(plugin.getLangManager().getMessage("tpa.error.self"));
            return;
        }

        if (plugin.getTpaManager().isTpaDisabled(target)) {
            sender.sendMessage(plugin.getLangManager().getMessage("tpa.error.disabled"));
            return;
        }

        if (plugin.getTpaManager().hasRequest(target)) {
            sender.sendMessage(plugin.getLangManager().getMessage("tpa.error.pending"));
            return;
        }

        plugin.getTpaManager().createRequest(sender, target);
        sender.sendMessage(plugin.getLangManager().getMessage("tpa.sent")
                .replace("{player}", target.getName()));

        // Mensagem para o alvo
        target.sendMessage("");
        target.sendMessage(plugin.getLangManager().getMessage("tpa.request.title")
                .replace("{player}", sender.getName()));

        // Botões interativos
        String acceptText = plugin.getLangManager().getMessage("tpa.request.accept.text");
        String acceptHover = plugin.getLangManager().getMessage("tpa.request.accept.hover");
        String denyText = plugin.getLangManager().getMessage("tpa.request.deny.text");
        String denyHover = plugin.getLangManager().getMessage("tpa.request.deny.hover");

        BaseComponent[] buttons = new ComponentBuilder("")
                .append(acceptText)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent(acceptHover) }))
                .append(" ")
                .append(denyText)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent(denyHover) }))
                .create();
        target.spigot().sendMessage(buttons);
        target.sendMessage("");
    }

    @Command(
            name = "tpaccept",
            description = "Aceita um pedido de teleporte",
            target = CommandTarget.PLAYER
    )
    public void tpaccept(Context<Player> context) {
        Player target = context.getSender();

        if (!plugin.getTpaManager().hasRequest(target)) {
            target.sendMessage(plugin.getLangManager().getMessage("tpa.error.no-request"));
            return;
        }

        Player sender = plugin.getTpaManager().getSender(target);
        if (sender == null || !sender.isOnline()) {
            target.sendMessage(plugin.getLangManager().getMessage("tpa.error.offline"));
            plugin.getTpaManager().removeRequest(target);
            return;
        }

        sender.teleport(target.getLocation());
        sender.sendMessage(plugin.getLangManager().getMessage("tpa.accepted.sender")
                .replace("{player}", target.getName()));
        target.sendMessage(plugin.getLangManager().getMessage("tpa.accepted.target")
                .replace("{player}", sender.getName()));
        plugin.getTpaManager().removeRequest(target);
    }

    @Command(
            name = "tpdeny",
            description = "Recusa um pedido de teleporte",
            target = CommandTarget.PLAYER
    )
    public void tpdeny(Context<Player> context) {
        Player target = context.getSender();

        if (!plugin.getTpaManager().hasRequest(target)) {
            target.sendMessage(plugin.getLangManager().getMessage("tpa.error.no-request"));
            return;
        }

        Player sender = plugin.getTpaManager().getSender(target);
        if (sender != null && sender.isOnline()) {
            sender.sendMessage(plugin.getLangManager().getMessage("tpa.denied.sender")
                    .replace("{player}", target.getName()));
        }

        target.sendMessage(plugin.getLangManager().getMessage("tpa.denied.target"));
        plugin.getTpaManager().removeRequest(target);
    }

    @Command(
            name = "tpatoggle",
            description = "Ativa ou desativa o recebimento de pedidos de teleporte",
            target = CommandTarget.PLAYER
    )
    public void tpatoggle(Context<Player> context) {
        Player player = context.getSender();

        boolean newState = plugin.getTpaManager().toggleTpa(player);

        if (newState) {
            player.sendMessage(plugin.getLangManager().getMessage("tpa.toggle.disabled"));
        } else {
            player.sendMessage(plugin.getLangManager().getMessage("tpa.toggle.enabled"));
        }
    }
}