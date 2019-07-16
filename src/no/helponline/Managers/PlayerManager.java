package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {
    private HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();

    public void updatePlayer(UUID uuid, String name) {
        if (oddPlayers.containsKey(uuid)) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            oddPlayer.setName(name);
            oddPlayers.put(uuid, oddPlayer);
        } else {
            create(uuid, Bukkit.getPlayer(uuid).getName(), false, new ArrayList<>(), new ArrayList<>());
            OddJob.getInstance().log("created " + uuid.toString());
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

    public Collection<OddPlayer> getPlayersMap() {
        return oddPlayers.values();
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

    public void create(UUID uuid, String name, boolean denyTPA, List<UUID> whiteList, List<UUID> blackList) {
        OddPlayer oddPlayer = new OddPlayer(uuid, name, denyTPA, whiteList, blackList);
        oddPlayers.put(uuid, oddPlayer);
    }

    public List<String> listPlayers() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : oddPlayers.keySet()) {
            list.add(oddPlayers.get(uuid).getName() + ":" + uuid.toString());
        }
        return list;
    }
}
