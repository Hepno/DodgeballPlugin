package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Teams.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GameListener implements Listener {

    private Dodgeball plugin;

    public GameListener(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Arena arena = plugin.getArenaManager().getArena(event.getPlayer());
        Team playerTeam = arena.getTeam(event.getPlayer());
        if (arena != null) {
            if (arena.getState() == GameState.LIVE) {
                arena.getGame().addPoint(playerTeam);
            }
        }

    }

}
