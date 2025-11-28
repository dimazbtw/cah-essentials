package github.dimazbtw.essentials.tasks;

import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AnnouncerTask extends BukkitRunnable {

    private final Main plugin;
    private final File announcerFile;
    private YamlConfiguration config;

    private boolean ativar;
    private int delay;
    private boolean aleatorio;
    private List<Announcement> announcements;
    private int currentIndex = 0;
    private Random random;

    public AnnouncerTask(Main plugin) {
        this.plugin = plugin;
        this.announcerFile = new File(plugin.getDataFolder(), "anuncios.yml");
        this.random = new Random();

        createDefaultConfig();
        loadConfig();
    }

    private void createDefaultConfig() {
        if (!announcerFile.exists()) {
            plugin.saveResource("anuncios.yml", false);
        }
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(announcerFile);

        ativar = config.getBoolean("Ativar", true);
        delay = config.getInt("Delay", 60);
        aleatorio = config.getBoolean("Aleatorio", true);

        announcements = new ArrayList<>();

        ConfigurationSection mensagensSection = config.getConfigurationSection("Mensagens");
        if (mensagensSection != null) {
            for (String key : mensagensSection.getKeys(false)) {
                ConfigurationSection msgSection = mensagensSection.getConfigurationSection(key);
                if (msgSection != null) {
                    int prioridade = msgSection.getInt("Prioridade", 1);
                    List<String> chat = msgSection.getStringList("Chat");

                    announcements.add(new Announcement(key, prioridade, chat));
                }
            }

            // Ordenar por prioridade (menor = maior prioridade)
            announcements.sort(Comparator.comparingInt(Announcement::getPrioridade));
        }

        plugin.getLogger().info("Carregados " + announcements.size() + " anúncios!");
    }

    public void start() {
        if (!ativar) {
            plugin.getLogger().info("Sistema de anúncios desativado!");
            return;
        }

        if (announcements.isEmpty()) {
            plugin.getLogger().warning("Nenhum anúncio configurado!");
            return;
        }

        // Converter segundos para ticks (20 ticks = 1 segundo)
        long delayTicks = delay * 20L;

        this.runTaskTimerAsynchronously(plugin, delayTicks, delayTicks);
        plugin.getLogger().info("Sistema de anúncios iniciado! Delay: " + delay + " segundos");
    }

    @Override
    public void run() {
        if (announcements.isEmpty()) {
            return;
        }

        Announcement announcement;

        if (aleatorio) {
            // Escolhe aleatoriamente
            announcement = announcements.get(random.nextInt(announcements.size()));
        } else {
            // Escolhe sequencialmente
            announcement = announcements.get(currentIndex);
            currentIndex = (currentIndex + 1) % announcements.size();
        }

        // Envia mensagem para todos os jogadores online
        List<String> messages = announcement.getChat().stream()
                .map(this::translateColor)
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());

        // Executar na thread principal
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String message : messages) {
                    player.sendMessage(replacePlaceholders(message, player));
                }
            }
        });
    }

    private String translateColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private String replacePlaceholders(String text) {
        text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
        return text;
    }

    private String replacePlaceholders(String text, Player player) {
        text = replacePlaceholders(text);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public void reload() {
        loadConfig();
        plugin.getLogger().info("Anúncios recarregados!");
    }

    public boolean isAtivar() {
        return ativar;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isAleatorio() {
        return aleatorio;
    }

    public int getAnnouncementsCount() {
        return announcements.size();
    }

    // Classe interna para representar um anúncio
    private static class Announcement {
        private final String id;
        private final int prioridade;
        private final List<String> chat;

        public Announcement(String id, int prioridade, List<String> chat) {
            this.id = id;
            this.prioridade = prioridade;
            this.chat = chat;
        }

        public String getId() {
            return id;
        }

        public int getPrioridade() {
            return prioridade;
        }

        public List<String> getChat() {
            return chat;
        }
    }
}