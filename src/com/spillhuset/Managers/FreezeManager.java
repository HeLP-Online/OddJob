package com.spillhuset.Managers;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class FreezeManager {
    private final HashMap<UUID, Location> frozen = new HashMap<>();

    public void add(UUID player, Location location) {
        frozen.put(player, location);
    }

    public Location get(UUID player) {
        return frozen.get(player);
    }

    public void del(UUID player) {
        frozen.remove(player);
    }
}
