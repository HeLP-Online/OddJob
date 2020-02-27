package no.helponline.Managers;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class WarpManager {

    private final double cost = OddJob.getInstance().getEconManager().cost("warp_use");

    public List<String> listWarps() {
        return OddJob.getInstance().getMySQLManager().listWarps();
    }

    public void add(CommandSender commandSender, String name, String password) {
        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().danger("Only usable as a player.", commandSender, false);
        } else {
            Player player = (Player) commandSender;
            if (!exists(name, password)) {
                OddJob.getInstance().getMySQLManager().addWarp(name, player, password);
                if (exists(name, password)) {
                    OddJob.getInstance().getMessageManager().success("Successfully created " + ChatColor.AQUA + name, commandSender, true);
                } else {
                    OddJob.getInstance().getMessageManager().danger("Something went wrong when adding warp.", commandSender, true);
                }
            } else {
                OddJob.getInstance().getMessageManager().danger("A warp with this name already exists.", commandSender, false);
            }
        }
    }

    public void add(CommandSender commandSender, String name) {
        add(commandSender, name, "");
    }

    public void del(CommandSender commandSender, String name, String password) {
        if (exists(name, password)) {
            OddJob.getInstance().getMySQLManager().deleteWarp(name, password);
            if (exists(name, password)) {
                OddJob.getInstance().getMessageManager().danger("Something went wrong when deleting warp.", commandSender, true);
            } else {
                OddJob.getInstance().getMessageManager().success("Successfully deleted " + ChatColor.AQUA + name, commandSender, true);
            }

        } else {
            OddJob.getInstance().getMessageManager().danger("A warp with this name does not exists or you used wrong password.", commandSender, false);
        }
    }

    public void del(CommandSender commandSender, String name) {
        del(commandSender, name, "");
    }

    public void pass(CommandSender commandSender, String name, String password) {
        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().danger("Only usable as a player.", commandSender, false);
        } else {
            Player player = (Player) commandSender;
            Location location = OddJob.getInstance().getMySQLManager().getWarp(name, password);
            if (exists(name, password)) {
                //TODO Permission
                OddJob.getInstance().getTeleportManager().teleport(player, OddJob.getInstance().getMySQLManager().getWarp(name, password), cost, PlayerTeleportEvent.TeleportCause.COMMAND);
            } else {
                OddJob.getInstance().getMessageManager().danger("A warp with this name does not exists or you used wrong password.", commandSender, false);
            }
        }
    }

    public boolean exists(String name, String password) {
        Location location = OddJob.getInstance().getMySQLManager().getWarp(name, password);
        return location != null;
    }

    public void pass(CommandSender commandSender, String name) {
        pass(commandSender, name, "");
    }

    public void list(CommandSender commandSender) {
        StringBuilder sb = new StringBuilder();
        List<String> list = OddJob.getInstance().getMySQLManager().listWarps();
        int i = 1;
        for (String s : list) {
            sb.append(i).append(".) ").append(ChatColor.GOLD).append(s).append(ChatColor.RESET).append("\n");
            i++;
        }
        commandSender.sendMessage(sb.toString());
    }

    public void help(CommandSender commandSender) {
    }
}
