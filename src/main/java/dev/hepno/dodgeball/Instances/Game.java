package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.GameState;
import org.bukkit.entity.Player;

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

        for (UUID uuid : arena.getPlayers()) {
            points.put(uuid, 0);
        }
    }

    public void addPoint(Player player) {
        int playerPoints = points.get(player.getUniqueId()) + 1;
        if (playerPoints == 20) {
            arena.broadcast(player.getName() + " has won the game!");
            arena.reset();
        }

        arena.broadcast(player.getName() + " now has " + playerPoints + " points!");
        points.replace(player.getUniqueId(), playerPoints);
    }

}
