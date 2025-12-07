package github.dimazbtw.essentials.views;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.inventories.ClickAction;
import github.dimazbtw.lib.inventories.InventoryGUI;
import github.dimazbtw.lib.inventories.InventorySize;
import github.dimazbtw.lib.inventories.ItemButton;
import github.dimazbtw.lib.utils.basics.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KitsView {
    private final Main plugin;
    private final File menusFolder;
    private final Map<String, Map<String, MenuConfig>> languageMenus = new HashMap<>();
    private final Map<UUID, BukkitTask> updateTasks = new HashMap<>();

    public KitsView(Main plugin) {
        this.plugin = plugin;
        this.menusFolder = new File(plugin.getDataFolder(), "menus");
        setupMenus();
        loadAllMenus();
    }

    private void setupMenus() {
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
        }

        for (String lang : plugin.getLangManager().getAvailableLanguages()) {
            File langFolder = new File(menusFolder, lang);
            if (!langFolder.exists()) {
                langFolder.mkdirs();
            }
            saveResourceMenu("menus/" + lang + "/kits.yml", langFolder);
        }

        File defaultMenu = new File(menusFolder, "kits.yml");
        if (!defaultMenu.exists()) {
            saveResourceMenu("menus/kits.yml", menusFolder);
            if (!defaultMenu.exists()) {
                createDefaultKitsMenuFile(defaultMenu, plugin.getLangManager().getDefaultLanguage());
            }
        }
    }

    private void saveResourceMenu(String resourcePath, File targetFolder) {
        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in != null) {
                String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                File targetFile = new File(targetFolder, fileName);

                if (!targetFile.exists()) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(
                            new InputStreamReader(in, StandardCharsets.UTF_8));
                    config.save(targetFile);
                    plugin.getLogger().info("Menu criado: " + targetFile.getPath());
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Erro ao salvar menu: " + resourcePath);
        }
    }

    private void createDefaultKitsMenuFile(File file, String lang) {
        YamlConfiguration config = new YamlConfiguration();
        boolean isEnglish = lang.startsWith("en");

        config.set("menus.principal.title", "&8Kits");
        config.set("menus.principal.size", 27);

        config.set("menus.principal.items.ranks.slot", 11);
        config.set("menus.principal.items.ranks.material", "DIAMOND");
        config.set("menus.principal.items.ranks.name", "&b&lKits - Ranks");
        config.set("menus.principal.items.ranks.lore", isEnglish ?
                Arrays.asList("&7Access the rank kits", "&7menu.", "", "&aClick to access") :
                Arrays.asList("&7Acessa o menu de kits", "&7de ranks.", "", "&aClica para acessar"));
        config.set("menus.principal.items.ranks.action", Arrays.asList("[menu] kits-ranks", "[sound] NOTE_PLING"));

        config.set("menus.principal.items.vips.slot", 13);
        config.set("menus.principal.items.vips.material", "EMERALD");
        config.set("menus.principal.items.vips.name", "&6&lKits - VIPs");
        config.set("menus.principal.items.vips.lore", isEnglish ?
                Arrays.asList("&7Access the VIP kits", "&7menu.", "", "&aClick to access") :
                Arrays.asList("&7Acessa o menu de kits", "&7VIP.", "", "&aClica para acessar"));
        config.set("menus.principal.items.vips.action", Arrays.asList("[menu] kits-vips", "[sound] NOTE_PLING"));

        config.set("menus.principal.items.outros.slot", 15);
        config.set("menus.principal.items.outros.material", "IRON_INGOT");
        config.set("menus.principal.items.outros.name", isEnglish ? "&e&lKits - Others" : "&e&lKits - Outros");
        config.set("menus.principal.items.outros.lore", isEnglish ?
                Arrays.asList("&7Access other kits", "&7menu.", "", "&aClick to access") :
                Arrays.asList("&7Acessa o menu de", "&7outros kits.", "", "&aClica para acessar"));
        config.set("menus.principal.items.outros.action", Arrays.asList("[menu] kits-outros", "[sound] NOTE_PLING"));

        config.set("menus.kits-outros.title", isEnglish ? "&8Kits - Others" : "&8Kits - Outros");
        config.set("menus.kits-outros.size", 27);

        config.set("menus.kits-outros.items.basico.slot", 11);
        config.set("menus.kits-outros.items.basico.kit", "basico");
        config.set("menus.kits-outros.items.basico.material", "STONE_SWORD");
        config.set("menus.kits-outros.items.basico.name", isEnglish ? "&7Basic Kit" : "&7Kit Básico");
        config.set("menus.kits-outros.items.basico.lore", Arrays.asList("", "{kit_status}", "",
                isEnglish ? "&eRight-Click to preview" : "&eBotão-Direito para ver"));
        config.set("menus.kits-outros.items.basico.action", Arrays.asList("[sound] NOTE_PLING", "[kit] basico"));

        config.set("menus.kits-outros.items.voltar.slot", 22);
        config.set("menus.kits-outros.items.voltar.material", "ARROW");
        config.set("menus.kits-outros.items.voltar.name", isEnglish ? "&cBack" : "&cVoltar");
        config.set("menus.kits-outros.items.voltar.lore", Arrays.asList(isEnglish ? "&7Click to go back" : "&7Clique para voltar"));
        config.set("menus.kits-outros.items.voltar.action", Arrays.asList("[menu] principal", "[sound] CLICK"));

        config.set("menus.kits-vips.title", "&8Kits - VIPs");
        config.set("menus.kits-vips.size", 27);

        config.set("menus.kits-vips.items.vip.slot", 13);
        config.set("menus.kits-vips.items.vip.kit", "vip");
        config.set("menus.kits-vips.items.vip.material", "GOLD_INGOT");
        config.set("menus.kits-vips.items.vip.name", "&6Kit VIP");
        config.set("menus.kits-vips.items.vip.lore", Arrays.asList("", "{kit_status}", "",
                isEnglish ? "&eRight-Click to preview" : "&eBotão-Direito para ver"));
        config.set("menus.kits-vips.items.vip.action", Arrays.asList("[sound] NOTE_PLING", "[kit] vip"));

        config.set("menus.kits-vips.items.voltar.slot", 22);
        config.set("menus.kits-vips.items.voltar.material", "ARROW");
        config.set("menus.kits-vips.items.voltar.name", isEnglish ? "&cBack" : "&cVoltar");
        config.set("menus.kits-vips.items.voltar.lore", Arrays.asList(isEnglish ? "&7Click to go back" : "&7Clique para voltar"));
        config.set("menus.kits-vips.items.voltar.action", Arrays.asList("[menu] principal", "[sound] CLICK"));

        config.set("menus.kits-ranks.title", "&8Kits - Ranks");
        config.set("menus.kits-ranks.size", 27);

        config.set("menus.kits-ranks.items.voltar.slot", 22);
        config.set("menus.kits-ranks.items.voltar.material", "ARROW");
        config.set("menus.kits-ranks.items.voltar.name", isEnglish ? "&cBack" : "&cVoltar");
        config.set("menus.kits-ranks.items.voltar.lore", Arrays.asList(isEnglish ? "&7Click to go back" : "&7Clique para voltar"));
        config.set("menus.kits-ranks.items.voltar.action", Arrays.asList("[menu] principal", "[sound] CLICK"));

        try {
            config.save(file);
            plugin.getLogger().info("Menu de kits criado: " + file.getPath());
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao criar menu de kits!");
            e.printStackTrace();
        }
    }

    private void loadAllMenus() {
        languageMenus.clear();

        for (String lang : plugin.getLangManager().getAvailableLanguages()) {
            File langFolder = new File(menusFolder, lang);
            if (langFolder.exists() && langFolder.isDirectory()) {
                loadMenusFromFolder(langFolder, lang);
            }
        }

        File defaultKits = new File(menusFolder, "kits.yml");
        if (defaultKits.exists()) {
            loadMenuFile(defaultKits, "default");
        }

        plugin.getLogger().info("Menus carregados para " + languageMenus.size() + " idiomas!");
    }

    private void loadMenusFromFolder(File folder, String lang) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            loadMenuFile(file, lang);
        }
    }

    private void loadMenuFile(File file, String lang) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("menus")) return;

        ConfigurationSection menusSection = config.getConfigurationSection("menus");
        if (menusSection == null) return;

        Map<String, MenuConfig> menus = languageMenus.computeIfAbsent(lang, k -> new HashMap<>());

        for (String menuName : menusSection.getKeys(false)) {
            String path = "menus." + menuName;

            String title = config.getString(path + ".title", "Menu");
            int size = config.getInt(path + ".size", 27);

            MenuConfig menuConfig = new MenuConfig(menuName, title, size);

            ConfigurationSection itemsSection = config.getConfigurationSection(path + ".items");
            if (itemsSection != null) {
                for (String itemKey : itemsSection.getKeys(false)) {
                    String itemPath = path + ".items." + itemKey;

                    List<Integer> slots = new ArrayList<>();
                    if (config.isList(itemPath + ".slot")) {
                        slots = config.getIntegerList(itemPath + ".slot");
                    } else {
                        slots.add(config.getInt(itemPath + ".slot", 0));
                    }

                    String material = config.getString(itemPath + ".material", "STONE");
                    String name = config.getString(itemPath + ".name", "");
                    List<String> lore = config.getStringList(itemPath + ".lore");
                    List<String> actions = config.getStringList(itemPath + ".action");
                    String kitId = config.getString(itemPath + ".kit", "");

                    for (int slot : slots) {
                        MenuItem menuItem = new MenuItem(slot, material, name, lore, actions, kitId);
                        menuConfig.addItem(menuItem);
                    }
                }
            }

            menus.put(menuName, menuConfig);
        }
    }

    private MenuConfig getMenuForPlayer(Player player, String menuName) {
        String playerLang = plugin.getLangManager().getPlayerLanguage(player);

        Map<String, MenuConfig> langMenus = languageMenus.get(playerLang);
        if (langMenus != null && langMenus.containsKey(menuName)) {
            return langMenus.get(menuName);
        }

        String defaultLang = plugin.getLangManager().getDefaultLanguage();
        langMenus = languageMenus.get(defaultLang);
        if (langMenus != null && langMenus.containsKey(menuName)) {
            return langMenus.get(menuName);
        }

        langMenus = languageMenus.get("default");
        if (langMenus != null && langMenus.containsKey(menuName)) {
            return langMenus.get(menuName);
        }

        return null;
    }

    public void openMenu(Player player, String menuName) {
        MenuConfig menuConfig = getMenuForPlayer(player, menuName);
        if (menuConfig == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("menu", menuName);
            player.sendMessage(plugin.getLangManager().getMessage(player, "menus.not-found", placeholders));
            return;
        }

        cancelUpdateTask(player);

        InventorySize invSize = getInventorySize(menuConfig.size);
        InventoryGUI inv = new InventoryGUI(
                ColorUtils.colorize(menuConfig.title),
                invSize
        );
        inv.setDefaultAllCancell(true);

        final String currentMenu = menuName;

        for (MenuItem item : menuConfig.items) {
            ItemStack itemStack = createItemStack(player, item);
            ItemButton button = new ItemButton(itemStack);

            button.setDefaultAction(new ClickAction() {
                private long lastClick = 0;

                @Override
                public void run(InventoryClickEvent e) {
                    long now = System.currentTimeMillis();
                    if (now - lastClick < 500) {
                        return;
                    }
                    lastClick = now;

                    Player clicker = (Player) e.getWhoClicked();

                    if (e.isRightClick() && !item.kitId.isEmpty()) {
                        openKitPreview(clicker, item.kitId, currentMenu);
                        return;
                    }

                    for (String action : item.actions) {
                        processAction(clicker, action, item.kitId);
                    }
                }
            });

            inv.setButton(item.slot, button);
        }

        inv.show(player);

        boolean hasKits = menuConfig.items.stream().anyMatch(item -> !item.kitId.isEmpty());
        if (hasKits) {
            startAutoUpdate(player, menuName, inv, menuConfig);
        }
    }

    private void startAutoUpdate(Player player, String menuName, InventoryGUI inv, MenuConfig menuConfig) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.getOpenInventory().getTitle().equals(ColorUtils.colorize(menuConfig.title))) {
                cancelUpdateTask(player);
                return;
            }

            for (MenuItem item : menuConfig.items) {
                if (!item.kitId.isEmpty()) {
                    ItemStack updatedItem = createItemStack(player, item);
                    inv.getInventory().setItem(item.slot, updatedItem);
                }
            }
        }, 20L, 20L);

        updateTasks.put(player.getUniqueId(), task);
    }

    private void cancelUpdateTask(Player player) {
        BukkitTask task = updateTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private ItemStack createItemStack(Player player, MenuItem item) {
        Material material;
        try {
            material = Material.valueOf(item.material.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.STONE;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ColorUtils.colorize(item.name));

            List<String> processedLore = new ArrayList<>();
            for (String line : item.lore) {
                if (line.contains("{kit_status}") && !item.kitId.isEmpty()) {
                    processedLore.addAll(getKitStatus(player, item.kitId));
                } else {
                    processedLore.add(ColorUtils.colorize(line));
                }
            }

            meta.setLore(processedLore);

            try {
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
            } catch (Exception ignored) {}

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private List<String> getKitStatus(Player player, String kitId) {
        List<String> status = new ArrayList<>();

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.not-exists"));
            return status;
        }

        KitModel kit = optKit.get();

        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.no-permission"));
            return status;
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitId);
        if (remaining > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", formatTime(remaining));
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.cooldown-title", placeholders));
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.cooldown-time", placeholders));
        } else {
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.available"));
            status.add(plugin.getLangManager().getMessage(player, "kit.menu.click-to-claim"));
        }

        return status;
    }

    private void processAction(Player player, String action, String kitId) {
        action = action.trim();

        if (action.startsWith("[menu]")) {
            String menuName = action.substring(7).trim();
            openMenu(player, menuName);
        } else if (action.startsWith("[sound]")) {
            String soundName = action.substring(8).trim();
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Som inválido: " + soundName);
            }
        } else if (action.startsWith("[kit]")) {
            String kitName = action.substring(6).trim();
            giveKit(player, kitName);
        } else if (action.startsWith("[command]")) {
            String command = action.substring(10).trim();
            if (!command.isEmpty()) {
                player.performCommand(command);
            }
        } else if (action.startsWith("[close]")) {
            player.closeInventory();
        } else if (action.startsWith("[message]")) {
            String message = action.substring(10).trim();
            player.sendMessage(ColorUtils.colorize(message));
        }
    }

    private void giveKit(Player player, String kitId) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.not-found"));
            return;
        }

        KitModel kit = optKit.get();

        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.no-permission"));
            return;
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitId);
        if (remaining > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("time", formatTime(remaining));
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.cooldown", placeholders));
            return;
        }

        if (plugin.getKitManager().giveKit(player, kitId, false)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("kit", ColorUtils.colorize(kit.getDisplay()));
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.received", placeholders));
            player.closeInventory();
        } else {
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.error"));
        }
    }

    public void openKitPreview(Player player, String kitId, String returnMenu) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            player.sendMessage(plugin.getLangManager().getMessage(player, "kit.not-found"));
            return;
        }

        KitModel kit = optKit.get();
        String previewTitle = plugin.getLangManager().getMessage(player, "kit.preview.title");
        String titulo = ColorUtils.colorize("§8" + kit.getNome() + " - " + previewTitle);

        cancelUpdateTask(player);

        InventoryGUI inv = new InventoryGUI(titulo, InventorySize.SIX_ROWS);
        inv.setDefaultAllCancell(true);

        ItemStack[] items = kit.getItems();

        for (int i = 0; i < items.length && i < 54; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                ItemStack displayItem = items[i].clone();
                ItemMeta meta = displayItem.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                    lore.add("");
                    lore.add(plugin.getLangManager().getMessage(player, "kit.preview.item-lore"));
                    meta.setLore(lore);
                    displayItem.setItemMeta(meta);
                }

                ItemButton button = new ItemButton(displayItem);
                inv.setButton(i, button);
            }
        }

        updateKitInfoButton(inv, player, kit, 53);

        ItemStack backItem = new ItemStack(returnMenu != null ? Material.ARROW : Material.BARRIER);
        ItemButton backButton = new ItemButton(backItem);

        if (returnMenu != null) {
            backButton.setName(plugin.getLangManager().getMessage(player, "kit.preview.back"));
            backButton.setLore(Arrays.asList(plugin.getLangManager().getMessage(player, "kit.preview.back-lore")));
            backButton.setDefaultAction(new ClickAction() {
                @Override
                public void run(InventoryClickEvent e) {
                    cancelUpdateTask(player);
                    openMenu(player, returnMenu);
                }
            });
        } else {
            backButton.setName(plugin.getLangManager().getMessage(player, "kit.preview.close"));
            backButton.setLore(Arrays.asList(plugin.getLangManager().getMessage(player, "kit.preview.close-lore")));
            backButton.setDefaultAction(new ClickAction() {
                @Override
                public void run(InventoryClickEvent e) {
                    player.closeInventory();
                    cancelUpdateTask(player);
                }
            });
        }

        inv.setButton(49, backButton);

        inv.show(player);

        startKitPreviewAutoUpdate(player, inv, kit, titulo);
    }

    private void startKitPreviewAutoUpdate(Player player, InventoryGUI inv, KitModel kit, String titulo) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.getOpenInventory().getTitle().equals(titulo)) {
                cancelUpdateTask(player);
                return;
            }

            updateKitInfoButton(inv, player, kit, 53);
        }, 20L, 20L);

        updateTasks.put(player.getUniqueId(), task);
    }

    private void updateKitInfoButton(InventoryGUI inv, Player player, KitModel kit, int slot) {
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();

        if (infoMeta != null) {
            infoMeta.setDisplayName(plugin.getLangManager().getMessage(player, "kit.preview.info-title"));

            List<String> infoLore = new ArrayList<>();

            Map<String, String> namePlaceholders = new HashMap<>();
            namePlaceholders.put("name", ColorUtils.colorize(kit.getDisplay()));
            infoLore.add(plugin.getLangManager().getMessage(player, "kit.preview.info-name", namePlaceholders));

            if (kit.getDelay() > 0) {
                Map<String, String> delayPlaceholders = new HashMap<>();
                delayPlaceholders.put("delay", formatTime(kit.getDelay()));
                infoLore.add(plugin.getLangManager().getMessage(player, "kit.preview.info-delay", delayPlaceholders));
            }

            long remaining = plugin.getKitManager().getRemainingCooldown(player, kit.getId());
            if (remaining > 0) {
                infoLore.add("");
                Map<String, String> cooldownPlaceholders = new HashMap<>();
                cooldownPlaceholders.put("time", formatTime(remaining));
                infoLore.add(plugin.getLangManager().getMessage(player, "kit.preview.info-cooldown", cooldownPlaceholders));
            } else if (player.hasPermission(kit.getPermissao()) || kit.getPermissao().isEmpty()) {
                infoLore.add("");
                infoLore.add(plugin.getLangManager().getMessage(player, "kit.preview.info-available"));
            }

            infoMeta.setLore(infoLore);

            try {
                infoMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception ignored) {}

            infoItem.setItemMeta(infoMeta);
        }

        inv.getInventory().setItem(slot, infoItem);
    }

    private String formatTime(long seconds) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (secs > 0 || sb.length() == 0) sb.append(secs).append("s");

        return sb.toString().trim();
    }

    private InventorySize getInventorySize(int size) {
        switch (size) {
            case 9: return InventorySize.ONE_ROW;
            case 18: return InventorySize.TWO_ROWS;
            case 27: return InventorySize.THREE_ROWS;
            case 36: return InventorySize.FOUR_ROWS;
            case 45: return InventorySize.FIVE_ROWS;
            case 54: return InventorySize.SIX_ROWS;
            default: return InventorySize.THREE_ROWS;
        }
    }

    public void reload() {
        setupMenus();
        loadAllMenus();
    }

    public void shutdown() {
        updateTasks.values().forEach(BukkitTask::cancel);
        updateTasks.clear();
    }

    private static class MenuConfig {
        private final String name;
        private final String title;
        private final int size;
        private final List<MenuItem> items;

        public MenuConfig(String name, String title, int size) {
            this.name = name;
            this.title = title;
            this.size = size;
            this.items = new ArrayList<>();
        }

        public void addItem(MenuItem item) {
            items.add(item);
        }
    }

    private static class MenuItem {
        private final int slot;
        private final String material;
        private final String name;
        private final List<String> lore;
        private final List<String> actions;
        private final String kitId;

        public MenuItem(int slot, String material, String name, List<String> lore, List<String> actions, String kitId) {
            this.slot = slot;
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.actions = actions;
            this.kitId = kitId;
        }
    }
}