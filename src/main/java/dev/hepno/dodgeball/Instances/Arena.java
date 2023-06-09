package dev.hepno.dodgeball.Instances;

import com.google.common.collect.TreeMultimap;
import dev.hepno.dodgeball.BukkitSerialization;
import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import dev.hepno.dodgeball.Managers.DatabaseManager;
import dev.hepno.dodgeball.Teams.Team;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Arena {

    private Dodgeball plugin;

    private int id;
    private Location redSpawn;
    private Location blueSpawn;

    private GameState state;
    private List<UUID> players;
    private HashMap<UUID, Team> teams;
    private Countdown countdown;
    private Game game;

    private DatabaseManager databaseManager;

    public Arena(Dodgeball plugin, int id, Location redSpawn, Location blueSpawn) {
        this.id = id;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.plugin = plugin;

        this.state = GameState.RECRUITING;
        this.players = new ArrayList<>();
        this.teams = new HashMap<>();
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
                Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
            players.clear();
            teams.clear();
        }
        broadcastTitle("", "");
        state = GameState.RECRUITING;
        countdown.cancel();
        countdown = new Countdown(plugin, this);
        game = new Game(this);

        // Remove all dropped snowballs in the arena
        ConfigurationManager.setup(plugin);
        FileConfiguration config = plugin.getConfig();

        for (Entity entity : Objects.requireNonNull(redSpawn.getWorld()).getEntities()) {
                // Setup Boundingbox around the arena and remove all snowballs that are inside it
            BoundingBox arena = BoundingBox.of(new Location(redSpawn.getWorld(),
                        config.getDouble("arenas." + getId() + "arena-corner-1.x"),
                        config.getDouble("arenas." + getId() + "arena-corner-1.y"),
                        config.getDouble("arenas." + getId() + "arena-corner-1.z")),
                        config.getDouble("arenas." + getId() + "arena-corner-2.x"),
                        config.getDouble("arenas." + getId() + "arena-corner-2.y"),
                        config.getDouble("arenas." + getId() + "arena-corner-2.z"));
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
            }
        }
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
    public Dodgeball getPlugin() { return plugin; }
    public int getId() { return id; }

    public GameState getState() { return state; }
    public List<UUID> getPlayers() { return players; }
    public Game getGame() { return game; }

    public Team getTeam(Player player) { return teams.get(player.getUniqueId()); }

    public int getTeamCount(Team team) {
        int amount = 0;
        for (Team t : teams.values()) {
            if (t == team) {
                amount++;
            }
        }
        return amount;
    }

    // Setters
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());

        TreeMultimap<Integer, Team> count = TreeMultimap.create();
        for (Team team : Team.values()) {
            count.put(getTeamCount(team), team);
        }

        // Set the players scoreboard to the waiting room scoreboard
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("DodgeballWR", "dummy");
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lDodgeball"));

        Score one = objective.getScore(ChatColor.translateAlternateColorCodes('&', " "));
        one.setScore(5);
        Score two = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&7Waiting for"));
        two.setScore(4);
        Score three = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&7players to join..."));
        three.setScore(3);
        Score five = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&e"));
        five.setScore(2);
        Score six = objective.getScore(ChatColor.translateAlternateColorCodes('&', "&ewww.example.com"));
        six.setScore(1);

        player.setScoreboard(board);

        // Teams
        Team lowest = (Team) count.values().toArray()[0];
        setTeam(player, lowest);
        player.sendMessage("§aYou have been put on team " + lowest.getDisplay() + "!");
        ConfigurationManager.setup(plugin);
        FileConfiguration config = plugin.getConfig();

        // Teleport the player to the waiting room
        player.teleport(new Location(Bukkit.getWorld(config.getString("arenas." + getId() + ".waiting_room-spawn.world")),
                config.getDouble("arenas." + getId() + ".waiting_room-spawn.x"),
                config.getDouble("arenas." + getId() + ".waiting_room-spawn.y"),
                config.getDouble("arenas." + getId() + ".waiting_room-spawn.z"),
                (float) config.getInt("arenas." + getId() + ".waiting_room-spawn.yaw"),
                (float) config.getInt("arenas." + getId() + ".waiting_room-spawn.pitch")));

        if (state.equals(GameState.RECRUITING) && players.size() >= ConfigurationManager.getRequiredPlayers()) {
            // ensure that not every player is on the same team
            if (getTeamCount(Team.RED) > 0 || getTeamCount(Team.BLUE) > 0) {
                state = GameState.STARTING;
                countdown.start();
            }
        }

        // Encode players inventory into base64 and store it in the database for later use, then clear the inventory
        databaseManager = new DatabaseManager();

        try {
            databaseManager.connect();
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(
                    "INSERT INTO `dodgeball` (`uuid`, `inventory`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `inventory` = ?");
            Inventory inventory = Bukkit.createInventory(null, 45);
            inventory.setContents(player.getInventory().getContents());
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, BukkitSerialization.toBase64(inventory));
            statement.setString(3, BukkitSerialization.toBase64(inventory));
            statement.executeUpdate();
            player.getInventory().clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.teleport(ConfigurationManager.getLobbySpawn());
        player.sendTitle("", "");
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(board);

        if (state == GameState.STARTING && players.size() < ConfigurationManager.getRequiredPlayers()) {
            broadcast("§cNot enough players to start the game!");
            reset(false);
        }

        if (state == GameState.LIVE && players.size() < ConfigurationManager.getRequiredPlayers()) {
            broadcast("§cNot enough players to continue the game! The game has ended.");
            reset(true);
        }

        // Restore the players inventory from the database
        databaseManager = new DatabaseManager();
        Inventory inventory = null;

        try {
            databaseManager.connect();
            PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT * FROM `dodgeball` WHERE `uuid` = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                inventory = BukkitSerialization.fromBase64(resultSet.getString("inventory"));

                for (int i = 0; i < inventory.getSize(); i++) {
                    player.getInventory().setItem(i, inventory.getItem(i));
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTeam(Player player, Team team) {
        removeTeam(player);
        teams.put(player.getUniqueId(), team);
    }
    public void removeTeam(Player player) {
        if (teams.containsKey(player.getUniqueId())) {
            teams.remove(player.getUniqueId());
        }
    }

    public void setState(GameState state) { this.state = state; }
}
