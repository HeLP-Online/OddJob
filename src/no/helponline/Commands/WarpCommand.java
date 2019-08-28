package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("addwarp")) {
            if (commandSender.hasPermission("addwarp") && commandSender instanceof Player && strings.length >= 1) {
                Player player = (Player) commandSender;
                String password = "";
                if (strings.length == 2) {
                    password = strings[1];
                }
                if (!OddJob.getInstance().getWarpManager().exists(strings[0])) {
                    OddJob.getInstance().getWarpManager().addWarp(player, strings[0], password);
                } else {
                    OddJob.getInstance().getMessageManager().danger("Sorry, name " + strings[0] + "already exists.", commandSender);
                }
            }
        }
        if (command.getName().equalsIgnoreCase("delwarp")) {
            if (commandSender.hasPermission("delwarp")) {
                String password = "";
                if (strings.length == 2) {
                    password = strings[1];
                }
                if (OddJob.getInstance().getWarpManager().exists(strings[0])) {
                    OddJob.getInstance().getWarpManager().delWarp(strings[0], password);
                } else {
                    OddJob.getInstance().getMessageManager().danger("Sorry, can't find " + strings[0] + ", or the password is wrong.", commandSender);
                }
            }
        }
        if (command.getName().equalsIgnoreCase("warp")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (strings.length >= 1) {
                    if (strings.length == 2) {
                        OddJob.getInstance().getWarpManager().warp(player, strings[0], strings[1]);
                    } else {
                        OddJob.getInstance().getWarpManager().warp(player, strings[0]);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
