package dev.hepno.dodgeball;

import dev.hepno.dodgeball.Managers.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dodgeball extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        RegisterCommands();
        RegisterEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaManager getArenaManager() { return arenaManager; }

    public void RegisterCommands() {

    }

    public void RegisterEvents() {

    }

    // Arena World

}