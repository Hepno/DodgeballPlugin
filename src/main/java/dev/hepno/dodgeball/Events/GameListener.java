package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Instances.Game;
import dev.hepno.dodgeball.Managers.ArenaManager;
import dev.hepno.dodgeball.Teams.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.util.UUID;

public class GameListener implements Listener {

    private Dodgeball plugin;

    public GameListener(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {

        if (event.getEntity().getShooter() instanceof Player && event.getEntity().getType() == EntityType.SNOWBALL) {

            // Initialize variables
            Player hitPlayer = (Player) event.getHitEntity();
            Player shooter = (Player) event.getEntity().getShooter();
            ArenaManager arenaManager = plugin.getArenaManager();
            if (arenaManager.getArena(shooter) == null) return;
            Arena arena = arenaManager.getArena(shooter);
            Game game = arena.getGame();
            FileConfiguration config = plugin.getConfig();

            if (event.getHitEntity() instanceof Player) {

                // Check if its hitting a player while the game is live
                if (event.getHitEntity() != null && event.getHitEntity() instanceof Player && arena.getState() == GameState.LIVE) {
                    if (arena.getTeam(hitPlayer) == arena.getTeam(shooter)) { return; }
                    game.addPoint(arena.getTeam(shooter));
                    hitPlayer.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));

                    // Add points to scoreboard
                    for (UUID uuid : arena.getPlayers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (!(config.getBoolean("use-scoreboard"))) {
                            return;
                        }
                        if (arena.getTeam(player) == Team.RED) {
                            player.getScoreboard().getTeam("redPoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.RED));
                        } else if (arena.getTeam(player) == Team.BLUE) {
                            player.getScoreboard().getTeam("bluePoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.BLUE));
                        }
                    }
                }
            }

            // Check if snowball is hitting the ground so it can drop a snowball
            if (event.getHitBlock() != null && arena.getState() == GameState.LIVE) {
                event.getHitBlock().getLocation().getWorld().dropItem(event.getHitBlock().getLocation(), new ItemStack(Material.SNOWBALL));
            }
        }
    }

    // Stop players from taking damage
    @EventHandler
    public void onInjury(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            ArenaManager arenaManager = plugin.getArenaManager();
            if (arenaManager.getArena((Player) event.getEntity()) == null) return;
            Arena arena = arenaManager.getArena((Player) event.getEntity());
            if (arena.getPlayers().contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // Stop players from crossing the middle line
    @EventHandler
    public void onCrossLine(PlayerMoveEvent event) {
        World world = event.getPlayer().getWorld();
        Location location = event.getPlayer().getLocation();
        FileConfiguration config = plugin.getConfig();
        if (plugin.getArenaManager().getArena(event.getPlayer()) == null) return;
        Arena arena = plugin.getArenaManager().getArena(event.getPlayer());
        if (arena.getState() != GameState.LIVE) return;

        // Setup a bounding box for the middle line from the config
        BoundingBox redLine = BoundingBox.of(new Location(world, config.getDouble("arenas." + arena.getId()
                + ".line-corner-1.x"), 0, config.getDouble("arenas." + arena.getId() + ".line-corner-1.z")),
                new Location(world, config.getDouble("arenas." + arena.getId() + ".line-corner-2.x"), 319,
                config.getDouble("arenas." + arena.getId() + ".line-corner-2.z")));

        // Make it so that if players enter the bounding box, the event is cancelled
        if (redLine.contains(event.getTo().toVector())) {
            event.setCancelled(true);
            // send action bar message
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(
                    ChatColor.RED + "You can't cross the middle line!").create());


        }

    }

    // Stop players from starving
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            ArenaManager arenaManager = plugin.getArenaManager();
            if (arenaManager.getArena((Player) event.getEntity()) == null) return;
            Arena arena = arenaManager.getArena((Player) event.getEntity());
            if (arena.getPlayers().contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // Stop players from breaking blocks
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.getArenaManager().getArena(event.getPlayer()) == null) return;

        Arena arena = plugin.getArenaManager().getArena(event.getPlayer());
        if (arena.getPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // Stop players from placing blocks
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getArenaManager().getArena(event.getPlayer()) == null) return;

        Arena arena = plugin.getArenaManager().getArena(event.getPlayer());
        if (arena.getPlayers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}