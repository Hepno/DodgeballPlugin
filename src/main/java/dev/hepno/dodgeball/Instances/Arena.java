package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {

    private Dodgeball plugin;

    private int id;
    private Location spawn;

    private GameState state;
    private List<UUID> players;
    private Countdown countdown;
    private Game game;

    public Arena(Dodgeball plugin, int id, Location spawn) {
        this.id = id;
        this.spawn = spawn;
        this.plugin = plugin;

        this.state = GameState.RECRUITING;
        this.players = new ArrayList<>();
        this.countdown = new Countdown(plugin, this);
        this.game = new Game(this);
    }

    // Game
    public void start() { game.start(); }

    public void reset(boolean kickPlayers) {
        if (kickPlayers) {
            Location location = ConfigurationManager.getLobbySpawn();
            for (UUID uuid : players) {
                Bukkit.getPlayer(uuid).teleport(location);
            }
            players.clear();
        }
        broadcastTitle("", "");
        state = GameState.RECRUITING;
        countdown.cancel();
        countdown = new Countdown(plugin, this);
        game = new Game(this);
    }

    // Tools
    public void broadcast(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }

    public void broadcastTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendTitle(title, subtitle);
        }
    }

    // Getters
    public int getId() { return id; }

    public GameState getState() { return state; }
    public List<UUID> getPlayers() { return players; }
    public Game getGame() { return game; }

    // Setters
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        player.teleport(spawn);

        if (state.equals(GameState.RECRUITING) && players.size() >= ConfigurationManager.getRequiredPlayers()) {
            state = GameState.STARTING;
            countdown.start();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.teleport(ConfigurationManager.getLobbySpawn());

        if (state == GameState.STARTING && players.size() < ConfigurationManager.getRequiredPlayers()) {
            broadcast("§cNot enough players to start the game!");
            reset(false);
        }

        if (state == GameState.LIVE && players.size() < ConfigurationManager.getRequiredPlayers()) {
            broadcast("§cNot enough players to continue the game! The game has ended.");
            reset(true);
        }
    }

    public void setState(GameState state) { this.state = state; }
}
