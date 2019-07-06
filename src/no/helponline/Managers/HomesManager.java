package no.helponline.Managers;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HomesManager {
    private HashMap<UUID, HashMap<String, Location>> homes = new HashMap<>();

    public boolean add(UUID uuid, Location location) {
        return add(uuid, "home", location);
    }

    public boolean del(UUID uuid) {
        return del(uuid, "home");
    }

    public Location get(UUID uuid) {
        return get(uuid, "home");
    }

    public boolean has(UUID uuid) {
        return has(uuid, "home");
    }

    public boolean add(UUID uuid, String name, Location location) {
        HashMap<String, Location> loc = new HashMap<>();
        if (homes.containsKey(uuid)) {
            loc = homes.get(uuid);
        }
        loc.put(name, location);
        homes.put(uuid, loc);

        return true;
    }

    public boolean del(UUID uuid, String name) {
        HashMap<String, Location> loc = new HashMap<>();
        if (homes.containsKey(uuid)) {
            loc = homes.get(uuid);
        }
        if (loc.containsKey(name)) {
            loc.remove(name);
            homes.put(uuid, loc);
            return true;
        }
        return false;
    }

    public Location get(UUID uuid, String name) {
        HashMap<String, Location> loc = new HashMap<>();
        if (homes.containsKey(uuid)) {
            loc = homes.get(uuid);
        }
        return loc.get(name);
    }

    public boolean has(UUID uuid, String name) {
        HashMap<String, Location> loc;
        if (homes.containsKey(uuid)) {
            loc = homes.get(uuid);
            return loc.containsKey(name);
        }
        return false;
    }

    public Set<String> list(UUID uuid) {
        Set<String> list = null;
        HashMap<String, Location> loc;
        if (homes.containsKey(uuid)) {
            loc = homes.get(uuid);
            if (loc.size() >= 1) {
                list = loc.keySet();
            }
        }
        return list;
    }
}
