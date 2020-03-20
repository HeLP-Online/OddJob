package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

        if (!(commandSender instanceof Player)) {
            OddJob.getInstance().getMessageManager().errorConsole();
            return true;
        }

        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("warp")) {
            switch (strings[0].toLowerCase()) {
                // Command /warp add <name> [password]
                case "add":
                    if (strings.length == 2)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().add(player, strings[1]);
                    else if (strings.length == 3)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().add(player, strings[1], strings[2]);
                    else
                        OddJob.getInstance().getMessageManager().danger("Invalid number of arguments.", player, false);

                    break;
                // Command /warp del <name> [password]
                case "del":
                    if (strings.length == 2)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().del(player, strings[1]);
                    else if (strings.length == 3)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().del(player, strings[1], strings[2]);
                    else
                        OddJob.getInstance().getMessageManager().danger("Invalid number of arguments.", player, false);
                    break;
                // Command /warp list
                case "list":
                    OddJob.getInstance().getWarpManager().list(player);
                    break;
                // Command /warp help
                case "help":
                    break;
                // Command /warp <name> [password]
                default:
                    if (strings.length == 1)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().pass(player, strings[0]);
                    else if (strings.length == 2)
                        // TODO : Cost
                        // TODO : Permission
                        OddJob.getInstance().getWarpManager().pass(player, strings[0], strings[1]);
                    else
                        OddJob.getInstance().getMessageManager().danger("Invalid number of arguments.", player, false);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        String[] sub = {"add", "del", "list", "goto"};
        if (strings.length == 1) {
            for (String st : sub) {
                if (st.startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                    list.add(st);
                }
            }

            for (String name : OddJob.getInstance().getWarpManager().listWarps()) {
                if (name.startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                    String astrix = (OddJob.getInstance().getWarpManager().get(name).hasPassword() ? "*" : "");
                    ChatColor color = (commandSender.hasPermission("oddjob.warp." + name) ? ChatColor.GREEN : ChatColor.RED);
                    list.add(color + name + astrix);
                }
            }
        } else if (strings.length == 2 && strings[0].equalsIgnoreCase("del")) {
            for (String name : OddJob.getInstance().getWarpManager().listWarps()) {
                if (name.startsWith(strings[0].toLowerCase()) || strings[0].isEmpty()) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
