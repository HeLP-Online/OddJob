package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand extends CommandCompleter implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (checkArgs(0, 1, args, sender, Plugin.heal)) {
            return true;
        }

        Player target;
        if (args.length == 1) {
            target = Bukkit.getPlayer(args[0]);
        } else {
            if (!(sender instanceof Player)) {
                OddJob.getInstance().getMessageManager().errorConsole(Plugin.heal);
                return true;
            }
            target = (Player) sender;
        }

        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(Plugin.feed, args[0], sender);
            return true;
        }

        target.setHealth(20);
        if (!sender.equals(target)) {
            OddJob.getInstance().getMessageManager().healPlayer(target.getName(), sender);
        }
        OddJob.getInstance().getMessageManager().healTarget(target.getUniqueId());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (args.length == 1) {
                if (player.getName().startsWith(args[0])) {
                    list.add(player.getName());
                }
            } else if (args.length == 0) {
                list.add(player.getName());
            }
        }
        return list;
    }

    @Override
    public String getSyntax() {
        return "/heal [name]";
    }
}
