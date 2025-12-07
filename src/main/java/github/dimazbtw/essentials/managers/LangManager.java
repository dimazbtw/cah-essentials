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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
        File langFolder = new File(plugin.getDataFolder(), "langs");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
            saveDefaultLanguages();
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            saveDefaultLanguages();
            files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        }

        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            languages.put(langCode, config);
            plugin.getLogger().info("Idioma carregado: " + langCode + " - " + config.getString("language.name", langCode));
        }

        String configDefault = plugin.getConfig().getString("language.default", "pt_PT");
        if (languages.containsKey(configDefault)) {
            defaultLang = configDefault;
        }

        plugin.getLogger().info("Carregados " + languages.size() + " idiomas! Padrão: " + defaultLang);
    }

    private void saveDefaultLanguages() {
        saveResourceLang("langs/pt_PT.yml");
        saveResourceLang("langs/en_US.yml");
    }

    private void saveResourceLang(String resourcePath) {
        File langFile = new File(plugin.getDataFolder(), resourcePath);

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();

            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in != null) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                    config.save(langFile);
                    plugin.getLogger().info("Arquivo de idioma criado: " + resourcePath);
                } else {
                    createDefaultLangFile(langFile, resourcePath);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Erro ao salvar arquivo de idioma: " + resourcePath);
                e.printStackTrace();
            }
        }
    }

    private void createDefaultLangFile(File file, String resourcePath) {
        YamlConfiguration config = new YamlConfiguration();
        String langCode = file.getName().replace(".yml", "");

        if (langCode.equals("pt_PT")) {
            createPortugueseConfig(config);
        } else if (langCode.equals("en_US")) {
            createEnglishConfig(config);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPortugueseConfig(YamlConfiguration config) {
        config.set("language.name", "Português (Portugal)");
        config.set("language.flag", "🇵🇹");
        config.set("language.changed", "&aIdioma alterado para: &f{language}");
        config.set("language.current", "&eSeu idioma atual: &f{language}");
        config.set("language.not-found", "&cIdioma não encontrado!");
        config.set("language.available", "&eIdiomas disponíveis: &f{languages}");
        config.set("language.list-header", "&e&l━━━ Idiomas Disponíveis ━━━");
        config.set("language.list-item", "&8• &f{code} &7- &e{name} {flag}");
        config.set("language.list-current", "&a(atual)");

        config.set("prefixes.main", "&f➤ ");
        config.set("prefixes.broadcast", "&8[&c&lALERTA&8] ");
        config.set("prefixes.error", "&8[&c&lERRO&8] ");
        config.set("prefixes.join", "&8[&a&l+&8] ");
        config.set("prefixes.quit", "&8[&c&l-&8] ");

        config.set("no-permission", "{prefix_error}&cVocê não tem permissão para executar este comando!");
        config.set("player-only-command", "{prefix_error}&cEste comando só pode ser executado por jogadores!");
        config.set("player-not-found", "{prefix_error}&cJogador não encontrado!");
        config.set("invalid-syntax", "{prefix_error}&cUso correto: {usage}");
        config.set("world-not-found", "{prefix_error}&cMundo não encontrado!");

        config.set("gamemodes.survival", "Sobrevivência");
        config.set("gamemodes.creative", "Criativo");
        config.set("gamemodes.adventure", "Aventura");
        config.set("gamemodes.spectator", "Espectador");

        config.set("join-quit.join", "{prefix_join}&f{player} entrou no servidor");
        config.set("join-quit.quit", "{prefix_quit}&f{player} saiu do servidor");
        config.set("join-quit.first-join", "\n{prefix_broadcast}&6Bem-vindo(a) &f{player} &6ao servidor!\n");

        config.set("gamemode.changed-self", "{prefix_main}&aSeu modo de jogo foi alterado para &f{gamemode}");
        config.set("gamemode.changed-other", "{prefix_main}&aVocê alterou o modo de jogo de &f{target} &apara &f{gamemode}");
        config.set("gamemode.target-changed", "{prefix_main}&aSeu modo de jogo foi alterado para &f{gamemode} &apor &f{sender}");
        config.set("gamemode.invalid-mode", "{prefix_error}&cModo de jogo inválido! Use: survival, creative, adventure ou spectator");

        config.set("menus.reloaded", "&aMenus recarregados com sucesso! &7(&f{amount} menus em {time}ms&7)");
        config.set("menus.usage", "&cUso correto: &f/menus abrir <menu>");
        config.set("menus.not-found", "&cMenu '{menu}' não encontrado!");
        config.set("menus.opened", "&aAbrindo menu '{menu}'...");

        config.set("tp.teleported-to-player", "§aTeleportado para §e{target}§a.");
        config.set("tp.teleported-player-to-you", "§aJogador §e{target}§a teleportado para você.");
        config.set("tp.teleported-to-coords", "§aTeleportado para §e{x}§a, §e{y}§a, §e{z}§a no mundo §e{world}§a.");
        config.set("tp.self-teleport", "§cVocê não pode se teleportar para si mesmo.");
        config.set("tp.invalid-coordinates", "§cCoordenadas inválidas. Use números.");
        config.set("tp.usage", "§cUso: /tp <jogador> | /tp <x> <y> <z> | /tp <x> <y> <z> <mundo>");

        config.set("tphere.usage", "§cUso: /tphere <jogador>");

        config.set("tpall.teleported", "§aVocê foi teleportado para §e{player}§a.");
        config.set("tpall.teleported-all", "§aTeleportados §e{count}§a jogadores para sua localização.");

        config.set("tpa.error.self", "&cVocê não pode enviar um pedido de teleporte para si mesmo!");
        config.set("tpa.error.disabled", "&cEste jogador desativou o recebimento de pedidos de teleporte!");
        config.set("tpa.error.pending", "&cEste jogador já possui um pedido de teleporte pendente!");
        config.set("tpa.error.no-request", "&cVocê não possui pedidos de teleporte pendentes!");
        config.set("tpa.error.offline", "&cO jogador não está mais online!");
        config.set("tpa.sent", "&aPedido de teleporte enviado para {player}");
        config.set("tpa.request.title", "&ePedido de teleporte de {player}");
        config.set("tpa.request.accept.text", "&a[ACEITAR]");
        config.set("tpa.request.accept.hover", "&aClique para aceitar");
        config.set("tpa.request.deny.text", "&c[RECUSAR]");
        config.set("tpa.request.deny.hover", "&cClique para recusar");
        config.set("tpa.accepted.sender", "&aTeleporte aceito por {player}");
        config.set("tpa.accepted.target", "&aVocê aceitou o teleporte de {player}");
        config.set("tpa.denied.sender", "&c{player} recusou seu pedido de teleporte!");
        config.set("tpa.denied.target", "&cVocê recusou o pedido de teleporte!");
        config.set("tpa.toggle.enabled", "&aVocê ativou o recebimento de pedidos de teleporte!");
        config.set("tpa.toggle.disabled", "&cVocê desativou o recebimento de pedidos de teleporte!");
        config.set("tpa.expired.sender", "&cSeu pedido de teleporte para {player} expirou!");
        config.set("tpa.expired.target", "&cO pedido de teleporte de {player} expirou!");

        config.set("heal.healed-self", "{prefix_main}&aVocê foi curado!");
        config.set("heal.healed-other", "{prefix_main}&aVocê curou o jogador &f{target}");
        config.set("heal.target-healed", "{prefix_main}&aVocê foi curado por &f{sender}");

        config.set("feed.fed-self", "{prefix_main}&aVocê foi alimentado!");
        config.set("feed.fed-other", "{prefix_main}&aVocê alimentou o jogador &f{target}");
        config.set("feed.target-fed", "{prefix_main}&aVocê foi alimentado por &f{sender}");

        config.set("broadcast.format", "\n{prefix_broadcast}&f{message}\n");
        config.set("broadcast.no-message", "{prefix_error}&cVocê precisa digitar uma mensagem!");

        config.set("night-vision.enabled", "{prefix_main}&aVisão noturna ativada!");
        config.set("night-vision.disabled", "{prefix_main}&cVisão noturna desativada!");

        config.set("speed.changed", "{prefix_main}&aVelocidade alterada para &f{speed}");
        config.set("speed.reset", "{prefix_main}&aVelocidade restaurada para o valor padrão!");
        config.set("speed.invalid-speed", "{prefix_error}&cA velocidade deve estar entre 1 e 10!");
        config.set("speed.invalid-input", "{prefix_error}&cPor favor, insira um número entre 1 e 10 ou 'clear'!");

        config.set("playerinfo.format", Arrays.asList(
                "\n&8=-=-=-=-= &e&lINFORMAÇÕES DO JOGADOR &8=-=-=-=-=\n",
                "&fNick: &e{nick}",
                "&fUUID: &7{uuid}",
                "&fIP: &7{ip}",
                "&fPrimeira Entrada: &7{date}",
                "&fMundo Atual: &7{world}",
                "&fVida: &7{health}/{max_health}",
                "&fFome: &7{food}/20",
                "&fModo de Jogo: &7{gamemode}",
                "&fStatus: {status}",
                "\n&8=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="
        ));

        config.set("spawn.not-set", "{prefix_error}&cO spawn ainda não foi definido!");
        config.set("spawn.set", "{prefix_main}&aO ponto de spawn foi definido com sucesso em &f{world} &7({x}, {y}, {z})");
        config.set("spawn.teleported", "{prefix_main}&aVocê foi teleportado para o spawn!");
        config.set("spawn.teleported-by", "{prefix_main}&aVocê foi teleportado para o spawn por &f{sender}");
        config.set("spawn.teleported-void", "{prefix_main}&aVocê foi teleportado para o spawn para evitar morte no void!");

        config.set("fly.toggled-self", "{prefix_main}&aModo de voo {state}!");
        config.set("fly.toggled-other", "{prefix_main}&aVocê {state} o modo de voo de &f{target}");
        config.set("fly.target-toggled", "{prefix_main}&aSeu modo de voo foi {state} por &f{sender}");
        config.set("fly.world-disabled", "{prefix_error}&cO modo de voo está desativado neste mundo!");

        config.set("kit.created", "&aKit &f{kit} &acriado com sucesso!");
        config.set("kit.deleted", "&cKit &f{kit} &cdeletado!");
        config.set("kit.no-permission", "&cVocê não tem permissão para pegar este kit!");
        config.set("kit.cooldown", "&cAguarde &f{time} &cpara pegar este kit novamente!");
        config.set("kit.received", "&aVocê recebeu o kit {kit}&a!");
        config.set("kit.given", "&aVocê deu o kit {kit} &apara &f{player}&a!");
        config.set("kit.not-found", "&cKit não encontrado!");
        config.set("kit.error", "&cErro ao resgatar o kit!");

        config.set("kit.menu.not-exists", "&cKit não existe");
        config.set("kit.menu.no-permission", "&cSem permissão para este kit");
        config.set("kit.menu.cooldown-title", "&cPodes resgatar este kit em:");
        config.set("kit.menu.cooldown-time", "&f{time}");
        config.set("kit.menu.available", "&a✓ Disponível para resgate");
        config.set("kit.menu.click-to-claim", "&eBotão esquerdo para resgatar");

        config.set("kit.preview.title", "Preview");
        config.set("kit.preview.item-lore", "&7Preview do kit");
        config.set("kit.preview.back", "&cVoltar");
        config.set("kit.preview.back-lore", "&7Clique para voltar");
        config.set("kit.preview.close", "&c&lFechar");
        config.set("kit.preview.close-lore", "&7Clique para fechar");
        config.set("kit.preview.info-title", "&e&lInformações do Kit");
        config.set("kit.preview.info-name", "&fNome: {name}");
        config.set("kit.preview.info-delay", "&fDelay: {delay}");
        config.set("kit.preview.info-cooldown", "&cCooldown: &f{time}");
        config.set("kit.preview.info-available", "&a✓ Disponível para resgatar!");
    }

    private void createEnglishConfig(YamlConfiguration config) {
        config.set("language.name", "English (United States)");
        config.set("language.flag", "🇺🇸");
        config.set("language.changed", "&aLanguage changed to: &f{language}");
        config.set("language.current", "&eYour current language: &f{language}");
        config.set("language.not-found", "&cLanguage not found!");
        config.set("language.available", "&eAvailable languages: &f{languages}");
        config.set("language.list-header", "&e&l━━━ Available Languages ━━━");
        config.set("language.list-item", "&8• &f{code} &7- &e{name} {flag}");
        config.set("language.list-current", "&a(current)");

        config.set("prefixes.main", "&f➤ ");
        config.set("prefixes.broadcast", "&8[&c&lALERT&8] ");
        config.set("prefixes.error", "&8[&c&lERROR&8] ");
        config.set("prefixes.join", "&8[&a&l+&8] ");
        config.set("prefixes.quit", "&8[&c&l-&8] ");

        config.set("no-permission", "{prefix_error}&cYou don't have permission to execute this command!");
        config.set("player-only-command", "{prefix_error}&cThis command can only be executed by players!");
        config.set("player-not-found", "{prefix_error}&cPlayer not found!");
        config.set("invalid-syntax", "{prefix_error}&cCorrect usage: {usage}");
        config.set("world-not-found", "{prefix_error}&cWorld not found!");

        config.set("gamemodes.survival", "Survival");
        config.set("gamemodes.creative", "Creative");
        config.set("gamemodes.adventure", "Adventure");
        config.set("gamemodes.spectator", "Spectator");

        config.set("join-quit.join", "{prefix_join}&f{player} joined the server");
        config.set("join-quit.quit", "{prefix_quit}&f{player} left the server");
        config.set("join-quit.first-join", "\n{prefix_broadcast}&6Welcome &f{player} &6to the server!\n");

        config.set("gamemode.changed-self", "{prefix_main}&aYour gamemode has been changed to &f{gamemode}");
        config.set("gamemode.changed-other", "{prefix_main}&aYou changed &f{target}'s &agamemode to &f{gamemode}");
        config.set("gamemode.target-changed", "{prefix_main}&aYour gamemode was changed to &f{gamemode} &aby &f{sender}");
        config.set("gamemode.invalid-mode", "{prefix_error}&cInvalid gamemode! Use: survival, creative, adventure or spectator");

        config.set("menus.reloaded", "&aMenus reloaded successfully! &7(&f{amount} menus in {time}ms&7)");
        config.set("menus.usage", "&cCorrect usage: &f/menus open <menu>");
        config.set("menus.not-found", "&cMenu '{menu}' not found!");
        config.set("menus.opened", "&aOpening menu '{menu}'...");

        config.set("tp.teleported-to-player", "§aTeleported to §e{target}§a.");
        config.set("tp.teleported-player-to-you", "§aPlayer §e{target}§a teleported to you.");
        config.set("tp.teleported-to-coords", "§aTeleported to §e{x}§a, §e{y}§a, §e{z}§a in world §e{world}§a.");
        config.set("tp.self-teleport", "§cYou cannot teleport to yourself.");
        config.set("tp.invalid-coordinates", "§cInvalid coordinates. Use numbers.");
        config.set("tp.usage", "§cUsage: /tp <player> | /tp <x> <y> <z> | /tp <x> <y> <z> <world>");

        config.set("tphere.usage", "§cUsage: /tphere <player>");

        config.set("tpall.teleported", "§aYou were teleported to §e{player}§a.");
        config.set("tpall.teleported-all", "§aTeleported §e{count}§a players to your location.");

        config.set("tpa.error.self", "&cYou cannot send a teleport request to yourself!");
        config.set("tpa.error.disabled", "&cThis player has disabled receiving teleport requests!");
        config.set("tpa.error.pending", "&cThis player already has a pending teleport request!");
        config.set("tpa.error.no-request", "&cYou don't have any pending teleport requests!");
        config.set("tpa.error.offline", "&cThe player is no longer online!");
        config.set("tpa.sent", "&aTeleport request sent to {player}");
        config.set("tpa.request.title", "&eTeleport request from {player}");
        config.set("tpa.request.accept.text", "&a[ACCEPT]");
        config.set("tpa.request.accept.hover", "&aClick to accept");
        config.set("tpa.request.deny.text", "&c[DENY]");
        config.set("tpa.request.deny.hover", "&cClick to deny");
        config.set("tpa.accepted.sender", "&aTeleport accepted by {player}");
        config.set("tpa.accepted.target", "&aYou accepted the teleport from {player}");
        config.set("tpa.denied.sender", "&c{player} denied your teleport request!");
        config.set("tpa.denied.target", "&cYou denied the teleport request!");
        config.set("tpa.toggle.enabled", "&aYou enabled receiving teleport requests!");
        config.set("tpa.toggle.disabled", "&cYou disabled receiving teleport requests!");
        config.set("tpa.expired.sender", "&cYour teleport request to {player} expired!");
        config.set("tpa.expired.target", "&cThe teleport request from {player} expired!");

        config.set("heal.healed-self", "{prefix_main}&aYou have been healed!");
        config.set("heal.healed-other", "{prefix_main}&aYou healed player &f{target}");
        config.set("heal.target-healed", "{prefix_main}&aYou were healed by &f{sender}");

        config.set("feed.fed-self", "{prefix_main}&aYou have been fed!");
        config.set("feed.fed-other", "{prefix_main}&aYou fed player &f{target}");
        config.set("feed.target-fed", "{prefix_main}&aYou were fed by &f{sender}");

        config.set("broadcast.format", "\n{prefix_broadcast}&f{message}\n");
        config.set("broadcast.no-message", "{prefix_error}&cYou need to type a message!");

        config.set("night-vision.enabled", "{prefix_main}&aNight vision enabled!");
        config.set("night-vision.disabled", "{prefix_main}&cNight vision disabled!");

        config.set("speed.changed", "{prefix_main}&aSpeed changed to &f{speed}");
        config.set("speed.reset", "{prefix_main}&aSpeed restored to default value!");
        config.set("speed.invalid-speed", "{prefix_error}&cSpeed must be between 1 and 10!");
        config.set("speed.invalid-input", "{prefix_error}&cPlease enter a number between 1 and 10 or 'clear'!");

        config.set("playerinfo.format", Arrays.asList(
                "\n&8=-=-=-=-= &e&lPLAYER INFORMATION &8=-=-=-=-=\n",
                "&fNickname: &e{nick}",
                "&fUUID: &7{uuid}",
                "&fIP: &7{ip}",
                "&fFirst Join: &7{date}",
                "&fCurrent World: &7{world}",
                "&fHealth: &7{health}/{max_health}",
                "&fHunger: &7{food}/20",
                "&fGamemode: &7{gamemode}",
                "&fStatus: {status}",
                "\n&8=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="
        ));

        config.set("spawn.not-set", "{prefix_error}&cThe spawn has not been set yet!");
        config.set("spawn.set", "{prefix_main}&aSpawn point has been successfully set at &f{world} &7({x}, {y}, {z})");
        config.set("spawn.teleported", "{prefix_main}&aYou have been teleported to spawn!");
        config.set("spawn.teleported-by", "{prefix_main}&aYou were teleported to spawn by &f{sender}");
        config.set("spawn.teleported-void", "{prefix_main}&aYou were teleported to spawn to avoid death in void!");

        config.set("fly.toggled-self", "{prefix_main}&aFlight mode {state}!");
        config.set("fly.toggled-other", "{prefix_main}&aYou {state} &f{target}'s flight mode");
        config.set("fly.target-toggled", "{prefix_main}&aYour flight mode was {state} by &f{sender}");
        config.set("fly.world-disabled", "{prefix_error}&cFlight mode is disabled in this world!");

        config.set("kit.created", "&aKit &f{kit} &acreated successfully!");
        config.set("kit.deleted", "&cKit &f{kit} &cdeleted!");
        config.set("kit.no-permission", "&cYou don't have permission to get this kit!");
        config.set("kit.cooldown", "&cWait &f{time} &cto get this kit again!");
        config.set("kit.received", "&aYou received the kit {kit}&a!");
        config.set("kit.given", "&aYou gave the kit {kit} &ato &f{player}&a!");
        config.set("kit.not-found", "&cKit not found!");
        config.set("kit.error", "&cError claiming the kit!");

        config.set("kit.menu.not-exists", "&cKit doesn't exist");
        config.set("kit.menu.no-permission", "&cNo permission for this kit");
        config.set("kit.menu.cooldown-title", "&cYou can claim this kit in:");
        config.set("kit.menu.cooldown-time", "&f{time}");
        config.set("kit.menu.available", "&a✓ Available to claim");
        config.set("kit.menu.click-to-claim", "&eLeft-Click to claim");

        config.set("kit.preview.title", "Preview");
        config.set("kit.preview.item-lore", "&7Kit preview");
        config.set("kit.preview.back", "&cBack");
        config.set("kit.preview.back-lore", "&7Click to go back");
        config.set("kit.preview.close", "&c&lClose");
        config.set("kit.preview.close-lore", "&7Click to close");
        config.set("kit.preview.info-title", "&e&lKit Information");
        config.set("kit.preview.info-name", "&fName: {name}");
        config.set("kit.preview.info-delay", "&fDelay: {delay}");
        config.set("kit.preview.info-cooldown", "&cCooldown: &f{time}");
        config.set("kit.preview.info-available", "&a✓ Available to claim!");
    }

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

        if (message == null && !lang.equals(defaultLang)) {
            config = languages.get(defaultLang);
            message = config != null ? config.getString(key) : null;
        }

        if (message == null) {
            return "&cMensagem não encontrada: " + key;
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

    public List<String> getMessageList(Player player, String key, Map<String, String> placeholders) {
        String lang = getPlayerLanguage(player);
        YamlConfiguration config = languages.get(lang);

        if (config == null) {
            config = languages.get(defaultLang);
        }

        List<String> messages = config.getStringList(key);

        if (messages.isEmpty() && !lang.equals(defaultLang)) {
            config = languages.get(defaultLang);
            messages = config != null ? config.getStringList(key) : new ArrayList<>();
        }

        List<String> processed = new ArrayList<>();
        YamlConfiguration finalConfig = config;
        for (String message : messages) {
            message = processPrefix(finalConfig, message);

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }

            processed.add(ColorUtils.colorize(message));
        }

        return processed;
    }

    public List<String> getStringList(String key, Map<String, String> placeholders) {
        YamlConfiguration config = languages.get(defaultLang);

        if (config == null) {
            return new ArrayList<>();
        }

        List<String> messages = config.getStringList(key);
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

    public Set<String> getAvailableLanguagesSet() {
        return languages.keySet();
    }

    public Map<String, LanguageInfo> getLanguagesInfo() {
        Map<String, LanguageInfo> info = new HashMap<>();
        for (String code : languages.keySet()) {
            info.put(code, new LanguageInfo(code, getLanguageName(code), getLanguageFlag(code)));
        }
        return info;
    }

    public void reload() {
        languages.clear();
        loadLanguages();
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(String defaultLang) {
        if (languages.containsKey(defaultLang)) {
            this.defaultLang = defaultLang;
        }
    }

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

    public static class LanguageInfo {
        private final String code;
        private final String name;
        private final String flag;

        public LanguageInfo(String code, String name, String flag) {
            this.code = code;
            this.name = name;
            this.flag = flag;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getFlag() { return flag; }
    }
}