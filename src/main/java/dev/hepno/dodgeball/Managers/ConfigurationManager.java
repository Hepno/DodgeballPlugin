package dev.hepno.dodgeball.Managers;

import dev.hepno.dodgeball.Dodgeball;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationManager {

    private static FileConfiguration config;

    public static void setup(Dodgeball plugin) {
        ConfigurationManager.config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }

    public static Location getLobbySpawn() {
        return new Location(
                Bukkit.getWorld(config.getString("lobby-spawn.world")),
                config.getDouble("lobby-spawn.x"),
                config.getDouble("lobby-spawn.y"),
                config.getDouble("lobby-spawn.z"),
                (float) config.getDouble("lobby-spawn.yaw"),
                (float) config.getDouble("lobby-spawn.pitch")
        );
    }

    public static int getCountdownTime() { return config.getInt("countdown-time"); }
}
