package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.lib.utils.basics.ColorUtils;
import lombok.var;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VisualsManager implements Listener {

    private final Main plugin;

    // Hooks externos
    private boolean placeholderApiEnabled;
    private boolean luckPermsEnabled;
    private LuckPerms luckPerms;

    // ==================== SCOREBOARD ====================
    private boolean scoreboardEnabled;
    private int scoreboardUpdateInterval;
    private final Map<UUID, PlayerScoreboard> playerScoreboards;
    private BukkitTask scoreboardTask;

    // ==================== TAB ====================
    private boolean tabEnabled;
    private int tabUpdateInterval;
    private boolean tabSortByGroup;
    private BukkitTask tabTask;

    // Configurações por idioma
    private final Map<String, VisualsConfig> languageConfigs;

    // Constantes
    private static final String[] COLOR_CODES = {
            "§0§r", "§1§r", "§2§r", "§3§r", "§4§r", "§5§r", "§6§r", "§7§r",
            "§8§r", "§9§r", "§a§r", "§b§r", "§c§r", "§d§r", "§e§r", "§f§r"
    };

    public VisualsManager(Main plugin) {
        this.plugin = plugin;
        this.playerScoreboards = new ConcurrentHashMap<>();
        this.languageConfigs = new HashMap<>();

        // Verificar hooks
        setupHooks();

        // Carregar configurações
        loadConfig();

        // Registar listener
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Iniciar tasks
        startTasks();

        // Aplicar a jogadores online
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setupPlayer(player);
            }
        }, 20L);

        plugin.getLogger().info("VisualsManager iniciado! Scoreboard: " + scoreboardEnabled + " | Tab: " + tabEnabled);
    }

    // ==================== HOOKS ====================

    private void setupHooks() {
        placeholderApiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderApiEnabled) {
            plugin.getLogger().info("PlaceholderAPI detectado para Visuals!");
        }

        try {
            luckPerms = LuckPermsProvider.get();
            luckPermsEnabled = true;
            plugin.getLogger().info("LuckPerms detectado! Ordenação de Tab habilitada.");
        } catch (IllegalStateException | NoClassDefFoundError e) {
            luckPermsEnabled = false;
        }
    }

    // ==================== CONFIGURAÇÃO ====================

    private void loadConfig() {
        languageConfigs.clear();

        // Configurações globais do config.yml
        scoreboardEnabled = plugin.getConfig().getBoolean("visuals.scoreboard.enabled", true);
        scoreboardUpdateInterval = plugin.getConfig().getInt("visuals.scoreboard.update-interval", 20);

        tabEnabled = plugin.getConfig().getBoolean("visuals.tab.enabled", true);
        tabUpdateInterval = plugin.getConfig().getInt("visuals.tab.update-interval", 40);
        tabSortByGroup = plugin.getConfig().getBoolean("visuals.tab.sort-by-group", true);

        // Carregar de cada idioma
        for (String lang : plugin.getLangManager().getAvailableLanguages()) {
            VisualsConfig config = loadVisualsForLanguage(lang);
            if (config != null) {
                languageConfigs.put(lang, config);
                plugin.getLogger().info("Visuals carregados para idioma: " + lang +
                        " (Scoreboard: " + config.getScoreboardLines().size() + " linhas)");
            }
        }

        if (languageConfigs.isEmpty()) {
            plugin.getLogger().warning("Nenhuma configuração de visuals encontrada nos ficheiros de idioma!");
            scoreboardEnabled = false;
        } else {
            plugin.getLogger().info("Visuals carregados para " + languageConfigs.size() + " idiomas!");
        }
    }

    private VisualsConfig loadVisualsForLanguage(String lang) {
        try {
            File langFile = new File(plugin.getDataFolder(), "langs/" + lang + ".yml");
            if (!langFile.exists()) {
                plugin.getLogger().warning("Ficheiro de idioma não encontrado: " + langFile.getPath());
                return null;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(langFile);

            // Scoreboard
            String scoreboardTitle = config.getString("scoreboard.title", "");
            List<String> scoreboardLines = config.getStringList("scoreboard.lines");

            if (scoreboardTitle.isEmpty() && scoreboardLines.isEmpty()) {
                plugin.getLogger().warning("Scoreboard não configurada para idioma: " + lang);
            }

            // Tab
            List<String> tabHeader = config.getStringList("tab.header");
            List<String> tabFooter = config.getStringList("tab.footer");

            return new VisualsConfig(scoreboardTitle, scoreboardLines, tabHeader, tabFooter);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao carregar visuals para " + lang + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private VisualsConfig getConfigForPlayer(Player player) {
        String lang = plugin.getLangManager().getPlayerLanguage(player);
        VisualsConfig config = languageConfigs.get(lang);

        if (config == null) {
            config = languageConfigs.get(plugin.getLangManager().getDefaultLanguage());
        }

        // Fallback para primeiro disponível
        if (config == null && !languageConfigs.isEmpty()) {
            config = languageConfigs.values().iterator().next();
        }

        return config;
    }

    // ==================== TASKS ====================

    private void startTasks() {
        if (scoreboardEnabled && !languageConfigs.isEmpty()) {
            startScoreboardTask();
        }

        if (tabEnabled) {
            startTabTask();
        }
    }

    private void startScoreboardTask() {
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }

        scoreboardTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    updateScoreboard(player);
                } catch (Exception e) {
                    plugin.getLogger().warning("Erro ao atualizar scoreboard de " + player.getName() + ": " + e.getMessage());
                }
            }
        }, 40L, scoreboardUpdateInterval);

        plugin.getLogger().info("Task de scoreboard iniciada! Intervalo: " + scoreboardUpdateInterval + " ticks");
    }

    private void startTabTask() {
        if (tabTask != null) {
            tabTask.cancel();
        }

        tabTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    updateTab(player);
                    if (tabSortByGroup) {
                        updateTabList(player);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Erro ao atualizar tab de " + player.getName() + ": " + e.getMessage());
                }
            }
        }, 40L, tabUpdateInterval);

        plugin.getLogger().info("Task de tab iniciada! Intervalo: " + tabUpdateInterval + " ticks");
    }

    // ==================== SETUP JOGADOR ====================

    public void setupPlayer(Player player) {
        if (player == null || !player.isOnline()) return;

        plugin.getLogger().info("Configurando visuals para: " + player.getName());

        if (scoreboardEnabled) {
            createScoreboard(player);
        }

        if (tabEnabled) {
            updateTab(player);
            if (tabSortByGroup) {
                updateTabList(player);
            }
        }
    }

    public void removePlayer(Player player) {
        PlayerScoreboard ps = playerScoreboards.remove(player.getUniqueId());
        if (ps != null) {
            ps.remove(player);
        }
    }

    // ==================== SCOREBOARD ====================

    private void createScoreboard(Player player) {
        if (!scoreboardEnabled) return;

        VisualsConfig config = getConfigForPlayer(player);
        if (config == null || config.getScoreboardLines().isEmpty()) {
            plugin.getLogger().warning("Config de scoreboard não encontrada para: " + player.getName());
            return;
        }

        // Remover scoreboard antiga se existir
        PlayerScoreboard existing = playerScoreboards.get(player.getUniqueId());
        if (existing != null) {
            existing.remove(player);
        }

        // Criar nova scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            plugin.getLogger().warning("ScoreboardManager é null!");
            return;
        }

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("essentials", "dummy",
                ColorUtils.colorize(processPlaceholders(player, config.getScoreboardTitle())));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Criar entries para cada linha
        List<String> lines = config.getScoreboardLines();
        Map<Integer, Team> teams = new HashMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String teamName = "line_" + i;
            Team team = scoreboard.registerNewTeam(teamName);

            // Entry único para cada linha
            String entry = COLOR_CODES[i % COLOR_CODES.length];
            team.addEntry(entry);

            // Score invertido (linha 0 no topo)
            int score = lines.size() - i;
            objective.getScore(entry).setScore(score);

            teams.put(i, team);
        }

        // Guardar e aplicar
        PlayerScoreboard ps = new PlayerScoreboard(scoreboard, objective, teams, config);
        ps.setLanguage(plugin.getLangManager().getPlayerLanguage(player));
        playerScoreboards.put(player.getUniqueId(), ps);

        player.setScoreboard(scoreboard);

        // Atualizar conteúdo
        updateScoreboardContent(player, ps);

        plugin.getLogger().info("Scoreboard criada para: " + player.getName() + " com " + lines.size() + " linhas");
    }

    public void updateScoreboard(Player player) {
        if (!scoreboardEnabled || player == null || !player.isOnline()) return;

        PlayerScoreboard ps = playerScoreboards.get(player.getUniqueId());
        if (ps == null) {
            createScoreboard(player);
            return;
        }

        if (!ps.isVisible()) return;

        // Verificar se idioma mudou
        String currentLang = plugin.getLangManager().getPlayerLanguage(player);
        if (!currentLang.equals(ps.getLanguage())) {
            ps.setLanguage(currentLang);
            VisualsConfig newConfig = getConfigForPlayer(player);
            if (newConfig != null && newConfig.getScoreboardLines().size() != ps.getLineCount()) {
                // Recriar se número de linhas mudou
                createScoreboard(player);
                return;
            }
            ps.setConfig(newConfig);
        }

        updateScoreboardContent(player, ps);
    }

    private void updateScoreboardContent(Player player, PlayerScoreboard ps) {
        if (ps == null || ps.getConfig() == null) return;

        VisualsConfig config = ps.getConfig();

        // Atualizar título
        String title = processPlaceholders(player, config.getScoreboardTitle());
        title = ColorUtils.colorize(title);
        if (title.length() > 32) title = title.substring(0, 32);

        try {
            ps.getObjective().setDisplayName(title);
        } catch (Exception e) {
            // Ignorar se objective foi removido
        }

        // Atualizar linhas
        List<String> lines = config.getScoreboardLines();
        for (int i = 0; i < lines.size(); i++) {
            Team team = ps.getTeam(i);
            if (team == null) continue;

            String text = processPlaceholders(player, lines.get(i));
            text = ColorUtils.colorize(text);

            setTeamText(team, text);
        }
    }

    private void setTeamText(Team team, String text) {
        if (text == null) text = "";

        // Para versões 1.13+ o limite é 64, para anteriores é 16
        int maxLength = 64;

        try {
            if (text.length() <= maxLength) {
                team.setPrefix(text);
                team.setSuffix("");
            } else {
                String prefix = text.substring(0, maxLength);
                String suffix = text.substring(maxLength);

                // Manter cor
                String lastColor = ChatColor.getLastColors(prefix);
                if (!suffix.startsWith("§") && !lastColor.isEmpty()) {
                    suffix = lastColor + suffix;
                }

                if (suffix.length() > maxLength) {
                    suffix = suffix.substring(0, maxLength);
                }

                team.setPrefix(prefix);
                team.setSuffix(suffix);
            }
        } catch (Exception e) {
            // Ignorar erros de team já removido
        }
    }

    public void toggleScoreboard(Player player) {
        PlayerScoreboard ps = playerScoreboards.get(player.getUniqueId());
        if (ps == null) {
            createScoreboard(player);
            return;
        }

        if (ps.isVisible()) {
            // Esconder
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager != null) {
                player.setScoreboard(manager.getMainScoreboard());
            }
            ps.setVisible(false);
        } else {
            // Mostrar
            player.setScoreboard(ps.getScoreboard());
            ps.setVisible(true);
            updateScoreboardContent(player, ps);
        }
    }

    public boolean isScoreboardVisible(Player player) {
        PlayerScoreboard ps = playerScoreboards.get(player.getUniqueId());
        return ps != null && ps.isVisible();
    }

    // ==================== TAB ====================

    public void updateTab(Player player) {
        if (!tabEnabled || player == null || !player.isOnline()) return;

        VisualsConfig config = getConfigForPlayer(player);
        if (config == null) return;

        // Processar header
        String header = joinLines(config.getTabHeader());
        header = processPlaceholders(player, header);
        header = ColorUtils.colorize(header);

        // Processar footer
        String footer = joinLines(config.getTabFooter());
        footer = processPlaceholders(player, footer);
        footer = ColorUtils.colorize(footer);

        // Enviar usando API do Spigot (1.13+)
        try {
            player.setPlayerListHeaderFooter(
                    header.replace("\\n", "\n"),
                    footer.replace("\\n", "\n")
            );
        } catch (NoSuchMethodError e) {
            // Fallback para versões antigas via packets
            sendTabPacket(player, header, footer);
        }
    }

    public void updateTabList(Player player) {
        if (!tabEnabled || !tabSortByGroup) return;
        if (player == null || !player.isOnline()) return;

        Scoreboard scoreboard = player.getScoreboard();

        // Se o jogador está usando a scoreboard principal, usar a nossa
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        if (scoreboard == null || scoreboard.equals(manager.getMainScoreboard())) {
            // Verificar se já tem scoreboard do nosso sistema
            PlayerScoreboard ps = playerScoreboards.get(player.getUniqueId());
            if (ps != null && ps.getScoreboard() != null) {
                scoreboard = ps.getScoreboard();
                player.setScoreboard(scoreboard);
            } else {
                // Criar nova scoreboard para tab
                scoreboard = manager.getNewScoreboard();
                player.setScoreboard(scoreboard);
            }
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            String teamName = getTabTeamName(target);
            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            // Obter prefix do grupo
            String prefix = getPlayerPrefix(target);
            String colorizedPrefix = ChatColor.translateAlternateColorCodes('&', prefix);

            // Definir prefix no team (aparece antes do nome)
            if (colorizedPrefix.length() > 16) {
                team.setPrefix(colorizedPrefix.substring(0, 16));
            } else {
                team.setPrefix(colorizedPrefix);
            }

            // Adicionar jogador ao team
            if (!team.hasEntry(target.getName())) {
                // Remover de outros teams primeiro
                for (Team t : scoreboard.getTeams()) {
                    if (t.hasEntry(target.getName())) {
                        t.removeEntry(target.getName());
                    }
                }
                team.addEntry(target.getName());
            }

            // Definir nome na lista com prefix
            try {
                target.setPlayerListName(colorizedPrefix + target.getName());
            } catch (Exception ignored) {
            }
        }
    }

    private String getTabTeamName(Player player) {
        int weight = 0;

        if (luckPermsEnabled && luckPerms != null) {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user != null) {
                    weight = getGroupWeight(user.getPrimaryGroup());
                }
            } catch (Exception ignored) {
            }
        }

        // Formato: 000_NomeJogador (ordenação alfabética com peso)
        // 999 - weight = grupos com maior peso aparecem primeiro
        String weightStr = String.format("%03d", 999 - weight);
        String namePart = player.getName();
        if (namePart.length() > 8) {
            namePart = namePart.substring(0, 8);
        }

        return weightStr + "_" + namePart;
    }

    private int getGroupWeight(String groupName) {
        if (!luckPermsEnabled || luckPerms == null || groupName == null) return 0;

        try {
            var group = luckPerms.getGroupManager().getGroup(groupName);
            if (group == null) return 0;

            // Procurar peso do grupo
            for (var node : group.getNodes()) {
                String key = node.getKey();
                if (key.startsWith("weight.")) {
                    try {
                        return Integer.parseInt(key.substring(7));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }
            }

            // Alternativa: usar getWeight() se disponível
            var weight = group.getWeight();
            if (weight.isPresent()) {
                return weight.getAsInt();
            }

        } catch (Exception ignored) {
        }

        return 0;
    }

    private String getPlayerPrefix(Player player) {
        String prefix = "";

        // Tentar PlaceholderAPI primeiro (mais flexível)
        if (placeholderApiEnabled) {
            try {
                prefix = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");
                if (prefix != null && !prefix.isEmpty() && !prefix.equals("%luckperms_prefix%")) {
                    return prefix;
                }
            } catch (Exception ignored) {
            }
        }

        // Fallback para LuckPerms direto
        if (luckPermsEnabled && luckPerms != null) {
            try {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user != null) {
                    String lpPrefix = user.getCachedData().getMetaData().getPrefix();
                    if (lpPrefix != null && !lpPrefix.isEmpty()) {
                        return lpPrefix + " ";
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return "";
    }

    private void sendTabPacket(Player player, String header, String footer) {
        try {
            Object packet = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor().newInstance();

            Object headerComponent = serializeText(header.replace("\\n", "\n"));
            Object footerComponent = serializeText(footer.replace("\\n", "\n"));

            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, headerComponent);

            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footerComponent);

            sendPacket(player, packet);
        } catch (Exception e) {
            // Ignorar se não suportado
        }
    }

    private Object serializeText(String text) throws Exception {
        Class<?> chatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
        if (chatSerializer == null) {
            chatSerializer = getNMSClass("ChatSerializer");
        }

        String json = "{\"text\":\"" + text.replace("\"", "\\\"") + "\"}";
        return chatSerializer.getMethod("a", String.class).invoke(null, json);
    }

    private Class<?> getNMSClass(String name) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception ignored) {
        }
    }

    public void clearTab(Player player) {
        try {
            player.setPlayerListHeaderFooter("", "");
        } catch (NoSuchMethodError e) {
            sendTabPacket(player, "", "");
        }
    }

    // ==================== UTILIDADES ====================

    private String processPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) return "";

        // Placeholders internos
        text = text
                .replace("%player%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("%max%", String.valueOf(Bukkit.getMaxPlayers()))
                .replace("%world%", player.getWorld().getName())
                .replace("%ping%", String.valueOf(getPing(player)))
                .replace("%health%", String.valueOf((int) player.getHealth()))
                .replace("%max_health%", String.valueOf((int) player.getMaxHealth()))
                .replace("%food%", String.valueOf(player.getFoodLevel()))
                .replace("%level%", String.valueOf(player.getLevel()))
                .replace("%exp%", String.format("%.0f%%", player.getExp() * 100))
                .replace("%x%", String.valueOf(player.getLocation().getBlockX()))
                .replace("%y%", String.valueOf(player.getLocation().getBlockY()))
                .replace("%z%", String.valueOf(player.getLocation().getBlockZ()));

        // PlaceholderAPI
        if (placeholderApiEnabled) {
            try {
                text = PlaceholderAPI.setPlaceholders(player, text);
            } catch (Exception ignored) {
            }
        }

        return text;
    }

    private String joinLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            builder.append(lines.get(i));
            if (i < lines.size() - 1) {
                builder.append("\\n");
            }
        }
        return builder.toString();
    }

    private int getPing(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            return (int) handle.getClass().getField("ping").get(handle);
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== EVENTOS ====================

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) {
                setupPlayer(event.getPlayer());
            }
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    // ==================== GESTÃO ====================

    public void refreshPlayerLanguage(Player player) {
        if (scoreboardEnabled) {
            createScoreboard(player);
        }
        if (tabEnabled) {
            updateTab(player);
            if (tabSortByGroup) {
                updateTabList(player);
            }
        }
    }

    public void reload() {
        // Parar tasks
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
            scoreboardTask = null;
        }
        if (tabTask != null) {
            tabTask.cancel();
            tabTask = null;
        }

        // Limpar scoreboards
        for (Player player : Bukkit.getOnlinePlayers()) {
            removePlayer(player);
        }
        playerScoreboards.clear();

        // Recarregar
        loadConfig();
        startTasks();

        // Reaplicar
        for (Player player : Bukkit.getOnlinePlayers()) {
            setupPlayer(player);
        }
    }

    public void shutdown() {
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        if (tabTask != null) {
            tabTask.cancel();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard ps = playerScoreboards.get(player.getUniqueId());
            if (ps != null) {
                ps.remove(player);
            }
            clearTab(player);
        }

        playerScoreboards.clear();
    }

    // ==================== CLASSES INTERNAS ====================

    private static class VisualsConfig {
        private final String scoreboardTitle;
        private final List<String> scoreboardLines;
        private final List<String> tabHeader;
        private final List<String> tabFooter;

        public VisualsConfig(String scoreboardTitle, List<String> scoreboardLines,
                             List<String> tabHeader, List<String> tabFooter) {
            this.scoreboardTitle = scoreboardTitle != null ? scoreboardTitle : "";
            this.scoreboardLines = scoreboardLines != null ? scoreboardLines : new ArrayList<>();
            this.tabHeader = tabHeader != null ? tabHeader : new ArrayList<>();
            this.tabFooter = tabFooter != null ? tabFooter : new ArrayList<>();
        }

        public String getScoreboardTitle() {
            return scoreboardTitle;
        }

        public List<String> getScoreboardLines() {
            return scoreboardLines;
        }

        public List<String> getTabHeader() {
            return tabHeader;
        }

        public List<String> getTabFooter() {
            return tabFooter;
        }
    }

    private static class PlayerScoreboard {
        private final Scoreboard scoreboard;
        private final Objective objective;
        private final Map<Integer, Team> teams;
        private VisualsConfig config;
        private boolean visible;
        private String language;

        public PlayerScoreboard(Scoreboard scoreboard, Objective objective,
                                Map<Integer, Team> teams, VisualsConfig config) {
            this.scoreboard = scoreboard;
            this.objective = objective;
            this.teams = teams;
            this.config = config;
            this.visible = true;
            this.language = "";
        }

        public void remove(Player player) {
            try {
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                if (manager != null && player.isOnline()) {
                    player.setScoreboard(manager.getMainScoreboard());
                }
            } catch (Exception ignored) {
            }
        }

        public Scoreboard getScoreboard() {
            return scoreboard;
        }

        public Objective getObjective() {
            return objective;
        }

        public Team getTeam(int index) {
            return teams.get(index);
        }

        public int getLineCount() {
            return teams.size();
        }

        public VisualsConfig getConfig() {
            return config;
        }

        public void setConfig(VisualsConfig config) {
            this.config = config;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}