package dev.hepno.dodgeball.Instances;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.GameState;
import dev.hepno.dodgeball.Managers.ConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private Dodgeball dodgeball;
    private Arena arena;
    private int countdownTime = 10;

    public Countdown(Dodgeball dodgeball, Arena arena) {
        this.dodgeball = dodgeball;
        this.arena = arena;
        this.countdownTime = ConfigurationManager.getCountdownTime();
    }

    public void start() {
        arena.setState(GameState.STARTING);
        runTaskTimer(dodgeball, 0, 20);
    }

    @Override
    public void run() {
        if (countdownTime == 0) {
            cancel();
            arena.start();
            return;
        }

        if (countdownTime <= 10 || countdownTime % 15 == 0) {
            arena.broadcast(ChatColor.translateAlternateColorCodes('&', "Game starting in " + countdownTime + " seconds!" + (countdownTime == 1 ? "" : "s")));

        }

        countdownTime--;
    }
}
