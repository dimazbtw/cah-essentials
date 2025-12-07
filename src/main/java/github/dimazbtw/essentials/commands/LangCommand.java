package github.dimazbtw.essentials.commands;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.managers.LangManager;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LangCommand {
    private final Main plugin;

    public LangCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "lang",
            aliases = {"idioma", "language"},
            description = "Altera ou visualiza o idioma do jogador",
            usage = "/lang [código]",
            target = CommandTarget.PLAYER
    )
    public void lang(Context<Player> context, @Optional String langCode) {
        Player player = context.getSender();
        LangManager langManager = plugin.getLangManager();

        // Se não passar argumento, mostrar idioma atual e lista
        if (langCode == null || langCode.isEmpty()) {
            showLanguageList(player, langManager);
            return;
        }

        // Tentar mudar o idioma
        if (!langManager.languageExists(langCode)) {
            player.sendMessage(langManager.getMessage(player, "language.not-found"));
            showLanguageList(player, langManager);
            return;
        }

        // Mudar idioma
        langManager.setPlayerLanguage(player, langCode);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("language", langManager.getLanguageName(langCode));
        placeholders.put("flag", langManager.getLanguageFlag(langCode));

        player.sendMessage(langManager.getMessage(player, "language.changed", placeholders));
    }

    @Command(
            name = "lang.list",
            aliases = {"idioma.lista", "language.list"},
            description = "Lista todos os idiomas disponíveis",
            target = CommandTarget.PLAYER
    )
    public void langList(Context<Player> context) {
        Player player = context.getSender();
        showLanguageList(player, plugin.getLangManager());
    }

    @Command(
            name = "lang.reload",
            aliases = {"idioma.reload", "language.reload"},
            description = "Recarrega os arquivos de idioma",
            permission = "admin",
            target = CommandTarget.ALL
    )
    public void langReload(Context<?> context) {
        plugin.getLangManager().reload();
        context.sendMessage("§aIdiomas recarregados com sucesso!");
    }

    private void showLanguageList(Player player, LangManager langManager) {
        String currentLang = langManager.getPlayerLanguage(player);

        // Header
        player.sendMessage(langManager.getMessage(player, "language.list-header"));
        player.sendMessage("");

        // Lista de idiomas
        for (String code : langManager.getAvailableLanguages()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("code", code);
            placeholders.put("name", langManager.getLanguageName(code));
            placeholders.put("flag", langManager.getLanguageFlag(code));

            String line = langManager.getMessage(player, "language.list-item", placeholders);

            // Adicionar indicador se for o idioma atual
            if (code.equals(currentLang)) {
                line += " " + langManager.getMessage(player, "language.list-current");
            }

            player.sendMessage(line);
        }

        player.sendMessage("");

        // Mostrar idioma atual
        Map<String, String> currentPlaceholders = new HashMap<>();
        currentPlaceholders.put("language", langManager.getLanguageName(currentLang));
        currentPlaceholders.put("flag", langManager.getLanguageFlag(currentLang));
        player.sendMessage(langManager.getMessage(player, "language.current", currentPlaceholders));
    }
}