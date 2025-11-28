package github.dimazbtw.essentials.commands.registry;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.commands.*;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;

public class CommandRegistry {
    public CommandRegistry(Main plugin){
        BukkitFrame frame = new BukkitFrame(plugin);

        frame.registerCommands(
                new Broadcast(plugin),
                new Feed(plugin),
                new Fly(plugin),
                new Gamemode(plugin),
                new Heal(plugin),
                new HomeCommands(plugin),
                new ItemInfoCommand(),
                new Luz(plugin),
                new PlayerInfo(plugin),
                new SpawnCommands(plugin),
                new Speed(plugin),
                new TpaCommands(plugin),
                new UtilityCommands(plugin),
                new WarpCommands(plugin),
                new TeleportCommands(plugin),
                new KitCommands(plugin),
                new EnchantCommand(plugin)
        );

        MessageHolder messageHolder = frame.getMessageHolder();

        messageHolder.setMessage(MessageType.ERROR, "§cOcorreu um erro durante a execução deste comando.");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cEste comando é destinado apenas a jogadores.");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUso correto: {usage}.");
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar esse comando.");
    }
}
