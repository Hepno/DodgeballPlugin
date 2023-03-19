package dev.hepno.dodgeball.Events;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Teams.Team;
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
            Team team = Team.valueOf(event.getCurrentItem().getItemMeta().getLocalizedName());
            Player player = (Player) event.getWhoClicked();

            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena != null) {
                if (arena.getTeam(player) == team) {
                    player.sendMessage("You are already on this team!");
                } else {
                    player.sendMessage("You have joined the " + team.getDisplay() + " team!");
                    arena.setTeam(player, team);
                }
            }
            player.closeInventory();
            event.setCancelled(true);
        }
    }
}
