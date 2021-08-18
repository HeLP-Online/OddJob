package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.HomeSQL;
import com.spillhuset.Utils.Enum.Zone;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.UUID;

public class HomesManager {
    //private HashMap<UUID, Home> homes = new HashMap<>();

    /**
     * Adds a given Home to the collection of a given Player
     * Chunk must be WILD or your own guild!
     *
     * @param uuid     UUID of the Player
     * @param name     String name of the Home
     * @param location Location to be set
     * @return boolean successful or not
     */
    public boolean add(UUID uuid, String name, Location location) {
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(location.getChunk());
        // Chunk is owned by a guild, you are not a member in the guild, the guild is not WILD
        if (guild != null &&
                !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByMember(uuid)) &&
                !guild.equals(OddJob.getInstance().getGuildManager().getGuildUUIDByZone(Zone.WILD))) {
            OddJob.getInstance().getMessageManager().homesInsideGuild(uuid);
            return true;
        }

        List<String> names = getList(uuid);
        if (names.contains(name)) {
            HomeSQL.change(uuid, name, location);
            OddJob.getInstance().getMessageManager().homesChangedSuccess(name, uuid);
        } else {
            if (CostManager.cost(uuid, "homes.set")) {
                HomeSQL.add(uuid, name, location);
                OddJob.getInstance().getMessageManager().homesSetSuccess(name, uuid);
            }
        }

        return true;
    }

    /**
     * Deletes a given Home from a given Player
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     */
    public void del(UUID uuid, String name) {
        HomeSQL.delete(uuid, name);
        OddJob.getInstance().getMessageManager().homesDelSuccess(name, uuid);
    }

    /**
     * Returns the Location of a named Home of a given Player
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     * @return Location of the Home
     */
    public Location get(UUID uuid, String name) {
        return HomeSQL.get(uuid, name);
    }

    /**
     * Returns a String List of the Home names of a Player
     *
     * @param player UUID of the Player
     * @return List of home names
     */
    public List<String> getList(UUID player) {
        return HomeSQL.getList(player);
    }

    /**
     * Returns a List of the Homes of a Player
     *
     * @param uuid   UUID of the Player
     * @param sender CommandSender
     */
    public void list(UUID uuid, CommandSender sender) {
        OddJob.getInstance().getMessageManager().homesCount(getList(uuid), OddJob.getInstance().getPlayerManager().getName(uuid), getMax(uuid), sender, false);
    }

    /**
     * Returns the max homes can be set by player
     *
     * @param uuid UUID of player
     * @return int max homes
     */
    public int getMax(UUID uuid) {
        // max homes from config -> default -> player
        int config = OddJob.getInstance().getConfig().getInt("homes.max");
        OddJob.getInstance().log("config: " + config);
        // max homes from permissions -> group
        int permission = ConfigManager.maxHomes(uuid);
        OddJob.getInstance().log("permission: " + permission);
        // max homes from granted -> player
        int player = OddJob.getInstance().getPlayerManager().getMaxHomes(uuid);
        OddJob.getInstance().log("player: " + player);

        return config + permission + player;
    }

    public void teleport(Player player, UUID uuid, String string) {
        for (String name : getList(uuid)) {
            if (name.equalsIgnoreCase(string)) {
                if ((!player.isOp() || player.hasPermission("homes.free")) && CostManager.cost(player.getUniqueId(), "homes.teleport")) {
                    OddJob.getInstance().getTeleportManager().teleport(player, get(uuid, name), PlayerTeleportEvent.TeleportCause.COMMAND, true);
                    return;
                }
            }
        }
    }
}
/**
 * Permissions:
 * homes.plenty
 * homes.others
 * <p>
 * Config:
 * homes.max
 * homes.cost
 * <p>
 * SQL:
 * homes.name
 * homes.x
 * homes.y
 * homes.z
 * homes.world
 * homes.yaw
 * homes.pitch
 * <p>
 * Commands:
 * /homes
 * /homes set <name>
 * /homes del <name>
 * /homes list
 * /homes buy
 * <p>
 * player -> max + number of homes
 */