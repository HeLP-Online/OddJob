package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();

    public void updatePlayer(UUID uuid, String name) {
        OddJob.getInstance().getMySQLManager().updatePlayer(uuid, name);
        /*if (oddPlayers.containsKey(uuid)) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            oddPlayer.setName(name);
            oddPlayers.put(uuid, oddPlayer);
        } else {
            create(uuid, Bukkit.getPlayer(uuid).getName(), false, new ArrayList<>(), new ArrayList<>());
            OddJob.getInstance().log("created " + uuid.toString());
        }*/
    }

    public String getName(UUID uuid) {
        return OddJob.getInstance().getMySQLManager().getPlayerName(uuid);
        //return oddPlayers.get(uuid).getName();
    }

    public UUID getUUID(String name) {

        UUID uuid = OddJob.getInstance().getMySQLManager().getPlayerUUID(name);
        if (uuid == null) {
            for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                if (op.getName().equalsIgnoreCase(name)) {
                    uuid = op.getUniqueId();
                }
            }
        }
        return uuid;
    }

    public List<UUID> getUUIDs() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapUUIDs();
    }

    public Player getPlayer(UUID uniqueId) {
        return Bukkit.getServer().getPlayer(uniqueId);
    }

    public OfflinePlayer getOffPlayer(UUID uniqueId) {
        return Bukkit.getServer().getOfflinePlayer(uniqueId);
    }

    public HashMap<String, Object> getOddPlayer(UUID uniqueId) {
        return OddJob.getInstance().getMySQLManager().getPlayer(uniqueId);
    }

    public boolean request(UUID to, UUID from) {
        HashMap<String, Object> teleportTo = getOddPlayer(to);
        OddJob.getInstance().log("request: " + (teleportTo.get("denytpa")));
        boolean request = (boolean) teleportTo.get("denytpa");
        if (((List) teleportTo.get("whitelist")).contains(from)) {
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (((List) teleportTo.get("blacklist")).contains(from)) {
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if ((boolean) teleportTo.get("denytpa")) {
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(teleportTo.get("name") + " is denying all request!", from);
            request = false;
        }
        return request;
    }

    public List<String> getNames() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapNames();
    }
}
