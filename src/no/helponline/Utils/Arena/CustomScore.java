package no.helponline.Utils.Arena;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class CustomScore {
    String name,regex,extra;
    Team team;
    Score score;
    public CustomScore(Score score, String name, String regex, Team team, String extra) {
        this.regex = regex.toLowerCase();
        this.name = name;
        this.team = team;
        this.score = score;
        this.extra = extra;
    }

    public void update(Game game) {
        score.setScore(getData(game));
    }

    private int getData(Game game) {
        int i = -1;
        switch (regex) {
            case "%requiredplayers%":
                i = game.getRequiredPlayers();
                break;
            case"%playing%":
                i = game.getPlayingUsers();
                break;
            case"%death%":
                i = game.getDeathAmount();
                break;
            case"%spectators%":
                i = game.getSpectators().size();
                break;
        }
        if (i == -1) {
            if (extra.equals("%time%")) {
                switch (game.getGameState()) {
                    case COOLDOWN:
                        i = game.getCooldownPhase().getTime();
                        break;
                    case INGAME:
                        i = game.getIngamePhase().getTime();
                        break;
                    case DEATHMATCH:
                        i = game.getDeathmatchPhase().getTime();
                        break;
                    default:
                        break;
                }
            }
        }
        return i;
    }
}
