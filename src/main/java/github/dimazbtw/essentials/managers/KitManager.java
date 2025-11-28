package github.dimazbtw.essentials.managers;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.utils.objects.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KitManager {
    private final Main plugin;
    private final Map<String, KitModel> kits;
    private final File kitsFolder;
    private final String COOLDOWN_KEY_PREFIX = "kit_cooldown_";

    public KitManager(Main plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        this.kitsFolder = new File(plugin.getDataFolder(), "kits");

        if (!kitsFolder.exists()) kitsFolder.mkdirs();

        loadKits();
    }

    private void loadKits() {
        File[] files = kitsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String kitId = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String nome = config.getString(kitId + ".Nome", kitId);
            String display = config.getString(kitId + ".Display", "&7" + kitId);
            String permissao = config.getString(kitId + ".Permissao", "");
            long delay = config.getLong(kitId + ".Delay", 0);

            List<String> itemsString = config.getStringList(kitId + ".Items");
            ItemStack[] items = new ItemStack[0];

            if (!itemsString.isEmpty()) {
                try {
                    items = ItemUtils.deserialize(String.join("\n", itemsString));
                } catch (Exception e) {
                    plugin.getLogger().warning("Erro ao carregar itens do kit " + kitId);
                    e.printStackTrace();
                }
            }

            kits.put(kitId.toLowerCase(), new KitModel(kitId, nome, display, permissao, delay, items));
        }

        plugin.getLogger().info("Carregados " + kits.size() + " kits!");
    }

    public void createKit(String id, String nome, String display, String permissao, long delay, ItemStack[] items) {
        File kitFile = new File(kitsFolder, id + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set(id + ".Nome", nome);
        config.set(id + ".Display", display);
        config.set(id + ".Permissao", permissao);
        config.set(id + ".Delay", delay);

        try {
            String serialized = ItemUtils.serialize(items);
            config.set(id + ".Items", Arrays.asList(serialized.split("\n")));
            config.save(kitFile);

            kits.put(id.toLowerCase(), new KitModel(id, nome, display, permissao, delay, items));
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao criar kit " + id);
            e.printStackTrace();
        }
    }

    public void saveKit(KitModel kit) {
        File kitFile = new File(kitsFolder, kit.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set(kit.getId() + ".Nome", kit.getNome());
        config.set(kit.getId() + ".Display", kit.getDisplay());
        config.set(kit.getId() + ".Permissao", kit.getPermissao());
        config.set(kit.getId() + ".Delay", kit.getDelay());

        try {
            String serialized = ItemUtils.serialize(kit.getItems());
            config.set(kit.getId() + ".Items", Arrays.asList(serialized.split("\n")));
            config.save(kitFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Erro ao salvar kit " + kit.getId());
            e.printStackTrace();
        }
    }

    public void deleteKit(String id) {
        File kitFile = new File(kitsFolder, id + ".yml");
        if (kitFile.exists()) {
            kitFile.delete();
            kits.remove(id.toLowerCase());
        }
    }

    public boolean giveKit(Player player, String kitId, boolean ignoreDelay) {
        KitModel kit = kits.get(kitId.toLowerCase());
        if (kit == null) return false;

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            return false;
        }

        // Verificar cooldown
        if (!ignoreDelay && kit.getDelay() > 0) {
            long lastUse = getLastUse(player, kitId);
            long currentTime = System.currentTimeMillis() / 1000;
            long timeElapsed = currentTime - lastUse;

            if (timeElapsed < kit.getDelay()) {
                return false; // Ainda em cooldown
            }
        }

        // Dar itens ao jogador
        for (ItemStack item : kit.getItems()) {
            if (item != null) {
                player.getInventory().addItem(item.clone());
            }
        }

        // Atualizar cooldown
        if (!ignoreDelay) {
            setLastUse(player, kitId, System.currentTimeMillis() / 1000);
        }

        return true;
    }

    public long getRemainingCooldown(Player player, String kitId) {
        KitModel kit = kits.get(kitId.toLowerCase());
        if (kit == null || kit.getDelay() == 0) return 0;

        long lastUse = getLastUse(player, kitId);
        long currentTime = System.currentTimeMillis() / 1000;
        long timeElapsed = currentTime - lastUse;
        long remaining = kit.getDelay() - timeElapsed;

        return remaining > 0 ? remaining : 0;
    }

    private long getLastUse(Player player, String kitId) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, COOLDOWN_KEY_PREFIX + kitId.toLowerCase());
        return pdc.getOrDefault(key, PersistentDataType.LONG, 0L);
    }

    private void setLastUse(Player player, String kitId, long time) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, COOLDOWN_KEY_PREFIX + kitId.toLowerCase());
        pdc.set(key, PersistentDataType.LONG, time);
    }

    public Optional<KitModel> getKit(String id) {
        return Optional.ofNullable(kits.get(id.toLowerCase()));
    }

    public Map<String, KitModel> getKits() {
        return new HashMap<>(kits);
    }

    public void reload() {
        kits.clear();
        loadKits();
    }
}