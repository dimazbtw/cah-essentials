package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    private final Main plugin;
    private final Map<UUID, UUID> requests = new HashMap<>();
    private final Map<UUID, Long> requestTimestamps = new HashMap<>();
    private final NamespacedKey tpaDisabledKey;

    private static final long REQUEST_TIMEOUT = 60 * 1000; // 60 segundos

    public TpaManager(Main plugin) {
        this.plugin = plugin;
        this.tpaDisabledKey = new NamespacedKey(plugin, "tpa_disabled");
        startCleanupTask();
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            requests.entrySet().removeIf(entry -> {
                Long timestamp = requestTimestamps.get(entry.getKey());
                if (timestamp != null && now - timestamp > REQUEST_TIMEOUT) {
                    Player target = Bukkit.getPlayer(entry.getKey());
                    Player sender = Bukkit.getPlayer(entry.getValue());

                    if (sender != null && sender.isOnline()) {
                        sender.sendMessage("§cSeu pedido de teleporte expirou!");
                    }
                    if (target != null && target.isOnline()) {
                        target.sendMessage("§cO pedido de teleporte expirou!");
                    }

                    requestTimestamps.remove(entry.getKey());
                    return true;
                }
                return false;
            });
        }, 20L, 20L);
    }

    public void createRequest(Player sender, Player target) {
        requests.put(target.getUniqueId(), sender.getUniqueId());
        requestTimestamps.put(target.getUniqueId(), System.currentTimeMillis());
    }

    public boolean hasRequest(Player target) {
        return requests.containsKey(target.getUniqueId());
    }

    public Player getSender(Player target) {
        UUID senderUUID = requests.get(target.getUniqueId());
        return senderUUID != null ? Bukkit.getPlayer(senderUUID) : null;
    }

    public void removeRequest(Player target) {
        requests.remove(target.getUniqueId());
        requestTimestamps.remove(target.getUniqueId());
    }

    public boolean isTpaDisabled(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(tpaDisabledKey, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public boolean toggleTpa(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        boolean newState = !isTpaDisabled(player);

        if (newState) {
            pdc.set(tpaDisabledKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            pdc.remove(tpaDisabledKey);
        }

        return newState;
    }
}