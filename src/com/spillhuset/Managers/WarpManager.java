package com.spillhuset.Managers;

import com.spillhuset.OddJob;
import com.spillhuset.SQL.WarpSQL;
import com.spillhuset.Utils.Warp;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WarpManager {

    private HashMap<UUID, Warp> warps = new HashMap<>();

    public HashMap<UUID, Warp> listWarps() {
        return warps;
    }

    public void add(Player player, String name) {
        add(player, name, "");
    }

    public void add(Player player, String name, String password) {
        add(player, name, password, 0D);
    }

    public void add(Player player, String name, String password, double cost) {
        UUID uuid = UUID.randomUUID();
        warps.put(uuid, new Warp(name, player.getLocation(), password, cost, uuid));
        WarpSQL.add(name, player, password, cost, uuid);
        OddJob.getInstance().getMessageManager().successWarpAdded(name, player);
    }

    public void del(CommandSender sender, UUID uuid, String password) {
        if (WarpSQL.del(uuid, password)) {
            warps.remove(uuid);
            OddJob.getInstance().getMessageManager().successWarpDeleted(warps.get(uuid).getName(), sender);
            return;
        }
        OddJob.getInstance().getMessageManager().warpWrongPassword(warps.get(uuid).getName(), sender);
    }


    public void del(CommandSender commandSender, UUID uuid) {
        del(commandSender, uuid, "");
    }

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
                if (OddJob.getInstance().getCurrencyManager().getPocketBalance(player.getUniqueId()) >= cost) {
                    OddJob.getInstance().getCurrencyManager().subtractPocketBalance(player.getUniqueId(), cost,player.hasPermission("currency.negative"));
                } else {
                    OddJob.getInstance().getMessageManager().insufficientFunds(player);
                    return;
                }
            }
        }

        OddJob.getInstance().getTeleportManager().teleport(player, location, PlayerTeleportEvent.TeleportCause.COMMAND,true);
    }

    /**
     * @param name Name of the warp
     * @return UUID of the warp, null if not found
     */
    public UUID getUUID(String name) {
        for (UUID uuid : warps.keySet()) {
            if (warps.get(uuid).getName().equals(name)) {
                return uuid;
            }
        }
        return null;
    }

    public boolean has(String name) {
        return warps.containsKey(name);
    }

    public boolean has(String name, String password) {
        if (warps.containsKey(name)) {
            return warps.get(name).pass(password);
        }
        return false;
    }

    public void list(Player player) {
        //OddJob.getInstance().getMessageManager().infoListWarps("List of Warps", listWarps(), player);
    }

    public void help(CommandSender commandSender) {
    }

    public void pass(Player player, UUID uuid) {
        pass(player, uuid, "");
    }

    public void load() {
        warps = WarpSQL.loadWarps();
    }

    public void save() {
        WarpSQL.saveWarps(warps);
    }

    public Warp get(UUID uuid) {
        return warps.get(uuid);
    }

    /**
     * @param uuid UUID of the warp
     * @param password Password for the warp
     * @param player Player to get location from
     * @return boolean | true if successful
     */
    public boolean setLocation(UUID uuid, String password, Player player) {
        Warp warp = get(uuid);
        boolean a = false;
        if (warp.getPassword().equals(password)) {
            warp.setLocation(player.getLocation());
            a = true;
        }
        return a;
    }

    public boolean setCost(UUID uuid, double cost, String password) {
        Warp warp = get(uuid);
        boolean a = false;
        if (warp.getPassword().equals(password)) {
            warp.setCost(cost);
            a = true;
        }
        return a;
    }

    public boolean setPasswd(UUID uuid, String n, String o) {
        boolean a = false;
        Warp warp = get(uuid);
        if (warp.getPassword().equals(o)) {
            warp.setPasswd(n);
            a = true;
        }
        return a;
    }

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

    public HashMap<UUID,Warp> getAll() {
        return warps;
    }
}
