package github.dimazbtw.essentials.hooks;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.utils.formatters.TimeFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceholderHook extends PlaceholderExpansion {

    private final Main plugin;

    public PlaceholderHook(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "essentials";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // %essentials_lang% - Idioma atual do jogador
        if (identifier.equals("lang")) {
            return plugin.getLangManager().getPlayerLanguage(player);
        }

        // %essentials_lang_name% - Nome do idioma
        if (identifier.equals("lang_name")) {
            String lang = plugin.getLangManager().getPlayerLanguage(player);
            return plugin.getLangManager().getLanguageName(lang);
        }

        // %essentials_lang_flag% - Bandeira do idioma
        if (identifier.equals("lang_flag")) {
            String lang = plugin.getLangManager().getPlayerLanguage(player);
            return plugin.getLangManager().getLanguageFlag(lang);
        }

        // %essentials_kit_<nome>%
        if (identifier.startsWith("kit_") && !identifier.contains("_cooldown")
                && !identifier.contains("_seconds") && !identifier.contains("_available")
                && !identifier.contains("_formatted")) {
            String kitName = identifier.substring(4);
            return getKitStatus(player, kitName);
        }

        // %essentials_kit_<nome>_cooldown%
        if (identifier.startsWith("kit_") && identifier.endsWith("_cooldown")) {
            String kitName = identifier.substring(4, identifier.length() - 9);
            return getKitCooldown(player, kitName);
        }

        // %essentials_kit_<nome>_seconds%
        if (identifier.startsWith("kit_") && identifier.endsWith("_seconds")) {
            String kitName = identifier.substring(4, identifier.length() - 8);
            return String.valueOf(getKitCooldownSeconds(player, kitName));
        }

        // %essentials_kit_<nome>_available%
        if (identifier.startsWith("kit_") && identifier.endsWith("_available")) {
            String kitName = identifier.substring(4, identifier.length() - 10);
            return String.valueOf(isKitAvailable(player, kitName));
        }

        // %essentials_kit_<nome>_formatted%
        if (identifier.startsWith("kit_") && identifier.endsWith("_formatted")) {
            String kitName = identifier.substring(4, identifier.length() - 10);
            return getKitCooldownFormatted(player, kitName);
        }

        return null;
    }

    private String getKitStatus(Player player, String kitName) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitName);
        if (!optKit.isPresent()) {
            return plugin.getLangManager().getMessage(player, "placeholder.kit.not-exists");
        }

        KitModel kit = optKit.get();

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            return plugin.getLangManager().getMessage(player, "placeholder.kit.no-permission");
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        if (remaining <= 0) {
            return plugin.getLangManager().getMessage(player, "placeholder.kit.available");
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", TimeFormatter.getRemainingTime(remaining));
        return plugin.getLangManager().getMessage(player, "placeholder.kit.cooldown", placeholders);
    }

    private String getKitCooldown(Player player, String kitName) {
        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        if (remaining > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", TimeFormatter.getRemainingTime(remaining));
            return plugin.getLangManager().getMessage(player, "placeholder.kit.cooldown", placeholders);
        }
        return plugin.getLangManager().getMessage(player, "placeholder.kit.available");
    }

    private long getKitCooldownSeconds(Player player, String kitName) {
        return plugin.getKitManager().getRemainingCooldown(player, kitName);
    }

    private boolean isKitAvailable(Player player, String kitName) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitName);
        if (!optKit.isPresent()) {
            return false;
        }

        KitModel kit = optKit.get();

        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            return false;
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        return remaining <= 0;
    }

    private String getKitCooldownFormatted(Player player, String kitName) {
        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        if (remaining <= 0) {
            return "0s";
        }
        return TimeFormatter.getRemainingTime(remaining);
    }
}