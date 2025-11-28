package github.dimazbtw.essentials.hooks;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.utils.formatters.TimeFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        // %essentials_kit_<nome>%
        // Retorna "Disponível" ou o tempo restante
        if (identifier.startsWith("kit_")) {
            String kitName = identifier.substring(4);
            return getKitStatus(player, kitName);
        }

        // %essentials_kit_<nome>_cooldown%
        // Retorna apenas o tempo em formato legível
        if (identifier.startsWith("kit_") && identifier.endsWith("_cooldown")) {
            String kitName = identifier.substring(4, identifier.length() - 9);
            return getKitCooldown(player, kitName);
        }

        // %essentials_kit_<nome>_seconds%
        // Retorna o tempo em segundos
        if (identifier.startsWith("kit_") && identifier.endsWith("_seconds")) {
            String kitName = identifier.substring(4, identifier.length() - 8);
            return String.valueOf(getKitCooldownSeconds(player, kitName));
        }

        // %essentials_kit_<nome>_available%
        // Retorna "true" ou "false"
        if (identifier.startsWith("kit_") && identifier.endsWith("_available")) {
            String kitName = identifier.substring(4, identifier.length() - 10);
            return String.valueOf(isKitAvailable(player, kitName));
        }

        // %essentials_kit_<nome>_formatted%
        // Retorna formatado tipo "2d 5h 30m"
        if (identifier.startsWith("kit_") && identifier.endsWith("_formatted")) {
            String kitName = identifier.substring(4, identifier.length() - 10);
            return getKitCooldownFormatted(player, kitName);
        }

        return null;
    }

    private String getKitStatus(Player player, String kitName) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitName);
        if (!optKit.isPresent()) {
            return "§cKit não existe.";
        }

        KitModel kit = optKit.get();

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            return "§cSem permissão.";
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        if (remaining <= 0) {
            return "§aClica para resgatares.";
        }

        return TimeFormatter.getRemainingTime(remaining);
    }

    private String getKitCooldown(Player player, String kitName) {
        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitName);
        return remaining > 0 ? TimeFormatter.getRemainingTime(remaining) : "§aClica para resgatares.";
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

        // Verificar permissão
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