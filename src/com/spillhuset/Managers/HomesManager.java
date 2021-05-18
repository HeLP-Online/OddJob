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
     *
     * @param uuid     UUID of the Player
     * @param name     String name of the Home
     * @param location Location to be set
     * @return boolean successful or not
     */
    public boolean add(UUID uuid, String name, Location location) {
        UUID guild = OddJob.getInstance().getGuildManager().getGuildUUIDByChunk(location.getChunk());
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
            HomeSQL.add(uuid, name, location);
            OddJob.getInstance().getMessageManager().homesSetSuccess(name, uuid);
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
     * @return
     */
    public List<String> getList(UUID player) {
        /*
        if (homes.isEmpty()) return null;
        return homes.get(uuid).list();*/

        return HomeSQL.getList(player);
    }

    /**
     * Returns a List of the Homes of a Player
     *
     * @param uuid   UUID of the Player
     * @param sender
     * @return
     */
    public void list(UUID uuid, CommandSender sender) {
        OddJob.getInstance().getMessageManager().homesCount(getList(uuid),OddJob.getInstance().getPlayerManager().getName(uuid), getMaxHomes(uuid), sender,false);
    }

    public int getMaxHomes(UUID uuid) {
        int i = OddJob.getInstance().getConfig().getInt("default.player.maxHomes");
        i += ConfigManager.maxHomes(uuid);
        i += OddJob.getInstance().getPlayerManager().getMaxHomes(uuid);
        return i;
    }

    public void teleport(Player player, UUID uuid, String string) {
        for (String name : getList(uuid)) {
            if (name.equalsIgnoreCase(string)) {
                OddJob.getInstance().getMessageManager().homesTeleportSuccess(player.getUniqueId(), name);
                OddJob.getInstance().getTeleportManager().teleport(player, get(uuid, name), PlayerTeleportEvent.TeleportCause.COMMAND, !player.hasPermission("teleport.now"));
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