package dev.hepno.dodgeball.Placeholders;

import dev.hepno.dodgeball.Dodgeball;
import dev.hepno.dodgeball.Instances.Arena;
import dev.hepno.dodgeball.Instances.Game;
import dev.hepno.dodgeball.Teams.Team;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PointsPlaceholder extends PlaceholderExpansion {

    private Team team;
    private String teamName;
    private Dodgeball plugin;

    public PointsPlaceholder(Team team, Dodgeball plugin) {
        if (team == null) {
            // Handle the logic if the required team wasn't provided
        }
        this.team = team;
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        if (teamName == null) {
            teamName = team.toString().toLowerCase();
            teamName = teamName.substring(0, 1).toUpperCase() + teamName.substring(1);
            teamName = teamName.replaceAll("_", "");
        }
        return "teamPoints" + teamName;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hepno";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (plugin.getArenaManager().getArena(player.getPlayer()) == null) {
            return "0";
        }
        Arena arena = plugin.getArenaManager().getArena(player.getPlayer());
        Game game = arena.getGame();
        int points = game.getPoints(team);
        return points + "";
    }
}
