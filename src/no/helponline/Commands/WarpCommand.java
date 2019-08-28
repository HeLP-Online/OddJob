package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
                    OddJob.getInstance().getMessageManager().success("Warp " + strings[0] + " added.", commandSender);
                } else {
                    OddJob.getInstance().getMessageManager().danger("Sorry, name " + strings[0] + " already exists.", commandSender);
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
                    OddJob.getInstance().getMessageManager().success("Warp " + strings[0] + " deleted.", commandSender);
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
                        if (OddJob.getInstance().getWarpManager().warp(player, strings[0], strings[1]))
                            OddJob.getInstance().getMessageManager().success("Warped to " + strings[0] + ". Zoooom Zooooooooooom.", commandSender);
                    } else {
                        if (OddJob.getInstance().getWarpManager().warp(player, strings[0]))
                            OddJob.getInstance().getMessageManager().success("Warped to " + strings[0] + ". Zoooom Zooooooooooom.", commandSender);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("warp") || command.getName().equalsIgnoreCase("delwarp")) {
            if (strings.length == 0 || strings.length == 1) {
                List<String> l = OddJob.getInstance().getWarpManager().listWarps();
                if (strings.length == 1) {
                    for (String st : l) {
                        if (st.startsWith(strings[0])) {
                            list.add(st);
                        }
                    }
                }
            }
        }
        return list;
    }
}
