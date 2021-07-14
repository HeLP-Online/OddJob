package com.spillhuset.Managers;

import com.spillhuset.SQL.ArenaSQL;
import com.spillhuset.Utils.Enum.ArenaType;
import com.spillhuset.Utils.Game;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {
    private HashMap<UUID,Game> games = new HashMap<>();

    public void load() {
        games = ArenaSQL.load();
    }

    public void save() {
        ArenaSQL.save(games);
    }

    public void create(String name, int minPlayers, int maxPlayers, ArenaType type, Location lobbySpawn) {
        UUID uuid = UUID.randomUUID();

        Game game = new Game(uuid,name,minPlayers,maxPlayers,type,lobbySpawn);
    }
}
