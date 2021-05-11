package com.spillhuset.Commands;

import com.spillhuset.OddJob;
import com.spillhuset.Utils.Enum.Plugin;
import com.spillhuset.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HealCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean allowOp() {
        return true;
    }

    @Override
    public boolean allowConsole() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.heal;
    }

    @Override
    public String getPermission() {
        return "heal";
    }

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
                OddJob.getInstance().getMessageManager().errorConsole(getPlugin());
                return true;
            }
            target = (Player) sender;
        }

        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }


        if (can(sender, false)) {
            target.setHealth(20);
        } else {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return true;
        }

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

}
