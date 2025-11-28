package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import org.bukkit.command.CommandSender;

public class CustomCommandsCommand {
    private final Main plugin;

    public CustomCommandsCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "customcommands.reload",
            description = "Recarrega os comandos customizados",
            permission = "admin",
            aliases = {"cc.reload", "ccreload"}
    )
    public void reload(Context<CommandSender> context) {
        CommandSender sender = context.getSender();

        try {
            plugin.getCustomCommandsManager().reload();
            sender.sendMessage("§aComandos customizados recarregados com sucesso!");
        } catch (Exception e) {
            sender.sendMessage("§cErro ao recarregar comandos customizados!");
            e.printStackTrace();
        }
    }
}