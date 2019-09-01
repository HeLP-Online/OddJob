package no.helponline.Utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Arena {
    private int maxPlayers;
    private int minPlayers;
    private Status status;
    private Type type;
    private HashMap<Integer, Location> spawn;
    private String name;
    private List<UUID> players = new ArrayList<>();
    private List<UUID> queue = new ArrayList<>();

    public int next = 1;

    //TNTTag
    public int explosionCountdown = 30;

    public Arena(String name, Type type, int maxPlayers, int minPlayers, HashMap<Integer, Location> spawn) {
        this.name = name;
        this.type = type;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.status = Status.Stopped;
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, Location> getSpawn() {
        return spawn;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isStarted() {
        return (status.equals(Status.Started));
    }

    public Type getType() {
        return type;
    }

    public boolean isComplete() {
        return (status.equals(Status.Completed));
    }

    public Status getStatus() {
        return status;
    }

    public List<UUID> getQueue() {
        return queue;
    }

    public enum Type {
        TNTTag, HungerGames
    }

    public enum Status {
        Waiting, Started, Stopped, Completed
    }
}
