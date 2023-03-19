package dev.hepno.dodgeball.Teams;

import dev.hepno.dodgeball.Instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamGUI {

    public TeamGUI(Arena arena, Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Select a team");

        for (int i = 0; i < 54; i++) {
            ItemStack is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(" ");
            is.setItemMeta(isMeta);

            gui.setItem(i, is);
        }

        for (Team team : Team.values()) {
            ItemStack is = new ItemStack(team.getMaterial());
            ItemMeta isMeta = is.getItemMeta();
            isMeta.setDisplayName(team.getDisplay() + ChatColor.GRAY + " (" + arena.getTeamCount(team) + " players)");
            isMeta.setLocalizedName(team.name());
            is.setItemMeta(isMeta);

            gui.addItem(is);
        }

        ItemStack is = new ItemStack(Team.BLUE.getMaterial());
        ItemMeta isMeta = is.getItemMeta();
        isMeta.setDisplayName(Team.BLUE.getDisplay() + ChatColor.GRAY + " (" + arena.getTeamCount(Team.BLUE) + " players)");
        isMeta.setLocalizedName(Team.BLUE.name());
        is.setItemMeta(isMeta);

        ItemStack is2 = new ItemStack(Team.RED.getMaterial());
        ItemMeta isMeta2 = is2.getItemMeta();
        isMeta2.setDisplayName(Team.RED.getDisplay() + ChatColor.GRAY + " (" + arena.getTeamCount(Team.RED) + " players)");
        isMeta2.setLocalizedName(Team.RED.name());
        is2.setItemMeta(isMeta2);

        gui.setItem(21, is);
        gui.setItem(23, is2);

        player.openInventory(gui);
    }

}
