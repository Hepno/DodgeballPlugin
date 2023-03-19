package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {

    private Dodgeball plugin;
    public ConnectListener(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(ConfigurationManager.getLobbySpawn());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Arena arena = plugin.getArenaManager().getArena(event.getPlayer());
        if (arena != null) {
            arena.removePlayer(event.getPlayer());
        }

    }

}
