package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Countdown extends BukkitRunnable {

    private Dodgeball plugin;
    private Arena arena;
    private int countdownTime = 10;

    public Countdown(Dodgeball plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.countdownTime = ConfigurationManager.getCountdownTime();
    }

    public void start() {
        arena.setState(GameState.STARTING);
        runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (countdownTime == 0) {
            cancel();
            arena.start();
            arena.broadcastTitle("", "");
            for (UUID uuid : arena.getPlayers()) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
            }
            return;
        }

        if (countdownTime <= 10 || countdownTime % 15 == 0) {
            arena.broadcast(ChatColor.translateAlternateColorCodes('&', "Game starting in " + countdownTime + " second" + (countdownTime == 1 ? "" : "s" + "!")));
        }
        if (countdownTime <= 3) {
            for (UUID uuid : arena.getPlayers()) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
        }
        arena.broadcastTitle(ChatColor.GREEN.toString() + countdownTime, "");

        countdownTime--;
    }
}
