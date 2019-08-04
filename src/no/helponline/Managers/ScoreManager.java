package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScoreManager {
    private HashMap<UUID, Scoreboard> scoreboards = new HashMap<>(); // PLayers -> Scoreboard
    private ScoreboardManager scoreboardManager;

    public ScoreManager() {
        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public Scoreboard getScoreboard(UUID player) {
        if (!scoreboards.containsKey(player)) {
            setScoreboard(player);
        }
        return scoreboards.get(player);
    }

    private void setScoreboard(UUID player) {
        Scoreboard scoreboard = getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.getObjective("guild");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("guild", "", "Guild");
        }
        scoreboards.put(player, scoreboard);
    }

    private void setScore(UUID player) {
        List<Objective> objectives = getScoreboard(player).getObjectives();
        objectives
    }
}
