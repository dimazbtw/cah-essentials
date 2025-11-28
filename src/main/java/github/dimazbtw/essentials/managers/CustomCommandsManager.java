package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class CustomCommandsManager {
    private final Main plugin;
    private File configFile;
    private YamlConfiguration config;
    private final Map<String, CustomCommand> customCommands = new HashMap<>();
    private CommandMap commandMap;

    public CustomCommandsManager(Main plugin) {
        this.plugin = plugin;
        setupCommandMap();
        setupConfig();
        loadCommands();
    }

    private void setupCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao obter CommandMap!");
            e.printStackTrace();
        }
    }

    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "custom-commands.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                createDefaultConfig();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar arquivo de comandos customizados!");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void createDefaultConfig() {
        config.set("commands.discord.enabled", true);
        config.set("commands.discord.permission", "");
        config.set("commands.discord.aliases", Arrays.asList("dc"));
        config.set("commands.discord.type", "MESSAGE");
        config.set("commands.discord.messages", Arrays.asList(
                "",
                "§b§l⚡ Discord",
                "§7Entre no nosso Discord:",
                "§fhttps://discord.gg/exemplo",
                ""
        ));

        config.set("commands.site.enabled", true);
        config.set("commands.site.permission", "");
        config.set("commands.site.aliases", Arrays.asList("website", "web"));
        config.set("commands.site.type", "MESSAGE");
        config.set("commands.site.messages", Arrays.asList(
                "§6Nosso site: §fhttps://exemplo.com"
        ));

        config.set("commands.loja.enabled", true);
        config.set("commands.loja.permission", "");
        config.set("commands.loja.aliases", Arrays.asList("shop", "store"));
        config.set("commands.loja.type", "MESSAGE");
        config.set("commands.loja.messages", Arrays.asList(
                "",
                "§6§l⭐ Loja VIP",
                "§7Adquira VIPs e benefícios:",
                "§fhttps://loja.exemplo.com",
                ""
        ));

        config.set("commands.regras.enabled", true);
        config.set("commands.regras.permission", "");
        config.set("commands.regras.aliases", Arrays.asList("rules"));
        config.set("commands.regras.type", "MESSAGE");
        config.set("commands.regras.messages", Arrays.asList(
                "",
                "§c§l⚠ Regras do Servidor",
                "§71. §fNão use hacks ou trapaças",
                "§72. §fRespeite todos os jogadores",
                "§73. §fNão faça spam no chat",
                "§74. §fNão divulgue outros servidores",
                "§75. §fSiga as orientações da staff",
                ""
        ));

        config.set("commands.spawn.enabled", true);
        config.set("commands.spawn.permission", "");
        config.set("commands.spawn.aliases", Collections.emptyList());
        config.set("commands.spawn.type", "COMMAND");
        config.set("commands.spawn.command", "tp {player} spawn");

        config.set("commands.vip.enabled", true);
        config.set("commands.vip.permission", "vip");
        config.set("commands.vip.aliases", Collections.emptyList());
        config.set("commands.vip.type", "COMMANDS");
        config.set("commands.vip.commands", Arrays.asList(
                "give {player} diamond 1",
                "give {player} emerald 5",
                "tell {player} &aVocê recebeu seu kit VIP!"
        ));

        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar custom-commands.yml!");
            e.printStackTrace();
        }
    }

    private void loadCommands() {
        customCommands.clear();

        if (!config.contains("commands")) {
            plugin.getLogger().warning("Nenhum comando customizado encontrado!");
            return;
        }

        ConfigurationSection commandsSection = config.getConfigurationSection("commands");
        if (commandsSection == null) return;

        for (String cmdName : commandsSection.getKeys(false)) {
            String path = "commands." + cmdName;

            if (!config.getBoolean(path + ".enabled", true)) {
                continue;
            }

            String permission = config.getString(path + ".permission", "");
            List<String> aliases = config.getStringList(path + ".aliases");
            String type = config.getString(path + ".type", "MESSAGE");

            CustomCommand customCommand = new CustomCommand(
                    cmdName,
                    permission,
                    aliases,
                    type
            );

            // Carregar dados específicos do tipo
            switch (type.toUpperCase()) {
                case "MESSAGE":
                    List<String> messages = config.getStringList(path + ".messages");
                    customCommand.setMessages(messages);
                    break;
                case "COMMAND":
                    String command = config.getString(path + ".command", "");
                    customCommand.setCommand(command);
                    break;
                case "COMMANDS":
                    List<String> commands = config.getStringList(path + ".commands");
                    customCommand.setCommands(commands);
                    break;
            }

            registerCommand(customCommand);
            customCommands.put(cmdName.toLowerCase(), customCommand);
        }

        plugin.getLogger().info("Carregados " + customCommands.size() + " comandos customizados!");
    }

    private void registerCommand(CustomCommand customCommand) {
        BukkitCommand command = new BukkitCommand(customCommand.getName()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                return customCommand.execute(sender, args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                return new ArrayList<>();
            }
        };

        command.setAliases(customCommand.getAliases());
        if (!customCommand.getPermission().isEmpty()) {
            command.setPermission(customCommand.getPermission());
        }

        commandMap.register("essentials", command);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadCommands();
    }

    private class CustomCommand {
        private final String name;
        private final String permission;
        private final List<String> aliases;
        private final String type;
        private List<String> messages;
        private String command;
        private List<String> commands;

        public CustomCommand(String name, String permission, List<String> aliases, String type) {
            this.name = name;
            this.permission = permission;
            this.aliases = aliases;
            this.type = type;
        }

        public boolean execute(CommandSender sender, String[] args) {
            // Verificar permissão
            if (!permission.isEmpty() && !sender.hasPermission(permission)) {
                sender.sendMessage("§cVocê não tem permissão para usar este comando!");
                return true;
            }

            switch (type.toUpperCase()) {
                case "MESSAGE":
                    executeMessage(sender);
                    break;
                case "COMMAND":
                    executeCommand(sender, args);
                    break;
                case "COMMANDS":
                    executeCommands(sender, args);
                    break;
            }

            return true;
        }

        private void executeMessage(CommandSender sender) {
            if (messages == null || messages.isEmpty()) return;

            for (String message : messages) {
                String parsed = parseMessage(sender, message);
                sender.sendMessage(parsed);
            }
        }

        private void executeCommand(CommandSender sender, String[] args) {
            if (command == null || command.isEmpty()) return;

            String parsed = parseCommand(sender, command, args);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }

        private void executeCommands(CommandSender sender, String[] args) {
            if (commands == null || commands.isEmpty()) return;

            for (String cmd : commands) {
                String parsed = parseCommand(sender, cmd, args);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            }
        }

        private String parseMessage(CommandSender sender, String message) {
            message = ChatColor.translateAlternateColorCodes('&', message);

            if (sender instanceof Player) {
                Player player = (Player) sender;
                message = message.replace("{player}", player.getName())
                        .replace("{displayname}", player.getDisplayName())
                        .replace("{world}", player.getWorld().getName())
                        .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                        .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                        .replace("{z}", String.valueOf(player.getLocation().getBlockZ()));
            }

            message = message.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                    .replace("{max_players}", String.valueOf(Bukkit.getMaxPlayers()));

            return message;
        }

        private String parseCommand(CommandSender sender, String cmd, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                cmd = cmd.replace("{player}", player.getName())
                        .replace("{uuid}", player.getUniqueId().toString())
                        .replace("{world}", player.getWorld().getName());
            }

            // Substituir argumentos {arg0}, {arg1}, etc.
            for (int i = 0; i < args.length; i++) {
                cmd = cmd.replace("{arg" + i + "}", args[i]);
            }

            // Substituir {args} com todos os argumentos
            cmd = cmd.replace("{args}", String.join(" ", args));

            return cmd;
        }

        public String getName() {
            return name;
        }

        public String getPermission() {
            return permission;
        }

        public List<String> getAliases() {
            return aliases;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setCommands(List<String> commands) {
            this.commands = commands;
        }
    }
}