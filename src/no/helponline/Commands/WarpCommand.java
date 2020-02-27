package no.helponline.Commands;

import no.helponline.OddJob;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("warp")) {
            if (strings.length > 1 && strings[0].equalsIgnoreCase("add")) {
                if (strings.length == 3) {
                    OddJob.getInstance().getWarpManager().add(commandSender, strings[1], strings[2]);
                } else if (strings.length == 2) {
                    OddJob.getInstance().getWarpManager().add(commandSender, strings[1]);
                }
            } else if (strings.length > 1 && strings[0].equalsIgnoreCase("del")) {
                if (strings.length == 3) {
                    OddJob.getInstance().getWarpManager().del(commandSender, strings[1], strings[2]);
                } else if (strings.length == 2) {
                    OddJob.getInstance().getWarpManager().del(commandSender, strings[1]);
                }
            } else if (strings.length > 1 && strings[0].equalsIgnoreCase("goto")) {
                if (strings.length == 3) {
                    OddJob.getInstance().getWarpManager().pass(commandSender, strings[1], strings[2]);
                } else if (strings.length == 2) {
                    OddJob.getInstance().getWarpManager().pass(commandSender, strings[1]);
                }
            } else if (strings.length == 1 && strings[0].equalsIgnoreCase("list")) {
                OddJob.getInstance().getWarpManager().list(commandSender);
            } else {
                OddJob.getInstance().getWarpManager().help(commandSender);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        String[] sub = {"add", "del", "list", "goto"};
        if (strings.length == 2 && (strings[0].equalsIgnoreCase("del") || strings[0].equalsIgnoreCase("goto"))) {
            // permission?
            list.addAll(OddJob.getInstance().getMySQLManager().listWarps());
        } else if (strings.length == 1) {
            for (String ss : sub) {
                if (ss.startsWith(strings[0])) {
                    list.add(ss);
                }
            }
        } else if (strings.length == 0) {
            list.addAll(Arrays.asList(sub));
        }
        return list;
    }
}
