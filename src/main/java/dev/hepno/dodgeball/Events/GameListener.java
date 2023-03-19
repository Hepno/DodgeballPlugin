package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

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
                for (UUID uuid : arena.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);

                    // set players scoreboard to show the new score for red and blue, and check which team to update
                    if (arena.getTeam(event.getPlayer()) == Team.RED) {
                        player.getScoreboard().getTeam("redPoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.RED));
                    } else if (arena.getTeam(event.getPlayer()) == Team.BLUE) {
                        player.getScoreboard().getTeam("bluePoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.BLUE));
                    }

                }
            }
        }

    }

}
