package dev.hepno.dodgeball.Commands;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Managers.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaForceStart implements CommandExecutor {

    private Dodgeball plugin;

    public ArenaForceStart(Dodgeball plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Debug command to force start the game
        Player player = (Player) sender;
        ArenaManager arenaManager = plugin.getArenaManager();
        if (plugin.getArenaManager().getArena(player) == null) { return false; }
        Arena arena = plugin.getArenaManager().getArena(player);

        arena.setState(GameState.LIVE);

        return false;
    }
}
