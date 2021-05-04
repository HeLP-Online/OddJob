package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.HomeSQL;
import com.spillhuset.Utils.Enum.Zone;
import com.spillhuset.Utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class HomesManager {
    private HashMap<UUID, Home> homes = new HashMap<>();

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

        if (homes.containsKey(uuid)) {
            homes.get(uuid).add(name, location);
        } else {
            Home home = new Home(uuid, location, name);
            homes.put(uuid, home);
        }


        HomeSQL.save(homes);

        OddJob.getInstance().getMessageManager().homesSetSuccess(name, uuid);
        return true;
    }

    /**
     * Deletes a given Home from a given Player
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     */
    public void del(UUID uuid, String name) {
        homes.get(uuid).remove(name);

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
        return homes.get(uuid).get(name);
    }

    /**
     * Checks is a given Player has a Home named with Name
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     * @return Home
     */
    public boolean has(UUID uuid, String name) {
        if (!homes.containsKey(uuid)) homes.put(uuid, new Home(uuid));
        return (get(uuid, name) != null);
    }

    /**
     * Returns a String List of the Home names of a Player
     *
     * @param uuid UUID of the Player
     * @return
     */
    public Set<String> getList(UUID uuid) {
        return homes.get(uuid).list();
    }

    /**
     * Returns a List of the Homes of a Player
     *
     * @param uuid UUID of the Player
     * @return
     */
    public List<String> list(UUID uuid) {
        OddJob.getInstance().getMessageManager().listHomes("List of Homes", getList(uuid), Bukkit.getPlayer(uuid));
        return null;
    }

    /**
     * Loading Homes from the Database
     */
    public void load() {
        homes = HomeSQL.load();
    }

    /**
     * Saves Homes to the Database
     */
    public void save() {
        HomeSQL.save(homes);
    }

    public void teleport(Player player, UUID uuid, String string) {
        for (String name : getList(uuid)) {
            if (name.equalsIgnoreCase(string)) {
                OddJob.getInstance().getMessageManager().homesTeleportSuccess(player.getUniqueId(), name);
                OddJob.getInstance().getTeleportManager().teleport(player, get(uuid, name), PlayerTeleportEvent.TeleportCause.COMMAND,true);
            }
        }
    }
}
