package no.helponline.Managers;

import no.helponline.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {
    private HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();

    public void updatePlayer(UUID uuid, String name) {
        if (oddPlayers.containsKey(uuid)) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            oddPlayer.setName(name);
            oddPlayers.put(uuid, oddPlayer);
        } else {
            create(uuid, Bukkit.getPlayer(uuid).getName(), false, null, null);
        }
    }

    public String getName(UUID uuid) {
        return oddPlayers.get(uuid).getName();
    }

    public UUID getUUID(String name) {
        for (UUID uuid : oddPlayers.keySet()) {
            if ((oddPlayers.get(uuid).getName()).equalsIgnoreCase(name)) {
                return uuid;
            }
        }
        return null;
    }

    public HashMap<UUID, OddPlayer> getPlayersMap() {
        return oddPlayers;
    }

    public Set<UUID> getUUIDs() {
        return oddPlayers.keySet();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public OfflinePlayer getOffPlayer(UUID uniqueId) {
        return Bukkit.getServer().getOfflinePlayer(uniqueId);
    }

    public OddPlayer getOddPlayer(UUID to) {
        return oddPlayers.get(to);
    }

    public void create(UUID uuid, String name, boolean denyTPA, List<String> whiteList, List<String> blackList) {
        OddPlayer oddPlayer = new OddPlayer(uuid, name, denyTPA, whiteList, blackList);
    }
}
