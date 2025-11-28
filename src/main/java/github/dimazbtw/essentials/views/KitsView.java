package github.dimazbtw.essentials.views;

import github.dimazbtw.essentials.Main;
import github.dimazbtw.essentials.models.KitModel;
import github.dimazbtw.lib.inventories.ClickAction;
import github.dimazbtw.lib.inventories.InventoryGUI;
import github.dimazbtw.lib.inventories.InventorySize;
import github.dimazbtw.lib.inventories.ItemButton;
import github.dimazbtw.lib.utils.basics.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class KitsView {
    private final Main plugin;
    private File configFile;
    private YamlConfiguration config;
    private final Map<String, MenuConfig> menus = new HashMap<>();
    private final Map<UUID, BukkitTask> updateTasks = new HashMap<>();

    public KitsView(Main plugin) {
        this.plugin = plugin;
        setupConfig();
        loadMenus();
    }

    private void setupConfig() {
        File menusFolder = new File(plugin.getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
        }

        configFile = new File(menusFolder, "kits.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar menus/kits.yml!");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        // Criar configuração padrão se o arquivo estiver vazio
        if (config.getKeys(false).isEmpty()) {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        config.set("menus.principal.title", "Kits");
        config.set("menus.principal.size", 27);

        // Item 1 - Ranks
        config.set("menus.principal.items.item1.slot", 11);
        config.set("menus.principal.items.item1.material", "DIAMOND");
        config.set("menus.principal.items.item1.name", "&aKits - Ranks");
        config.set("menus.principal.items.item1.lore", Arrays.asList(
                "&7Acessa o menu de kits",
                "&7de ranks.",
                "",
                "&aClica para acessar"
        ));
        config.set("menus.principal.items.item1.action", Arrays.asList(
                "[menu] kits-ranks",
                "[sound] NOTE_PLING"
        ));

        // Item 2 - VIPs
        config.set("menus.principal.items.item2.slot", 13);
        config.set("menus.principal.items.item2.material", "EMERALD");
        config.set("menus.principal.items.item2.name", "&aKits - VIPs");
        config.set("menus.principal.items.item2.lore", Arrays.asList(
                "&7Acessa o menu de kits",
                "&7VIPs.",
                "",
                "&aClica para acessar"
        ));
        config.set("menus.principal.items.item2.action", Arrays.asList(
                "[menu] kits-vips",
                "[sound] NOTE_PLING"
        ));

        // Item 3 - Outros
        config.set("menus.principal.items.item3.slot", 15);
        config.set("menus.principal.items.item3.material", "IRON_INGOT");
        config.set("menus.principal.items.item3.name", "&aKits - Outros");
        config.set("menus.principal.items.item3.lore", Arrays.asList(
                "&7Acessa o menu de outros",
                "&7kits.",
                "",
                "&aClica para acessar"
        ));
        config.set("menus.principal.items.item3.action", Arrays.asList(
                "[menu] kits-outros",
                "[sound] NOTE_PLING"
        ));

        // Menu Kits Outros
        config.set("menus.kits-outros.title", "Kits - Outros");
        config.set("menus.kits-outros.size", 27);

        config.set("menus.kits-outros.items.basico.slot", 11);
        config.set("menus.kits-outros.items.basico.kit", "basico");
        config.set("menus.kits-outros.items.basico.material", "STONE_SWORD");
        config.set("menus.kits-outros.items.basico.name", "&aKit Básico");
        config.set("menus.kits-outros.items.basico.lore", Arrays.asList(
                "",
                "{kit_status}",
                "",
                "&eBotão-Direito para ver"
        ));
        config.set("menus.kits-outros.items.basico.action", Arrays.asList(
                "[sound] NOTE_PLING",
                "[kit] basico"
        ));

        // Menu Kits VIPs
        config.set("menus.kits-vips.title", "Kits - VIPs");
        config.set("menus.kits-vips.size", 27);

        config.set("menus.kits-vips.items.vip.slot", 13);
        config.set("menus.kits-vips.items.vip.kit", "vip");
        config.set("menus.kits-vips.items.vip.material", "GOLD_INGOT");
        config.set("menus.kits-vips.items.vip.name", "&6Kit VIP");
        config.set("menus.kits-vips.items.vip.lore", Arrays.asList(
                "",
                "{kit_status}",
                "",
                "&eBotão-Direito para ver"
        ));
        config.set("menus.kits-vips.items.vip.action", Arrays.asList(
                "[sound] NOTE_PLING",
                "[kit] vip"
        ));

        // Menu Kits Ranks
        config.set("menus.kits-ranks.title", "Kits - Ranks");
        config.set("menus.kits-ranks.size", 27);

        // Botão de voltar em todos os submenus
        for (String menuName : Arrays.asList("kits-outros", "kits-vips", "kits-ranks")) {
            config.set("menus." + menuName + ".items.voltar.slot", 22);
            config.set("menus." + menuName + ".items.voltar.material", "ARROW");
            config.set("menus." + menuName + ".items.voltar.name", "&cVoltar");
            config.set("menus." + menuName + ".items.voltar.lore", Arrays.asList("&7Clique para voltar"));
            config.set("menus." + menuName + ".items.voltar.action", Arrays.asList(
                    "[menu] principal",
                    "[sound] CLICK"
            ));
        }

        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar menus/kits.yml!");
            e.printStackTrace();
        }
    }

    private void loadMenus() {
        menus.clear();

        if (!config.contains("menus")) {
            plugin.getLogger().warning("Nenhum menu encontrado em menus/kits.yml!");
            return;
        }

        ConfigurationSection menusSection = config.getConfigurationSection("menus");
        if (menusSection == null) return;

        for (String menuName : menusSection.getKeys(false)) {
            String path = "menus." + menuName;

            String title = config.getString(path + ".title", "Menu");
            int size = config.getInt(path + ".size", 27);

            MenuConfig menuConfig = new MenuConfig(menuName, title, size);

            ConfigurationSection itemsSection = config.getConfigurationSection(path + ".items");
            if (itemsSection != null) {
                for (String itemKey : itemsSection.getKeys(false)) {
                    String itemPath = path + ".items." + itemKey;

                    // Suporte para slot único ou múltiplos slots
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

                    // Criar um MenuItem para cada slot
                    for (int slot : slots) {
                        MenuItem menuItem = new MenuItem(slot, material, name, lore, actions, kitId);
                        menuConfig.addItem(menuItem);
                    }
                }
            }

            menus.put(menuName, menuConfig);
        }

        plugin.getLogger().info("Carregados " + menus.size() + " menus de kits!");
    }

    public void openMenu(Player player, String menuName) {
        MenuConfig menuConfig = menus.get(menuName);
        if (menuConfig == null) {
            player.sendMessage("§cMenu não encontrado!");
            return;
        }

        // Cancelar task anterior se existir
        cancelUpdateTask(player);

        InventorySize invSize = getInventorySize(menuConfig.size);
        InventoryGUI inv = new InventoryGUI(
                ColorUtils.colorize(menuConfig.title),
                invSize
        );
        inv.setDefaultAllCancell(true);

        // Variável final para usar no lambda
        final String currentMenu = menuName;

        for (MenuItem item : menuConfig.items) {
            ItemStack itemStack = createItemStack(player, item);
            ItemButton button = new ItemButton(itemStack);

            button.setDefaultAction(new ClickAction() {
                private long lastClick = 0;

                @Override
                public void run(InventoryClickEvent e) {
                    // Prevenir cliques duplicados (anti-spam de 500ms)
                    long now = System.currentTimeMillis();
                    if (now - lastClick < 500) {
                        return;
                    }
                    lastClick = now;

                    Player clicker = (Player) e.getWhoClicked();

                    // Verificar se é botão direito e tem kit associado
                    if (e.isRightClick() && !item.kitId.isEmpty()) {
                        openKitPreview(clicker, item.kitId, currentMenu);
                        return;
                    }

                    // Processar ações
                    for (String action : item.actions) {
                        processAction(clicker, action, item.kitId);
                    }
                }
            });

            inv.setButton(item.slot, button);
        }

        inv.show(player);

        // Iniciar atualização automática se tiver kits no menu
        boolean hasKits = menuConfig.items.stream().anyMatch(item -> !item.kitId.isEmpty());
        if (hasKits) {
            startAutoUpdate(player, menuName, inv, menuConfig);
        }
    }

    private void startAutoUpdate(Player player, String menuName, InventoryGUI inv, MenuConfig menuConfig) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Verificar se o jogador ainda está com o inventário aberto
            if (!player.getOpenInventory().getTitle().equals(ColorUtils.colorize(menuConfig.title))) {
                cancelUpdateTask(player);
                return;
            }

            // Atualizar itens com kits
            for (MenuItem item : menuConfig.items) {
                if (!item.kitId.isEmpty()) {
                    ItemStack updatedItem = createItemStack(player, item);
                    inv.getInventory().setItem(item.slot, updatedItem);
                }
            }
        }, 20L, 20L); // Atualiza a cada 1 segundo

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

            // Esconder atributos (funciona em 1.8+)
            try {
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
            } catch (Exception e) {
                // Versão antiga do Minecraft que não suporta ItemFlags
            }

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private List<String> getKitStatus(Player player, String kitId) {
        List<String> status = new ArrayList<>();

        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            status.add("§cKit não existe");
            return status;
        }

        KitModel kit = optKit.get();

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            status.add("§cSem permissão para este kit");
            return status;
        }

        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitId);
        if (remaining > 0) {
            status.add("§cPodes resgatar este kit em:");
            status.add("§f" + formatTime(remaining));
        } else {
            status.add("§a✓ Disponível para resgate");
            status.add("§eBotão esquerdo para resgatar");
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
            player.sendMessage("§cKit não encontrado!");
            return;
        }

        KitModel kit = optKit.get();

        // Verificar permissão
        if (!kit.getPermissao().isEmpty() && !player.hasPermission(kit.getPermissao())) {
            player.sendMessage("§cVocê não tem permissão para este kit!");
            return;
        }

        // Verificar cooldown
        long remaining = plugin.getKitManager().getRemainingCooldown(player, kitId);
        if (remaining > 0) {
            player.sendMessage("§cVocê poderá pegar este kit em: §f" + formatTime(remaining));
            return;
        }

        // Dar o kit
        if (plugin.getKitManager().giveKit(player, kitId, false)) {
            player.sendMessage("§aVocê resgatou o kit: " + ColorUtils.colorize(kit.getDisplay()));
            player.closeInventory();
        } else {
            player.sendMessage("§cErro ao resgatar o kit!");
        }
    }

    private void openKitPreview(Player player, String kitId) {
        openKitPreview(player, kitId, null);
    }

    public void openKitPreview(Player player, String kitId, String returnMenu) {
        Optional<KitModel> optKit = plugin.getKitManager().getKit(kitId);
        if (!optKit.isPresent()) {
            player.sendMessage("§cKit não encontrado!");
            return;
        }

        KitModel kit = optKit.get();
        String titulo = ColorUtils.colorize("§8" + kit.getNome() + " - Preview");

        // Cancelar task anterior
        cancelUpdateTask(player);

        InventoryGUI inv = new InventoryGUI(titulo, InventorySize.SIX_ROWS);
        inv.setDefaultAllCancell(true);

        ItemStack[] items = kit.getItems();

        // Adicionar itens do kit
        for (int i = 0; i < items.length && i < 54; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                ItemStack displayItem = items[i].clone();
                ItemMeta meta = displayItem.getItemMeta();
                if (meta != null) {
                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                    lore.add("");
                    lore.add("§7Preview do kit");
                    meta.setLore(lore);
                    displayItem.setItemMeta(meta);
                }

                ItemButton button = new ItemButton(displayItem);
                inv.setButton(i, button);
            }
        }

        // Botão de informações (slot 53)
        updateKitInfoButton(inv, player, kit, 53);

        // Botão de voltar/fechar (slot 49)
        ItemStack backItem = new ItemStack(returnMenu != null ? Material.ARROW : Material.BARRIER);
        ItemButton backButton = new ItemButton(backItem);

        if (returnMenu != null) {
            // Botão de voltar ao menu
            backButton.setName("§cVoltar");
            backButton.setLore(Arrays.asList("§7Clique para voltar"));
            backButton.setDefaultAction(new ClickAction() {
                @Override
                public void run(InventoryClickEvent e) {
                    cancelUpdateTask(player);
                    openMenu(player, returnMenu);
                }
            });
        } else {
            // Botão de fechar
            backButton.setName("§c§lFechar");
            backButton.setLore(Arrays.asList("§7Clique para fechar"));
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

        // Iniciar atualização automática do botão de informações
        startKitPreviewAutoUpdate(player, inv, kit, titulo);
    }

    private void startKitPreviewAutoUpdate(Player player, InventoryGUI inv, KitModel kit, String titulo) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Verificar se o jogador ainda está com o inventário aberto
            if (!player.getOpenInventory().getTitle().equals(titulo)) {
                cancelUpdateTask(player);
                return;
            }

            // Atualizar apenas o botão de informações (slot 53)
            updateKitInfoButton(inv, player, kit, 53);
        }, 20L, 20L); // Atualiza a cada 1 segundo

        updateTasks.put(player.getUniqueId(), task);
    }

    private void updateKitInfoButton(InventoryGUI inv, Player player, KitModel kit, int slot) {
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();

        if (infoMeta != null) {
            infoMeta.setDisplayName("§e§lInformações do Kit");

            List<String> infoLore = new ArrayList<>();
            infoLore.add("§fNome: " + ColorUtils.colorize(kit.getDisplay()));

            if (kit.getDelay() > 0) {
                infoLore.add("§fDelay: " + formatTime(kit.getDelay()));
            }

            long remaining = plugin.getKitManager().getRemainingCooldown(player, kit.getId());
            if (remaining > 0) {
                infoLore.add("");
                infoLore.add("§cCooldown: §f" + formatTime(remaining));
            } else if (player.hasPermission(kit.getPermissao()) || kit.getPermissao().isEmpty()) {
                infoLore.add("");
                infoLore.add("§a✓ Disponível para resgatar!");
            }

            infoMeta.setLore(infoLore);

            // Esconder atributos
            try {
                infoMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            } catch (Exception e) {
                // Versão antiga
            }

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
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMenus();
    }

    public void shutdown() {
        // Cancelar todas as tasks ativas
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