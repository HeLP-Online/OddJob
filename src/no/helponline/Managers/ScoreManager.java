package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreManager {
    private Scoreboard scoreboard;
    private Map<String, Integer> scores;
    private List<Team> teams;
    private String title;

    public ScoreManager(String title) {
        this.title = title;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teams = new ArrayList<>();
    }

    public void blankLine() {
        add(" ");
    }

    public void add(String text) {
        add(text, null);
    }

    public void add(String text, Integer score) {
        scores.put(text, score);
    }

    private Map.Entry<Team, String> createTeam(String text) {
        String result = "";
        if (text.length() <= 16) return new AbstractMap.SimpleEntry<>(null, text);
        Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    public void build() {
        Objective objective = scoreboard.registerNewObjective(title, "", title);
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Map.Entry<Team, String> team = createTeam(text.getKey());
            Integer score = text.getValue() != null ? text.getValue() : index;
            if (team.getKey() != null) team.getKey().addEntry(team.getValue());
            objective.getScore(team.getValue()).setScore(score);
            index -= 1;
        }
    }

    public void reset() {
        title = "";
        scores.clear();
        for (Team t : teams) t.unregister();
        teams.clear();
    }
}
