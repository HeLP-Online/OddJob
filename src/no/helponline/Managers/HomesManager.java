package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Enum.Zone;
import no.helponline.Utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class HomesManager {
    /**
     * HashMap containing the UUID of the Player and their Homes
     */
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
            homes.put(uuid, new Home(uuid, location, name));
        }
        OddJob.getInstance().getMessageManager().homesSetSuccess(name,uuid);
        return true;
    }

    /**
     * Deletes a given Home from a given Player
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     */
    public void del(UUID uuid, String name) {
        if (homes.containsKey(uuid)) {
            Home home = homes.get(uuid);
            home.remove(name);
        }
        OddJob.getInstance().getMySQLManager().deleteHome(uuid, name);
        OddJob.getInstance().getMessageManager().homesDelSuccess(name,uuid);
    }

    /**
     * Returns the Location of a named Home of a given Player
     *
     * @param uuid UUID of the Player
     * @param name String name of the Home
     * @return Location of the Home
     */
    public Location get(UUID uuid, String name) {
        if (!homes.containsKey(uuid)) homes.put(uuid, new Home(uuid));
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
     */
    public Set<String> getList(UUID uuid) {
        if (!homes.containsKey(uuid)) homes.put(uuid, new Home(uuid));
        return homes.get(uuid).list();
    }

    /**
     * Returns a List of the Homes of a Player
     *
     * @param uuid UUID of the Player
     */
    public void list(UUID uuid) {
        OddJob.getInstance().getMessageManager().listHomes("List of Homes", getList(uuid), Bukkit.getPlayer(uuid));
    }

    /**
     * Loading Homes from the Database
     */
    public void load() {
        homes = OddJob.getInstance().getMySQLManager().loadHomes();
    }

    /**
     * Saves Homes to the Database
     */
    public void save() {
        OddJob.getInstance().getMySQLManager().saveHomes(homes);
    }

    public void teleport(Player player,UUID uuid, String string) {
        for (String name : getList(uuid)) {
            if (name.equalsIgnoreCase(string)) {
                OddJob.getInstance().getMessageManager().homesTeleportSuccess(player.getUniqueId(),name);
                OddJob.getInstance().getTeleportManager().teleport(player,get(uuid,name),0, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        }
    }
}
