package github.dimazbtw.essentials;

import github.dimazbtw.essentials.api.LanguageAPI;
import github.dimazbtw.essentials.commands.registry.CommandRegistry;
import github.dimazbtw.essentials.databases.HomeDatabase;
import github.dimazbtw.essentials.hooks.PlaceholderHook;
import github.dimazbtw.essentials.listeners.*;
import github.dimazbtw.essentials.managers.*;
import github.dimazbtw.essentials.tasks.AnnouncerTask;
import github.dimazbtw.essentials.views.KitsView;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

@Getter
public final class Main extends JavaPlugin {

    private static Main instance;

    private LangManager langManager;
    private LocationManager locationManager;
    private WarpManager warpManager;
    private TpaManager tpaManager;
    private HomeDatabase homeDatabase;
    private KitManager kitManager;
    private PlaceholderHook placeholderHook;
    private AnnouncerTask announcerTask;
    private CustomCommandsManager customCommandsManager;
    private KitsView kitsView;

    @Override
    public void onEnable() {
        instance = this;

        this.langManager = new LangManager(this);
        this.locationManager = new LocationManager(this);

        warpManager = new WarpManager(this);
        tpaManager = new TpaManager(this);
        kitManager = new KitManager(this);
        announcerTask = new AnnouncerTask(this);
        customCommandsManager = new CustomCommandsManager(this);
        kitsView = new KitsView(this);

        announcerTask.start();

        registerListeners();

        new CommandRegistry(this);
        saveDefaultConfig();

        try {
            homeDatabase = new HomeDatabase(getDataFolder() + "/database.db", getConfig());
        } catch (SQLException e) {
            getLogger().severe("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderHook = new PlaceholderHook(this);
            if (this.placeholderHook.register()) {
                getLogger().info("PlaceholderAPI hook registrado com sucesso!");
            }
        }

        getLogger().info("Plugin iniciado com sucesso!");
    }

    @Override
    public void onDisable() {
        try {
            if (homeDatabase != null) {
                homeDatabase.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (placeholderHook != null) {
            placeholderHook.unregister();
        }

        if (announcerTask != null) {
            announcerTask.cancel();
        }

        getLogger().info("Plugin desativado com sucesso!");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ContainerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BedListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CropListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PortalListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MobListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VoidListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CraftListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);
        Bukkit.getPluginManager().registerEvents(new NickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PluginListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WeatherListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FluidListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FireListener(this), this);
        Bukkit.getPluginManager().registerEvents(new HungerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    // ==================== API PÚBLICA ====================

    /**
     * Obtém a instância do plugin
     *
     * @return Instância do plugin
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Obtém a API de idiomas para uso externo
     *
     * @return API de idiomas ou null se o plugin não estiver carregado
     */
    public static LanguageAPI getLanguageAPI() {
        return instance != null ? instance.langManager : null;
    }
}