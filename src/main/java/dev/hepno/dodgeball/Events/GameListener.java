package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Instances.Game;
import dev.hepno.dodgeball.Managers.ArenaManager;
import dev.hepno.dodgeball.Teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GameListener implements Listener {

    private Dodgeball plugin;

    public GameListener(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        Player hitPlayer = (Player) event.getHitEntity();
        Player shooter = (Player) event.getEntity().getShooter();
        ArenaManager arenaManager = plugin.getArenaManager();
        if (arenaManager.getArena(shooter) == null) return;
        Arena arena = arenaManager.getArena(shooter);
        Game game = arena.getGame();

        if (event.getEntity().getShooter() instanceof Player && event.getEntity().getType() == EntityType.SNOWBALL) {
                if (event.getHitEntity() != null && event.getHitEntity() instanceof Player && arena.getState() == GameState.LIVE) {
                    if (arena.getTeam(hitPlayer) == arena.getTeam(shooter)) { return; }
                    game.addPoint(arena.getTeam(shooter));

                    for (UUID uuid : arena.getPlayers()) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (arena.getTeam(player) == Team.RED) {
                            player.getScoreboard().getTeam("redPoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.RED));
                        } else if (arena.getTeam(player) == Team.BLUE) {
                            player.getScoreboard().getTeam("bluePoints").setSuffix(ChatColor.WHITE + " " + arena.getGame().getPoints(Team.BLUE));
                        }
                    }
            }

            if (event.getHitBlock() != null && arena.getState() == GameState.LIVE) {
                ((Player) event.getEntity().getShooter()).getInventory().removeItem(new ItemStack(Material.SNOWBALL, 1));
                event.getHitBlock().getLocation().getWorld().dropItem(event.getHitBlock().getLocation(), new ItemStack(Material.SNOWBALL));
            }
        }
    }

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
}