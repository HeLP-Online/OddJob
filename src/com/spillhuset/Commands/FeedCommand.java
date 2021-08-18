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

public class FeedCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean onlyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean onlyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.feed;
    }

    @Override
    public String getPermission() {
        return "feed";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (checkArgs(0, 1, args, sender, getPlugin())) {
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

        if (can(sender,false)){
            target.setFoodLevel(20);
        }else {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(),sender);
        }
        if (!sender.equals(target)) {
            OddJob.getInstance().getMessageManager().feedPlayer(target.getName(), sender);
        }
        OddJob.getInstance().getMessageManager().feedTarget(target.getUniqueId());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (args.length == 1) {
                if (args[0].isEmpty() || player.getName().startsWith(args[0])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }

    public String getSyntax() {
        return "/feed [name]";
    }
}
