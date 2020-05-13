package no.helponline.Utils.Arena;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private List<Game> games = new ArrayList<>();
    public Game getGame(String value) {
        for (Game game: games) {
            if (game.getName().equalsIgnoreCase(value)) return game;
        }
        return null;
    }
}
