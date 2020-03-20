package no.helponline.Utils;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class Home {
    UUID owner;
    HashMap<String, Location> homes = new HashMap<>();

    public Home(UUID uuid, Location location, String name) {
        this.owner = uuid;
        homes.put(name, location);
    }

    public Home(UUID owner) {
        this.owner = owner;
    }

    public Location get(String name) {
        return homes.get(name);
    }

    public void add(String name, Location location) {
        homes.put(name, location);
    }

    public void remove(String name) {
        homes.remove(name);
    }

    public Set<String> list() {
        return homes.keySet();
    }

    public boolean has(String name) {
        return homes.containsKey(name);
    }
}
