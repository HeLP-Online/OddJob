package com.spillhuset.Managers;

import com.spillhuset.Utils.Game;
import com.spillhuset.Utils.RollbackHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ArenaManager {
    private HashSet<Game> games = new HashSet<>();
    private int gamesLimit = 1;
    private Location lobbyPoint = null;
    private HashMap<UUID,Game> playerGameMap = new HashMap<>();

    public void disable() {
        for (Game game : games) {
            for (Player player : game.getWorld().getPlayers()) {
                player.teleport(getLobbyPoint());
            }
            RollbackHandler.getRollbackHandler().rollback(game.getWorld());
        }
    }

    public Location getLobbyPoint() {
        if (lobbyPoint == null) {
            new Location(Bukkit.getWorld("world"), 0, 65, 0);
        }
        return lobbyPoint;
    }

    public boolean registerGame(Game game) {
        if (games.size() >= gamesLimit && gamesLimit != -1) {
            return false;
        }
        games.add(game);
        return true;
    }

    public Game getGame(String gameName) {
        for (Game game : games) {
            if(game.getDisplayName().equalsIgnoreCase(gameName)) {
                return game;
            }
        }
        return null;
    }

    public Game getGame(UUID player) {
        return playerGameMap.get(player);
    }

    public void setGame(UUID player,Game game) {
        if (game == null) {
            playerGameMap.remove(player);
        } else {
            playerGameMap.put(player,game);
        }
    }

    public HashSet<Game> getGames() {
        return games;
    }
}
