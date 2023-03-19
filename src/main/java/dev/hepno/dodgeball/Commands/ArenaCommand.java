package dev.hepno.dodgeball.Commands;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private Dodgeball plugin;

    public ArenaCommand(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                player.sendMessage(ChatColor.GREEN + "List of arenas:");
                for (Arena arena : plugin.getArenaManager().getArenas()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a- &7Arena " + arena.getId() + " &a(&7" + arena.getState().name() + "&a)"));
                }
            } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                Arena arena = plugin.getArenaManager().getArena(player);
                if (arena != null) {
                    arena.removePlayer(player);
                    player.sendMessage(ChatColor.RED + "You have left the arena!");
                } else {
                    player.sendMessage(ChatColor.RED + "You are not in an arena!");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
                if (plugin.getArenaManager().getArena(player) != null) {
                    player.sendMessage(ChatColor.RED + "You are already in an arena!");
                    return false;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);

                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid arena id!");
                    return false;
                }

                if (id >= 0 && id < plugin.getArenaManager().getArenas().size()) {
                    Arena arena = plugin.getArenaManager().getArenas().get(id);
                    if (arena.getState() == GameState.RECRUITING || arena.getState() == GameState.STARTING) {
                        player.sendMessage(ChatColor.GREEN + "You have joined arena " + id + "!");
                        arena.addPlayer(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "This arena is already in progress!");
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "Invalid arena id!");
                    return false;
                }
            } else {
                player.sendMessage("Usage: /arena <list|join|leave>");
            }


        }

        return false;
    }
}
