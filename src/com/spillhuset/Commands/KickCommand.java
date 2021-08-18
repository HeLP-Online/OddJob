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

public class KickCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {

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
        return Plugin.kick;
    }

    @Override
    public String getPermission() {
        return "kick";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        StringBuilder message = new StringBuilder();
        if (checkArgs(1, 0, args, sender, getPlugin())) {
            return true;
        }

        Player target = OddJob.getInstance().getPlayerManager().getPlayer(OddJob.getInstance().getPlayerManager().getUUID(args[0]));
        if (target == null) {
            OddJob.getInstance().getMessageManager().errorPlayer(getPlugin(), args[0], sender);
            return true;
        }
        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            message.deleteCharAt(message.lastIndexOf(" "));
        } else {
            message.append(OddJob.getInstance().getConfig().getString("default.kick_message", "kicked"));
        }

        if (!can(sender, false)) {
            OddJob.getInstance().getMessageManager().permissionDenied(getPlugin(), sender);
            return true;
        }
        OddJob.getInstance().getBanManager().kick(target, message.toString());
        OddJob.getInstance().getMessageManager().kickPlayer(target.getName(), message.toString(), sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("admin")) {
                    if (args[0].isEmpty() || player.getName().startsWith(args[0])) {
                        list.add(player.getName());
                    }
                }
            }
        } else if (args.length >= 2) {
            list.add("<message>");
        }
        return list;
    }
}
