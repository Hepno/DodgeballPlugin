package dev.hepno.dodgeball.Commands;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Teams.TeamGUI;
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
        if (!(sender instanceof Player)) { return false; }
        Player player = (Player) sender;
        if (!(player.hasPermission("dodgeball.arena"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /arena <list|join|leave|teams>");
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!(player.hasPermission("dodgeball.arena.list"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            player.sendMessage(ChatColor.GREEN + "List of arenas:");
            for (Arena arena : plugin.getArenaManager().getArenas()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a- &7Arena " + arena.getId() + " &a(&7" + arena.getState().toString() + "&a)"));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (!(player.hasPermission("dodgeball.arena.join"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena join <id>");
                return false;
            }
            Arena arena = plugin.getArenaManager().getArena(Integer.parseInt(args[1]));
            if (arena == null) {
                player.sendMessage(ChatColor.RED + "Arena " + args[1] + " does not exist!");
                return false;
            }
            if (arena.getState() == GameState.LIVE) {
                player.sendMessage(ChatColor.RED + "Arena " + args[1] + " is already in progress!");
                return false;
            }
            arena.addPlayer(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("teams")) {
            if (!(player.hasPermission("dodgeball.arena.teams"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena != null) {
                if (arena.getState() != GameState.LIVE) {
                    if (arena.getTeamCount(arena.getTeam(player)) == 1) {
                        player.sendMessage(ChatColor.RED + "You can't change teams while you are the only one in your team!");
                        return false;
                    }
                    new TeamGUI(arena, player);
                } else {
                    player.sendMessage(ChatColor.RED + "You can't change teams while the game is in progress!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not in an arena!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            if (!(player.hasPermission("dodgeball.arena.leave"))) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            Arena arena = plugin.getArenaManager().getArena(player);
            if (arena != null) {
                arena.removePlayer(player);
            } else {
                player.sendMessage(ChatColor.RED + "You are not in an arena!");
            }
            return true;
        }
        return false;
    }
}