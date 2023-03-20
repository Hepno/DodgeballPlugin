package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import dev.hepno.dodgeball.Teams.Team;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private Dodgeball plugin;
    public GUIListener(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != null && event.getCurrentItem() != null && event.getView().getTitle().contains("Select a team")) {
            if (event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
                event.setCancelled(true);
                return;
            }
            Team team = Team.valueOf(event.getCurrentItem().getItemMeta().getLocalizedName());
            Player player = (Player) event.getWhoClicked();

            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena != null) {
                if (arena.getTeam(player) == team) {
                    player.sendMessage("You are already on this team!");
                } else {
                    player.sendMessage("You have joined the " + team.getDisplay() + " team!");
                    arena.setTeam(player, team);
                    FileConfiguration config = plugin.getConfig();
                    player.teleport(new org.bukkit.Location(
                            player.getWorld(),
                            config.getDouble("arenas." + arena.getId() + "." + team.name().toLowerCase() + "-spawn.x"),
                            config.getDouble("arenas." + arena.getId() + "." + team.name().toLowerCase() + "-spawn.y"),
                            config.getDouble("arenas." + arena.getId() + "." + team.name().toLowerCase() + "-spawn.z"),
                            (float) config.getDouble("arenas." + arena.getId() + "." + team.name().toLowerCase() + "-spawn.yaw"),
                            (float) config.getDouble("arenas." + arena.getId() + "." + team.name().toLowerCase() + "-spawn.pitch")));
                }
            }
            player.closeInventory();
            event.setCancelled(true);
        }
    }
}
