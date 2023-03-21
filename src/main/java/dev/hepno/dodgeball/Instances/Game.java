package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import dev.hepno.dodgeball.Teams.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Game {

    private Arena arena;
    private HashMap<Team, Integer> points;

    public Game(Arena arena) {
        this.arena = arena;
        points = new HashMap<>();
    }

    public void start() {
        Dodgeball plugin = arena.getPlugin();
        ConfigurationManager.setup(plugin);
        FileConfiguration config = plugin.getConfig();

        arena.setState(GameState.LIVE);
        arena.broadcast("Game started!");

        if (config.getBoolean("use-scoreboard")) {

            // Clear old scoreboard
            for (UUID uuid : arena.getPlayers()) {
                Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            // Create new scoreboard

            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("Dodgeball", "dummy");
            objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lDodgeball"));

            Score one = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&e"));
            one.setScore(5);
            Score four = objective.getScore(ChatColor.translateAlternateColorCodes('&', "  "));
            four.setScore(2);
            Score five = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&ewww.example.com"));
            five.setScore(1);

            org.bukkit.scoreboard.Team bluePoints = board.registerNewTeam("bluePoints");
            bluePoints.addEntry(ChatColor.translateAlternateColorCodes('&', "&9"));
            bluePoints.setSuffix(ChatColor.translateAlternateColorCodes('&', "&f0"));
            bluePoints.setPrefix(ChatColor.translateAlternateColorCodes('&', "&9Blue Team &7- "));

            org.bukkit.scoreboard.Team redPoints = board.registerNewTeam("redPoints");
            redPoints.addEntry(ChatColor.translateAlternateColorCodes('&', "&c"));
            redPoints.setSuffix(ChatColor.translateAlternateColorCodes('&', "&f0"));
            redPoints.setPrefix(ChatColor.translateAlternateColorCodes('&', "&cRed Team &7- "));

            Score two = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&c"));
            two.setScore(3);
            objective.getScore(ChatColor.translateAlternateColorCodes('&', "&9")).setScore(4);

            // Show scoreboard to & teleport all players that are in the arena using a for loop
            for (UUID uuid : arena.getPlayers()) {
                Bukkit.getPlayer(uuid).setScoreboard(board);
            }
        }

            // Teleport all players
            for (UUID uuid : arena.getPlayers()) {
                Bukkit.getPlayer(uuid).getInventory().addItem(new ItemStack(Material.SNOWBALL, config.getInt("snowballs-per-player")));
                if (arena.getTeam(Bukkit.getPlayer(uuid)) == Team.BLUE) {
                    Bukkit.getPlayer(uuid).teleport(new Location(
                            Bukkit.getWorld(config.getString("arenas." + arena.getId() + ".blue-spawn.world")),
                            config.getDouble("arenas." + arena.getId() + ".blue-spawn.x"),
                            config.getDouble("arenas." + arena.getId() + ".blue-spawn.y"),
                            config.getDouble("arenas." + arena.getId() + ".blue-spawn.z"),
                            (float) config.getDouble("arenas." + arena.getId() + ".blue-spawn.yaw"),
                            (float) config.getDouble("arenas." + arena.getId() + ".blue-spawn.pitch")
                    ));
                } else {
                    Bukkit.getPlayer(uuid).teleport(new Location(
                            Bukkit.getWorld(config.getString("arenas." + arena.getId() + ".red-spawn.world")),
                            config.getDouble("arenas." + arena.getId() + ".red-spawn.x"),
                            config.getDouble("arenas." + arena.getId() + ".red-spawn.y"),
                            config.getDouble("arenas." + arena.getId() + ".red-spawn.z"),
                            (float) config.getDouble("arenas." + arena.getId() + ".red-spawn.yaw"),
                            (float) config.getDouble("arenas." + arena.getId() + ".red-spawn.pitch")
                    ));
                }
            }

        // Set points to 0 for both teams
        points.put(Team.BLUE, 0);
        points.put(Team.RED, 0);
    }

    public void addPoint(Team team) {
        int teamPoints = points.get(team) + 1;
        if (teamPoints == 10) {
            arena.broadcast(team.getDisplay() + " has won the game!");
            arena.reset(true);
            FileConfiguration config = arena.getPlugin().getConfig();
            List<String> list = config.getStringList("win-commands");
            int length = list.size();

            // Run all commands in the win-commands section
            for (int i = 1; i <= length; i++) {
                if (list.get(i - 1) != null) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), list.get(i - 1));
                }
            }
        }

        arena.broadcast(team.getDisplay() + " now has " + teamPoints + " points!");
        points.replace(team, teamPoints);
    }

    public int getPoints(Team team) {
        return points.get(team);
    }

}
