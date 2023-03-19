package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.UUID;

public class Game {

    private Arena arena;
    private HashMap<UUID, Integer> points;

    public Game(Arena arena) {
        this.arena = arena;
        points = new HashMap<>();
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.broadcast("Game started!");

        // Build scoreboard
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("Dodgeball", "dummy");
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lDodgeball"));

        Score one = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&e")); one.setScore(5);
        Score two = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&9Blue Team &7- &f0")); two.setScore(4);
        Score three = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&cRed Team &7- &f0")); three.setScore(3);
        Score four = objective.getScore(ChatColor.translateAlternateColorCodes('&', "  ")); four.setScore(2);
        Score five = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&ewww.example.com")); five.setScore(1);


        // Show scoreboard to all players that are in the arena using a for loop
        for (UUID uuid : arena.getPlayers()) {
            Bukkit.getPlayer(uuid).setScoreboard(board);
        }

        for (UUID uuid : arena.getPlayers()) {
            points.put(uuid, 0);
        }
    }

    public void addPoint(Player player) {
        int playerPoints = points.get(player.getUniqueId()) + 1;
        if (playerPoints == 10) {
            arena.broadcast(player.getName() + " has won the game!");
            arena.reset(true);
        }

        arena.broadcast(player.getName() + " now has " + playerPoints + " points!");
        points.replace(player.getUniqueId(), playerPoints);
    }

}
