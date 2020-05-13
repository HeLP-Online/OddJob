package no.helponline.Utils.Arena;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardPhase {
    Scoreboard scoreboard;
    Objective sidebar;
    String title;
    List<CustomScore> sScore;
    List<String> scores;

    public ScoreboardPhase(String title, List<String> scores) {
        if(title.length() > 32)
            title = title.substring(0, 32);
        this.title = title;

        for(String score : scores) {
            String[] split = score.split("//");
            if(split[0].length() > 48)
                split[0] = split[0].substring(0, 48);
            score = split[0] + "//" + split[1];
            this.scores.add(score);
        }
    }

    public Scoreboard initScoreboard(Game game) {
        if (Bukkit.getScoreboardManager() != null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            sidebar = scoreboard.registerNewObjective("sidebar", "dummy", "dummy");
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

            sidebar.setDisplayName(title);

            sScore = new ArrayList<>();
            int tName = 0;
            for (String score : scores) {
                String[] split = score.split("//");
                String name = split[0];
                String extra = null;

                if (name.contains("%arena%")) {
                    Arena arena = game.getCurrentArena();
                    if (arena != null) {
                        extra = arena.getName();
                        name = name.replace("%arena%", extra);
                        if (name.length() > 48) name = name.substring(0, 48);
                    }
                }

                String regex = split[1];
                String scoreName = name;
                Team team = null;
                if (name.length() > 16) {
                    team = scoreboard.registerNewTeam(Integer.valueOf(tName).toString());
                    team.setPrefix(name.substring(0, 16));
                    if (name.length() > 32) {
                        scoreName = name.substring(16, 32);
                        team.setSuffix(name.substring(32));
                    } else {
                        scoreName = name.substring(16);
                    }
                    tName++;
                }

                Score s = sidebar.getScore(scoreName);
                if (team != null) team.addEntry(s.getEntry());
                s.setScore(-1);
                sScore.add(new CustomScore(s, name, regex, team, extra));
            }
            return scoreboard;
        } return  null;
    }

    public List<CustomScore> getScores() {
        return sScore;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    public ScoreboardPhase clone() {
        return new ScoreboardPhase(title, scores);
    }
}
