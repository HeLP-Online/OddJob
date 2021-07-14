package com.spillhuset.Utils;

import com.spillhuset.Utils.Enum.ArenaType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class Game {
    private final UUID uuid;
    private String name;
    private int minPlayers,maxPlayers;
    private ArenaType type;
    private Location lobbySpawn;
    private HashMap<Integer,Location> spawns = new HashMap<>();
    private boolean active;

    public Game(UUID uuid, String name, int minPlayers, int maxPlayers, ArenaType type, Location lobbySpawn) {
        this.uuid = uuid;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.type = type;
        this.lobbySpawn = lobbySpawn;
    }

    public Game(UUID uuid, String name, int minPlayers, int maxPlayers, ArenaType type, Location lobbySpawn, HashMap<Integer, Location> spawns, boolean active) {
        this.uuid = uuid;
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.type = type;
        this.lobbySpawn = lobbySpawn;
        this.spawns = spawns;
        this.active = active;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public ArenaType getType() {
        return type;
    }

    public void setType(ArenaType type) {
        this.type = type;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }

    public HashMap<Integer, Location> getSpawns() {
        return spawns;
    }

    public Location getSpawn(int num) {
        return spawns.get(num);
    }

    public void setSpawn(int num,Location location) {
        spawns.put(num,location);
    }

    public void setSpawns(HashMap<Integer, Location> spawns) {
        this.spawns = spawns;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
