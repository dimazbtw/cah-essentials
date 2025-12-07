package github.dimazbtw.essentials.tasks;

import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AnnouncerTask extends BukkitRunnable {

    private final Main plugin;
    private final File configFile;

    private boolean enabled;
    private int delay;
    private boolean random;
    private List<Announcement> announcements;
    private int currentIndex = 0;
    private Random randomGenerator;

    public AnnouncerTask(Main plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "anuncios.yml");
        this.randomGenerator = new Random();
        this.announcements = new ArrayList<>();

        setupConfig();
        loadConfig();
    }

    private void setupConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        YamlConfiguration config = new YamlConfiguration();

        config.set("Delay", 60);
        config.set("Ativar", true);
        config.set("Aleatorio", true);

        // Mensagem 1 - Dica
        config.set("Mensagens.dica.Prioridade", 1);
        config.set("Mensagens.dica.Chat.pt_PT", Arrays.asList(
                "",
                "&e&lDICA",
                "&8▪ &fUm bom começo será pela mineração &7(/mina).",
                ""
        ));
        config.set("Mensagens.dica.Chat.en_US", Arrays.asList(
                "",
                "&e&lTIP",
                "&8▪ &fA good start is mining &7(/mine).",
                ""
        ));

        // Mensagem 2 - Servidor
        config.set("Mensagens.servidor.Prioridade", 2);
        config.set("Mensagens.servidor.Chat.pt_PT", Arrays.asList(
                "",
                "&a&lSERVIDOR",
                "&8▪ &fAtualmente temos &a%online%&f/&a%max% &fjogadores online!",
                ""
        ));
        config.set("Mensagens.servidor.Chat.en_US", Arrays.asList(
                "",
                "&a&lSERVER",
                "&8▪ &fCurrently we have &a%online%&f/&a%max% &fplayers online!",
                ""
        ));

        // Mensagem 3 - Discord
        config.set("Mensagens.discord.Prioridade", 3);
        config.set("Mensagens.discord.Chat.pt_PT", Arrays.asList(
                "",
                "&b&lDISCORD",
                "&8▪ &fEntre no nosso Discord: &bdiscord.gg/exemplo",
                ""
        ));
        config.set("Mensagens.discord.Chat.en_US", Arrays.asList(
                "",
                "&b&lDISCORD",
                "&8▪ &fJoin our Discord: &bdiscord.gg/example",
                ""
        ));

        // Mensagem 4 - Kits
        config.set("Mensagens.kits.Prioridade", 4);
        config.set("Mensagens.kits.Chat.pt_PT", Arrays.asList(
                "",
                "&6&lKITS",
                "&8▪ &fUse &6/kit &fpara ver os kits disponíveis!",
                ""
        ));
        config.set("Mensagens.kits.Chat.en_US", Arrays.asList(
                "",
                "&6&lKITS",
                "&8▪ &fUse &6/kit &fto see available kits!",
                ""
        ));

        // Mensagem 5 - Loja
        config.set("Mensagens.loja.Prioridade", 5);
        config.set("Mensagens.loja.Chat.pt_PT", Arrays.asList(
                "",
                "&d&lLOJA",
                "&8▪ &fAdquira VIP em &dloja.seuservidor.com",
                ""
        ));
        config.set("Mensagens.loja.Chat.en_US", Arrays.asList(
                "",
                "&d&lSTORE",
                "&8▪ &fGet VIP at &dstore.yourserver.com",
                ""
        ));

        // Mensagem 6 - Regras
        config.set("Mensagens.regras.Prioridade", 6);
        config.set("Mensagens.regras.Chat.pt_PT", Arrays.asList(
                "",
                "&c&lREGRAS",
                "&8▪ &fLeia as regras do servidor: &c/regras",
                ""
        ));
        config.set("Mensagens.regras.Chat.en_US", Arrays.asList(
                "",
                "&c&lRULES",
                "&8▪ &fRead the server rules: &c/rules",
                ""
        ));

        // Mensagem 7 - Votos
        config.set("Mensagens.votar.Prioridade", 7);
        config.set("Mensagens.votar.Chat.pt_PT", Arrays.asList(
                "",
                "&5&lVOTE",
                "&8▪ &fVote no servidor e ganhe recompensas!",
                "&8▪ &fUse &5/votar &fpara votar.",
                ""
        ));
        config.set("Mensagens.votar.Chat.en_US", Arrays.asList(
                "",
                "&5&lVOTE",
                "&8▪ &fVote for the server and get rewards!",
                "&8▪ &fUse &5/vote &fto vote.",
                ""
        ));

        // Mensagem 8 - Ajuda
        config.set("Mensagens.ajuda.Prioridade", 8);
        config.set("Mensagens.ajuda.Chat.pt_PT", Arrays.asList(
                "",
                "&f&lAJUDA",
                "&8▪ &fPrecisa de ajuda? Use &7/ajuda &fou pergunte no chat!",
                ""
        ));
        config.set("Mensagens.ajuda.Chat.en_US", Arrays.asList(
                "",
                "&f&lHELP",
                "&8▪ &fNeed help? Use &7/help &for ask in chat!",
                ""
        ));

        try {
            config.save(configFile);
            plugin.getLogger().info("Arquivo de anúncios criado: anuncios.yml");
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao criar arquivo de anúncios!");
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        enabled = config.getBoolean("Ativar", true);
        delay = config.getInt("Delay", 60);
        random = config.getBoolean("Aleatorio", true);

        // Carregar anúncios
        announcements.clear();

        ConfigurationSection messagesSection = config.getConfigurationSection("Mensagens");
        if (messagesSection == null) {
            plugin.getLogger().warning("Nenhuma mensagem de anúncio configurada!");
            return;
        }

        for (String key : messagesSection.getKeys(false)) {
            String path = "Mensagens." + key;

            int priority = config.getInt(path + ".Prioridade", 1);
            String permission = config.getString(path + ".Permissao", "");

            // Carregar traduções
            Map<String, List<String>> translations = new HashMap<>();
            ConfigurationSection chatSection = config.getConfigurationSection(path + ".Chat");

            if (chatSection != null) {
                for (String lang : chatSection.getKeys(false)) {
                    List<String> messages = config.getStringList(path + ".Chat." + lang);
                    if (!messages.isEmpty()) {
                        translations.put(lang, messages);
                    }
                }
            }

            if (!translations.isEmpty()) {
                announcements.add(new Announcement(key, priority, translations, permission));
            }
        }

        // Ordenar por prioridade
        announcements.sort(Comparator.comparingInt(Announcement::getPriority));

        plugin.getLogger().info("Carregados " + announcements.size() + " anúncios!");
    }

    public void start() {
        if (!enabled) {
            plugin.getLogger().info("Sistema de anúncios desativado na configuração.");
            return;
        }

        if (announcements.isEmpty()) {
            plugin.getLogger().warning("Nenhum anúncio configurado! Sistema não iniciado.");
            return;
        }

        long delayTicks = delay * 20L;
        this.runTaskTimerAsynchronously(plugin, delayTicks, delayTicks);

        plugin.getLogger().info("Sistema de anúncios iniciado! Intervalo: " + delay + "s | Aleatório: " + random);
    }

    @Override
    public void run() {
        if (announcements.isEmpty()) return;

        // Selecionar anúncio
        Announcement announcement = selectAnnouncement();
        if (announcement == null) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (canReceive(player, announcement)) {
                    sendAnnouncement(player, announcement);
                }
            }

            // Incrementar índice para modo sequencial
            if (!random) {
                currentIndex++;
                if (currentIndex >= announcements.size()) {
                    currentIndex = 0;
                }
            }
        });
    }

    private Announcement selectAnnouncement() {
        if (announcements.isEmpty()) return null;

        if (random) {
            return announcements.get(randomGenerator.nextInt(announcements.size()));
        } else {
            return announcements.get(currentIndex % announcements.size());
        }
    }

    private boolean canReceive(Player player, Announcement announcement) {
        if (!announcement.getPermission().isEmpty()) {
            return player.hasPermission(announcement.getPermission());
        }
        return true;
    }

    private void sendAnnouncement(Player player, Announcement announcement) {
        String playerLang = plugin.getLangManager().getPlayerLanguage(player);
        List<String> messages = announcement.getMessagesForLanguage(playerLang, plugin.getLangManager().getDefaultLanguage());

        if (messages == null || messages.isEmpty()) return;

        for (String message : messages) {
            String processed = translateColor(message);
            processed = replacePlaceholders(processed, player);
            player.sendMessage(processed);
        }
    }

    private String translateColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private String replacePlaceholders(String text, Player player) {
        text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
        text = text.replace("%player%", player.getName());
        text = text.replace("%world%", player.getWorld().getName());

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public void reload() {
        loadConfig();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRandom() {
        return random;
    }

    public int getAnnouncementsCount() {
        return announcements.size();
    }

    // Classe interna para representar um anúncio
    private static class Announcement {
        private final String id;
        private final int priority;
        private final Map<String, List<String>> translations;
        private final String permission;

        public Announcement(String id, int priority, Map<String, List<String>> translations, String permission) {
            this.id = id;
            this.priority = priority;
            this.translations = translations;
            this.permission = permission;
        }

        public String getId() {
            return id;
        }

        public int getPriority() {
            return priority;
        }

        public String getPermission() {
            return permission;
        }

        public List<String> getMessagesForLanguage(String lang, String defaultLang) {
            // Tentar idioma do jogador
            List<String> messages = translations.get(lang);
            if (messages != null && !messages.isEmpty()) {
                return messages;
            }

            // Fallback para idioma padrão
            messages = translations.get(defaultLang);
            if (messages != null && !messages.isEmpty()) {
                return messages;
            }

            // Fallback para primeiro idioma disponível
            return translations.values().stream()
                    .filter(list -> !list.isEmpty())
                    .findFirst()
                    .orElse(Collections.emptyList());
        }

        public Set<String> getAvailableLanguages() {
            return translations.keySet();
        }
    }
}