package com.spillhuset.Managers;

import com.spillhuset.SQL.ArenaSQL;
import com.spillhuset.Utils.Enum.ArenaType;
import com.spillhuset.Utils.Game;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {
    private HashMap<UUID, Game> games = new HashMap<>();
    private HashMap<String, Object> settings = new HashMap<>();

    public void load() {
        settings = ArenaSQL.loadSettings();
        games = ArenaSQL.load();
    }

    public void save() {
        ArenaSQL.saveSettings(settings);
        ArenaSQL.save(games);
    }

    public boolean create(String name, int minPlayers, int maxPlayers, ArenaType type, Location lobbySpawn) {
        UUID uuid = UUID.randomUUID();

        Game game = new Game(uuid, name, minPlayers, maxPlayers, type, lobbySpawn);

        games.put(uuid, game);
        save();
        return true;
    }

    public HashMap<UUID, Game> getGames() {
        return games;
    }
}
