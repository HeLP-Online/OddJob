package no.helponline.Managers;

import no.helponline.OddJob;
import no.helponline.Utils.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Set;

public class WarpManager {

    HashMap<String, Warp> warps = new HashMap<>();

    private final double cost = OddJob.getInstance().getEconManager().cost("warp_use");

    public Set<String> listWarps() {
        return warps.keySet();
    }

    public void add(Player player, String name, String password) {

        if (has(name)) {
            OddJob.getInstance().getMessageManager().danger("A warp with this name already exists.", player, false);
        }

        warps.put(name, new Warp(name, player.getLocation(), password,0D));
        OddJob.getInstance().getMySQLManager().createWarp(name, player, password);

        OddJob.getInstance().getMessageManager().success("Successfully created " + ChatColor.AQUA + name, player, true);

    }

    public void add(Player player, String name) {
        add(player, name, "");
    }

    public void del(CommandSender commandSender, String name, String password) {
        if (has(name, password)) {
            warps.remove(name);
            OddJob.getInstance().getMySQLManager().deleteWarp(name, password);
            OddJob.getInstance().getMessageManager().success("Successfully deleted " + ChatColor.AQUA + name, commandSender, true);
        } else {
            OddJob.getInstance().getMessageManager().danger("A warp with this name does not exists or you used wrong password.", commandSender, false);
        }
    }

    public void del(CommandSender commandSender, String name) {
        del(commandSender, name, "");
    }

    public void pass(Player player, String name, String password) {

        Location location = warps.get(name).get();
        if (has(name, password)) {
            OddJob.getInstance().getTeleportManager().teleport(player, location, cost, PlayerTeleportEvent.TeleportCause.COMMAND);
        } else {
            OddJob.getInstance().getMessageManager().danger("A warp with this name does not exists or you used wrong password.", player, false);
        }

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
        OddJob.getInstance().getMessageManager().infoListWarps("List of Warps",warps,player);
    }

    public void help(CommandSender commandSender) {
    }

    public void pass(Player player, String string) {
        pass(player,string,"");
    }
    public void load() {
        warps = OddJob.getInstance().getMySQLManager().loadWarps();
    }
    public void save() {
        OddJob.getInstance().getMySQLManager().saveWarps(warps);
    }

    public Warp get(String name) {
        return warps.get(name);
    }
}
