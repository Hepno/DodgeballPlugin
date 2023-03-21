package dev.hepno.dodgeball;

import dev.hepno.dodgeball.Commands.ArenaCommand;
import dev.hepno.dodgeball.Events.ConnectListener;
import dev.hepno.dodgeball.Events.GUIListener;
import dev.hepno.dodgeball.Events.GameListener;
import dev.hepno.dodgeball.Managers.ArenaManager;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import dev.hepno.dodgeball.Managers.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class Dodgeball extends JavaPlugin {

    private ArenaManager arenaManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        ConfigurationManager.setup(this);
        arenaManager = new ArenaManager(this);
        RegisterCommands();
        RegisterEvents();

        // Database
        databaseManager = new DatabaseManager();
        try {
            databaseManager.connect();
            // Create table if not exists (uuid, wins, losses, saved base64 inventory)
            PreparedStatement statement = databaseManager.getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS dodgeball (uuid VARCHAR(36) primary key, wins INT, losses INT, inventory TEXT)");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            databaseManager.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArenaManager getArenaManager() { return arenaManager; }

    public void RegisterCommands() {
        getCommand("arena").setExecutor(new ArenaCommand(this));
    }

    public void RegisterEvents() {
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
    }

    // Arena World

}