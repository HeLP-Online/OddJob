package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {

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
        boolean request = OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(to); // false
        if (OddJob.getInstance().getMySQLManager().getPlayerWhiteList(to).contains(from)) { //false
            OddJob.getInstance().log("whitelist");
            request = true;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerBlackList(to).contains(from)) { // false
            OddJob.getInstance().log("blacklist");
            request = false;
        } else if (OddJob.getInstance().getMySQLManager().getPlayerDenyTpa(to)) { // false
            OddJob.getInstance().log("tpa deny");
            OddJob.getInstance().getMessageManager().warning(getName(to) + " is denying all request!", from);
            request = false;
        }
        OddJob.getInstance().log("request : deny");
        return !request;
    }

    public List<String> getNames() {
        return OddJob.getInstance().getMySQLManager().getPlayerMapNames();
    }

    public GameMode getGamemode(Player player, World world) {
        return OddJob.getInstance().getMySQLManager().getPlayerMode(player, world);
    }

    public void setGameMode(Player player, GameMode gameMode) {
        OddJob.getInstance().getMySQLManager().setGameMode(player, gameMode);
        player.setGameMode(gameMode);
        player.sendMessage("Your GameMode is set to " + gameMode.name() + " in " + player.getWorld().getName());
    }
}
