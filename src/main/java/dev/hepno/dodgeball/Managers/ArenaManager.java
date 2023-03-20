package dev.hepno.dodgeball.Managers;

import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Dodgeball;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private List<Arena> arenas = new ArrayList<>();

    public ArenaManager(Dodgeball plugin) {
        FileConfiguration config = plugin.getConfig();
        for (String str : config.getConfigurationSection("arenas.").getKeys(false)) {
            arenas.add(new Arena(plugin, Integer.parseInt(str), new Location(
                    Bukkit.getWorld(config.getString("arenas." + str + ".red-spawn.world")),
                    config.getDouble("arenas." + str + ".red-spawn.x"),
                    config.getDouble("arenas." + str + ".red-spawn.y"),
                    config.getDouble("arenas." + str + ".red-spawn.z"),
                    (float) config.getDouble("arenas." + str + ".red-spawn.yaw"),
                    (float) config.getDouble("arenas." + str + ".red-spawn.pitch"))
            , new Location(
                    Bukkit.getWorld(config.getString("arenas." + str + ".blue-spawn.world")),
                    config.getDouble("arenas." + str + ".blue-spawn.x"),
                    config.getDouble("arenas." + str + ".blue-spawn.y"),
                    config.getDouble("arenas." + str + ".blue-spawn.z"),
                    (float) config.getDouble("arenas." + str + ".blue-spawn.yaw"),
                    (float) config.getDouble("arenas." + str + ".blue-spawn.pitch"))));
        }
    }

    public List<Arena> getArenas() { return arenas; }
    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getId() == id) {
                return arena;
            }
        }
        return null;
    }

}
