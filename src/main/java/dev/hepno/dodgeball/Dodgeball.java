package dev.hepno.dodgeball;

import dev.hepno.dodgeball.Commands.ArenaCommand;
import dev.hepno.dodgeball.Events.ConnectListener;
import dev.hepno.dodgeball.Events.GameListener;
import dev.hepno.dodgeball.Managers.ArenaManager;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dodgeball extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        ConfigurationManager.setup(this);
        arenaManager = new ArenaManager(this);
        RegisterCommands();
        RegisterEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaManager getArenaManager() { return arenaManager; }

    public void RegisterCommands() {
        getCommand("arena").setExecutor(new ArenaCommand(this));
    }

    public void RegisterEvents() {
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
    }

    // Arena World

}