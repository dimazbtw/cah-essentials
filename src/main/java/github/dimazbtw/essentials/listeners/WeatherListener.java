package github.dimazbtw.essentials.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import github.dimazbtw.essentials.Main;

public class WeatherListener implements Listener {

    private final Main plugin;

    public WeatherListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (plugin.getConfig().getBoolean("mundo.desativar-chuva", false)) {
            if (event.toWeatherState()) {
                event.setCancelled(true);
            }
        }
    }
}
