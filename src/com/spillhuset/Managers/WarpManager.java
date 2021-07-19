package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.WarpSQL;
import com.spillhuset.Utils.Enum.Types;
import com.spillhuset.Utils.Warp;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class WarpManager {

    private HashMap<UUID, Warp> warps = new HashMap<>();

    public HashMap<UUID, Warp> listWarps() {
        return warps;
    }

    /**
     * Adds a new Warp
     *
     * @param player Player to get the Location from
     * @param name   String name of the Warp
     */
    public void add(Player player, String name) {
        add(player, name, "");
    }

    /**
     * Adds a new Warp with password
     *
     * @param player   Player to get Location from
     * @param name     String name of the Warp
     * @param password String password for the Warp
     */
    public void add(Player player, String name, String password) {
        add(player, name, password, 0D);
    }

    /**
     * Adds a new Warp with cost
     *
     * @param player Player to get Location from
     * @param name   String name of the Warp
     * @param cost   Double value subtracted when use of the Warp
     */
    public void add(Player player, String name, double cost) {
        add(player, name, "", cost);
    }

    /**
     * Adds a new Warp with password and cost
     *
     * @param player   Player to get Location from
     * @param name     String name of the Warp
     * @param password String password for the Warp
     * @param cost     Double value subtracted when use of the Warp
     */
    public void add(Player player, String name, String password, double cost) {
        UUID uuid = UUID.randomUUID();
        warps.put(uuid, new Warp(name, player.getLocation(), password, cost, uuid));
        WarpSQL.add(name, player, password, cost, uuid);
        OddJob.getInstance().getMessageManager().successWarpAdded(name, player);
    }

    /**
     * Deletes a given Warp
     *
     * @param sender   CommandSender
     * @param uuid     UUID of the Warp
     * @param password String password for the Warp
     */
    public void del(CommandSender sender, UUID uuid, String password) {
        String name = get(uuid).getName();
        if (WarpSQL.del(uuid, password)) {
            warps.remove(uuid);
            OddJob.getInstance().getMessageManager().successWarpDeleted(name, sender);
            return;
        }
        OddJob.getInstance().getMessageManager().warpWrongPassword(name, sender);
    }

    /**
     * Deletes a given Warp
     *
     * @param sender CommandSender
     * @param uuid   UUID of the Warp
     */
    public void del(CommandSender sender, UUID uuid) {
        del(sender, uuid, "");
    }

    /**
     * Using a warp
     *
     * @param player   Player to warp
     * @param uuid     UUID of the Warp
     * @param password String password to the Warp
     */
    public void pass(Player player, UUID uuid, String password) {
        Warp warp = warps.get(uuid);
        Location location = warp.getLocation();
        double cost = warp.getCost();

        if (!password.equals(warp.getPassword())) {
            OddJob.getInstance().getMessageManager().warpWrongPassword(warp.getName(), player);
            return;
        }

        if (OddJob.getInstance().getConfig().getBoolean("enabled.currency.pocket", true)) {
            if (warp.getCost() > 0d) {
                if (OddJob.getInstance().getCurrencyManager().get(player.getUniqueId()).get(Types.AccountType.pocket) >= cost) {
                    OddJob.getInstance().getCurrencyManager().subtract(player.getUniqueId(), cost, player.hasPermission("currency.negative"), Types.AccountType.pocket);
                } else {
                    OddJob.getInstance().getMessageManager().insufficientFunds(player);
                    return;
                }
            }
        }

        OddJob.getInstance().getTeleportManager().teleport(player, location, PlayerTeleportEvent.TeleportCause.COMMAND, true);
    }

    /**
     * Finding the UUID of the Warp
     *
     * @param name Name of the warp
     * @return UUID of the warp, null if not found
     */
    public UUID getUUID(String name) {
        for (UUID uuid : warps.keySet()) {
            if (warps.get(uuid).getName().equalsIgnoreCase(name)) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Using a Warp without password
     *
     * @param player Player to warp
     * @param uuid   UUID of Warp
     */
    public void pass(Player player, UUID uuid) {
        pass(player, uuid, "");
    }

    public void load() {
        warps = WarpSQL.loadWarps();
    }

    public void save() {
        WarpSQL.saveWarps(warps);
    }

    /**
     * Returns a Warp
     *
     * @param uuid UUID of the Warp
     * @return Warp | null if not found
     */
    public Warp get(UUID uuid) {
        return warps.get(uuid);
    }

    /**
     * Sets a new Location for the Warp
     *
     * @param uuid     UUID of the warp
     * @param password Password for the warp
     * @param location   Location to set
     * @return Boolean | true if successful
     */
    public boolean setLocation(UUID uuid, String password, Location location) {
        Warp warp = get(uuid);
        boolean a = false;
        if (warp.getPassword().equals(password)) {
            warp.setLocation(location);
            WarpSQL.saveWarps(warps);
            a = true;
        }
        return a;
    }

    /**
     * Set a new cost for the Warp
     *
     * @param uuid     UUID of the Warp
     * @param cost     Double value to use the Warp
     * @param password String password to access the Warp
     * @return Boolean | true if success
     */
    public boolean setCost(UUID uuid, double cost, String password) {
        Warp warp = get(uuid);
        boolean a = false;
        if (warp.getPassword().equals(password)) {
            warp.setCost(cost);
            a = true;
        }
        return a;
    }

    /**
     * Sets a new password for the Warp
     *
     * @param uuid UUID of the Warp
     * @param n    String the new password
     * @param o    String the old password
     * @return Boolean | true if success
     */
    public boolean setPasswd(UUID uuid, String n, String o) {
        boolean a = false;
        Warp warp = get(uuid);
        if (warp.getPassword().equals(o)) {
            warp.setPasswd(n);
            a = true;
        }
        return a;
    }

    /**
     * Sets a new name to the Warp
     *
     * @param password String password to access the Warp
     * @param newName  String new name to the Warp
     * @param uuid     UUID of the Waro
     * @return Boolean | true if success
     */
    public boolean setName(String password, String newName, UUID uuid) {
        boolean a = false;
        Warp warp = get(uuid);
        if (warp.getPassword().equals(password)) {
            warp.setName(newName);
            warps.put(uuid, warp);
            a = true;
        }

        return a;
    }

    /**
     * Returns the HashMap of all Warps
     *
     * @return HashMap of all Warps
     */
    public HashMap<UUID, Warp> getAll() {
        return warps;
    }
}
