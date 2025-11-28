package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.api.LanguageAPI;
import github.dimazbtw.lib.utils.basics.ColorUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class LangManager implements LanguageAPI {
    private final Main plugin;
    private final Map<String, YamlConfiguration> languages = new HashMap<>();
    private final NamespacedKey langKey;
    private String defaultLang = "pt_PT";

    public LangManager(Main plugin) {
        this.plugin = plugin;
        this.langKey = new NamespacedKey(plugin, "player_language");
        loadLanguages();
    }

    private void loadLanguages() {
        File langFolder = new File(plugin.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
            createDefaultLanguages(langFolder);
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            languages.put(langCode, config);
            plugin.getLogger().info("Idioma carregado: " + langCode + " - " + config.getString("language.name", langCode));
        }

        plugin.getLogger().info("Carregados " + languages.size() + " idiomas!");
    }

    private void createDefaultLanguages(File langFolder) {
        // Renomear lang.yml existente para pt_PT.yml
        File existingLang = new File(plugin.getDataFolder(), "lang.yml");
        File ptPT = new File(langFolder, "pt_PT.yml");

        if (existingLang.exists()) {
            existingLang.renameTo(ptPT);
            YamlConfiguration ptConfig = YamlConfiguration.loadConfiguration(ptPT);

            // Adicionar informações de idioma
            ptConfig.set("language.name", "Português (Portugal)");
            ptConfig.set("language.flag", "🇵🇹");
            ptConfig.set("language.changed", "&aIdioma alterado para: &f{language}");
            ptConfig.set("language.current", "&eSeu idioma atual: &f{language}");
            ptConfig.set("language.not-found", "&cIdioma não encontrado!");
            ptConfig.set("language.available", "&eIdiomas disponíveis: &f{languages}");

            try {
                ptConfig.save(ptPT);
                plugin.getLogger().info("Arquivo lang.yml migrado para pt_PT.yml!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Criar en_US.yml
        File enUS = new File(langFolder, "en_US.yml");
        if (!enUS.exists()) {
            YamlConfiguration enConfig = new YamlConfiguration();

            // Language info
            enConfig.set("language.name", "English (United States)");
            enConfig.set("language.flag", "🇺🇸");
            enConfig.set("language.changed", "&aLanguage changed to: &f{language}");
            enConfig.set("language.current", "&eYour current language: &f{language}");
            enConfig.set("language.not-found", "&cLanguage not found!");
            enConfig.set("language.available", "&eAvailable languages: &f{languages}");

            // Prefixes
            enConfig.set("prefixes.main", "&f➤ ");
            enConfig.set("prefixes.broadcast", "&8[&c&lALERT&8] ");
            enConfig.set("prefixes.error", "&8[&c&lERROR&8] ");
            enConfig.set("prefixes.join", "&8[&a&l+&8] ");
            enConfig.set("prefixes.quit", "&8[&c&l-&8] ");

            // General messages
            enConfig.set("no-permission", "{prefix_error}&cYou don't have permission to execute this command!");
            enConfig.set("player-only", "{prefix_error}&cThis command can only be executed by players!");
            enConfig.set("player-not-found", "{prefix_error}&cPlayer not found!");
            enConfig.set("invalid-syntax", "{prefix_error}&cCorrect usage: {usage}");

            // Join/Quit
            enConfig.set("join-quit.join", "{prefix_join}&f{player} joined the server");
            enConfig.set("join-quit.quit", "{prefix_quit}&f{player} left the server");
            enConfig.set("join-quit.first-join", "\n{prefix_broadcast}&6Welcome &f{player} &6to the server!\n");

            // Gamemode
            enConfig.set("gamemode.changed-self", "{prefix_main}&aYour gamemode has been changed to &f{gamemode}");
            enConfig.set("gamemode.changed-other", "{prefix_main}&aYou changed &f{target}'s &agamemode to &f{gamemode}");
            enConfig.set("gamemode.target-changed", "{prefix_main}&aYour gamemode was changed to &f{gamemode} &aby &f{sender}");
            enConfig.set("gamemode.invalid-mode", "{prefix_error}&cInvalid gamemode! Use: survival, creative, adventure or spectator");

            // Menus
            enConfig.set("menus.reloaded", "&aMenus reloaded successfully! &7(&f{amount} menus in {time}ms&7)");
            enConfig.set("menus.usage", "&cCorrect usage: &f/menus open <menu>");
            enConfig.set("menus.not-found", "&cMenu '{menu}' not found!");
            enConfig.set("menus.opened", "&aOpening menu '{menu}'...");

            // TP
            enConfig.set("tp.teleported-to-player", "§aTeleported to §e{target}§a.");
            enConfig.set("tp.teleported-player-to-you", "§aPlayer §e{target}§a teleported to you.");
            enConfig.set("tp.teleported-to-coords", "§aTeleported to §e{x}§a, §e{y}§a, §e{z}§a in world §e{world}§a.");
            enConfig.set("tp.self-teleport", "§cYou cannot teleport to yourself.");
            enConfig.set("tp.invalid-coordinates", "§cInvalid coordinates. Use numbers.");
            enConfig.set("tp.usage", "§cUsage: /tp <player> | /tp <x> <y> <z> | /tp <x> <y> <z> <world>");

            enConfig.set("tphere.usage", "§cUsage: /tphere <player>");

            enConfig.set("tpall.teleported", "§aYou were teleported to §e{player}§a.");
            enConfig.set("tpall.teleported-all", "§aTeleported §e{count}§a players to your location.");

            // TPA
            enConfig.set("tpa.error.self", "&cYou cannot send a teleport request to yourself!");
            enConfig.set("tpa.error.disabled", "&cThis player has disabled receiving teleport requests!");
            enConfig.set("tpa.error.pending", "&cThis player already has a pending teleport request!");
            enConfig.set("tpa.error.no-request", "&cYou don't have any pending teleport requests!");
            enConfig.set("tpa.error.offline", "&cThe player is no longer online!");
            enConfig.set("tpa.sent", "&aTeleport request sent to {player}");
            enConfig.set("tpa.request.title", "&eTeleport request from {player}");
            enConfig.set("tpa.request.accept.text", "&a[ACCEPT]");
            enConfig.set("tpa.request.accept.hover", "&aClick to accept");
            enConfig.set("tpa.request.deny.text", "&c[DENY]");
            enConfig.set("tpa.request.deny.hover", "&cClick to deny");
            enConfig.set("tpa.accepted.sender", "&aTeleport accepted by {player}");
            enConfig.set("tpa.accepted.target", "&aYou accepted the teleport from {player}");
            enConfig.set("tpa.denied.sender", "&c{player} denied your teleport request!");
            enConfig.set("tpa.denied.target", "&cYou denied the teleport request!");
            enConfig.set("tpa.toggle.enabled", "&aYou enabled receiving teleport requests!");
            enConfig.set("tpa.toggle.disabled", "&cYou disabled receiving teleport requests!");
            enConfig.set("tpa.expired.sender", "&cYour teleport request to {player} expired!");
            enConfig.set("tpa.expired.target", "&cThe teleport request from {player} expired!");

            // Heal/Feed
            enConfig.set("heal.healed-self", "{prefix_main}&aYou have been healed!");
            enConfig.set("heal.healed-other", "{prefix_main}&aYou healed player &f{target}");
            enConfig.set("heal.target-healed", "{prefix_main}&aYou were healed by &f{sender}");

            enConfig.set("feed.fed-self", "{prefix_main}&aYou have been fed!");
            enConfig.set("feed.fed-other", "{prefix_main}&aYou fed player &f{target}");
            enConfig.set("feed.target-fed", "{prefix_main}&aYou were fed by &f{sender}");

            // Broadcast
            enConfig.set("broadcast.format", "\n{prefix_broadcast}&f{message}\n");
            enConfig.set("broadcast.no-message", "{prefix_error}&cYou need to type a message!");

            // Night Vision
            enConfig.set("night-vision.enabled", "{prefix_main}&aNight vision enabled!");
            enConfig.set("night-vision.disabled", "{prefix_main}&cNight vision disabled!");

            // Speed
            enConfig.set("speed.changed", "{prefix_main}&aSpeed changed to &f{speed}");
            enConfig.set("speed.reset", "{prefix_main}&aSpeed restored to default value!");
            enConfig.set("speed.invalid-speed", "{prefix_error}&cSpeed must be between 1 and 10!");
            enConfig.set("speed.invalid-input", "{prefix_error}&cPlease enter a number between 1 and 10 or 'clear'!");

            // Player Info
            enConfig.set("playerinfo.format", Arrays.asList(
                    "\n&8=-=-=-=-= &e&lPLAYER INFORMATION &8=-=-=-=-=\n",
                    "&fNickname: &e{nick}",
                    "&fUUID: &7{uuid}",
                    "&fIP: &7{ip}",
                    "&fFirst Join: &7{date}",
                    "&fLast Quit: &7{date}",
                    "&fCurrent World: &7{world}",
                    "&fHealth: &7{health}/{max_health}",
                    "&fHunger: &7{food}/20",
                    "&fGamemode: &7{gamemode}",
                    "&fStatus: {status}",
                    "\n&8=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="
            ));

            // Spawn
            enConfig.set("spawn.not-set", "{prefix_error}&cThe spawn has not been set yet!");
            enConfig.set("spawn.set", "{prefix_main}&aSpawn point has been successfully set at &f{world} &7({x}, {y}, {z})");
            enConfig.set("spawn.teleported", "{prefix_main}&aYou have been teleported to spawn!");
            enConfig.set("spawn.teleported-by", "{prefix_main}&aYou were teleported to spawn by &f{sender}");

            // World Protection
            enConfig.set("world-protection.world-not-found", "{prefix_error}World not found!");
            enConfig.set("world-protection.toggle", "{prefix_main}Protection {protection} {state} for world {world}");

            // Player Protection
            enConfig.set("player-protection.flight-disabled", "{prefix_error}Flight mode is disabled in world {world}!");
            enConfig.set("player-protection.creative-disabled", "{prefix_error}Creative mode is disabled in this world!");
            enConfig.set("player-protection.void-teleport", "{prefix_main}You were teleported to spawn to avoid death in void!");

            enConfig.set("container-access-denied", "{prefix_error}You cannot open this container here!");

            // Fly
            enConfig.set("fly.toggled-self", "{prefix_main}&aFlight mode {state}!");
            enConfig.set("fly.toggled-other", "{prefix_main}&aYou {state} &f{target}'s flight mode");
            enConfig.set("fly.target-toggled", "{prefix_main}&aYour flight mode was {state} by &f{sender}");
            enConfig.set("fly.world-disabled", "{prefix_error}&cFlight mode is disabled in this world!");

            // Kit Messages
            enConfig.set("kit.created", "&aKit &f{kit} &acreated successfully!");
            enConfig.set("kit.deleted", "&cKit &f{kit} &cdeleted!");
            enConfig.set("kit.no-permission", "&cYou don't have permission to get this kit!");
            enConfig.set("kit.cooldown", "&cWait &f{time} &cto get this kit again!");
            enConfig.set("kit.received", "&aYou received the kit {kit}&a!");
            enConfig.set("kit.given", "&aYou gave the kit {kit} &ato &f{player}&a!");

            try {
                enConfig.save(enUS);
                plugin.getLogger().info("Arquivo en_US.yml criado!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== IMPLEMENTAÇÃO DA API ====================

    @Override
    public String getPlayerLanguage(Player player) {
        if (player == null) return defaultLang;
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        String lang = pdc.get(langKey, PersistentDataType.STRING);

        if (lang == null || !languages.containsKey(lang)) {
            return defaultLang;
        }

        return lang;
    }

    @Override
    public boolean setPlayerLanguage(Player player, String langCode) {
        if (player == null || !languages.containsKey(langCode)) {
            return false;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(langKey, PersistentDataType.STRING, langCode);
        return true;
    }

    @Override
    public String getLanguageName(String langCode) {
        YamlConfiguration config = languages.get(langCode);
        if (config == null) return langCode;
        return config.getString("language.name", langCode);
    }

    @Override
    public String getLanguageFlag(String langCode) {
        YamlConfiguration config = languages.get(langCode);
        if (config == null) return "";
        return config.getString("language.flag", "");
    }

    @Override
    public boolean languageExists(String langCode) {
        return languages.containsKey(langCode);
    }

    @Override
    public String getMessage(Player player, String key) {
        return getMessage(player, key, new HashMap<>());
    }

    @Override
    public String getMessage(Player player, String key, Map<String, String> placeholders) {
        String lang = getPlayerLanguage(player);
        YamlConfiguration config = languages.get(lang);

        if (config == null) {
            config = languages.get(defaultLang);
        }

        String message = config.getString(key);

        if (message == null) {
            config = languages.get(defaultLang);
            message = config != null ? config.getString(key, "&cMensagem não encontrada: " + key) : "&cMensagem não encontrada: " + key;
        }

        message = processPrefix(config, message);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return ColorUtils.colorize(message);
    }

    @Override
    public String getDefaultLanguage() {
        return defaultLang;
    }

    @Override
    public String[] getAvailableLanguages() {
        return languages.keySet().toArray(new String[0]);
    }

    // ==================== MÉTODOS PÚBLICOS ADICIONAIS ====================

    /**
     * Backward compatibility: getMessage sem player (usa idioma padrão)
     */
    public String getMessage(String key) {
        return getMessage(key, new HashMap<>());
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        YamlConfiguration config = languages.get(defaultLang);

        if (config == null) {
            return "&cIdioma padrão não encontrado!";
        }

        String message = config.getString(key, "&cMensagem não encontrada: " + key);
        message = processPrefix(config, message);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return ColorUtils.colorize(message);
    }

    /**
     * Pega lista de mensagens no idioma do jogador
     */
    public List<String> getMessageList(Player player, String key, Map<String, String> placeholders) {
        String lang = getPlayerLanguage(player);
        YamlConfiguration config = languages.get(lang);

        if (config == null) {
            config = languages.get(defaultLang);
        }

        List<String> messages = config.getStringList(key);

        if (messages.isEmpty()) {
            config = languages.get(defaultLang);
            messages = config != null ? config.getStringList(key) : new ArrayList<>();
        }

        List<String> processed = new ArrayList<>();
        for (String message : messages) {
            message = processPrefix(config, message);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }

            processed.add(ColorUtils.colorize(message));
        }

        return processed;
    }

    /**
     * Lista idiomas disponíveis como Set
     */
    public Set<String> getAvailableLanguagesSet() {
        return languages.keySet();
    }

    /**
     * Recarregar idiomas
     */
    public void reload() {
        languages.clear();
        loadLanguages();
    }

    /**
     * Getter do idioma padrão
     */
    public String getDefaultLang() {
        return defaultLang;
    }

    /**
     * Setter do idioma padrão
     */
    public void setDefaultLang(String defaultLang) {
        if (languages.containsKey(defaultLang)) {
            this.defaultLang = defaultLang;
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private String processPrefix(YamlConfiguration config, String message) {
        if (message.contains("{prefix_")) {
            message = message.replace("{prefix_main}", config.getString("prefixes.main", ""));
            message = message.replace("{prefix_broadcast}", config.getString("prefixes.broadcast", ""));
            message = message.replace("{prefix_error}", config.getString("prefixes.error", ""));
            message = message.replace("{prefix_join}", config.getString("prefixes.join", ""));
            message = message.replace("{prefix_quit}", config.getString("prefixes.quit", ""));
        }
        return message;
    }
}