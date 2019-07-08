package no.helponline.Managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private HashMap<UUID, String> players = new HashMap<>();

    public void updatePlayer(UUID uuid, String name) {
        players.put(uuid, name);
    }

    public String getName(UUID uuid) {
        return players.get(uuid);
    }

    public UUID getUUID(String name) {
        for (UUID uuid : players.keySet()) {
            if ((players.get(uuid)).equalsIgnoreCase(name)) {
                return uuid;
            }
        }
        return null;
    }

    public HashMap<UUID, String> getPlayersMap() {
        return players;
    }

    public Set<UUID> getUUIDs() {
        return players.keySet();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public OfflinePlayer getOffPlayer(UUID uniqueId) {
        return Bukkit.getServer().getOfflinePlayer(uniqueId);
    }
}
