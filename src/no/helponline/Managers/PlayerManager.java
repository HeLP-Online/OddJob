package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private static HashMap<UUID, String> players = new HashMap<>();

    public static void updatePlayer(UUID uuid, String name) {
        players.put(uuid, name);
    }

    public static String getName(UUID uuid) {
        return players.get(uuid);
    }

    public static UUID getUUID(String name) {
        for (UUID uuid : players.keySet()) {
            if ((players.get(uuid)).equalsIgnoreCase(name)) {
                return uuid;
            }
        }
        return null;
    }

    public static HashMap<UUID, String> getPlayersMap() {
        return players;
    }

    public static Set<UUID> getUUIDs() {
        return players.keySet();
    }

    public static Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }
}
